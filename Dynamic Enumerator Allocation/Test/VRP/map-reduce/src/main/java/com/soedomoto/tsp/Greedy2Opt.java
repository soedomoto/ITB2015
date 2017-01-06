package com.soedomoto.tsp;

import com.soedomoto.clustering.DistanceFunction;
import com.soedomoto.clustering.model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soedomoto on 30/12/16.
 */
public class Greedy2Opt extends AbstractTspAlgorithm implements TspAlgorithm {
    public Greedy2Opt(DistanceFunction distanceFunction) {
        super(distanceFunction);
    }

    @Override
    public void solve(Point depot, List<Point> path) {
        // Add depot to be first path
        List<Point> tmp = new ArrayList<>(path.size()+1);
        tmp.add(depot);
        for(Point p: path) tmp.add(p);
        path = tmp;

        // Calculate initial best cost of path
        double best = pathCost(path, false);

        final int numCities = path.size();
        int visited = 0, current = 0;

        long start = System.currentTimeMillis();
        if(listener != null) listener.onStart(path, best, start);

        int iter = 1;
        boolean running = true;
        while(running) {
            if(! (visited < numCities)) running = false;
            if(terminationCriteria.getTspIterations() != null) {
                if(iter > terminationCriteria.getTspIterations()) running = false;
            }
            if(terminationCriteria.getTspTimeLimit() != null) {
                long now = System.currentTimeMillis();
                if((now-start) > terminationCriteria.getTspTimeLimit()*1000) running = false;
            }

            final Point currentPoint = path.get(current);
            if(! currentPoint.isVisited()) {
                // from the current city, try to find a move.
                final double modified = findMove(current, currentPoint, path, numCities);

                // if a move was found, go to previous city.
                // best is += modified delta.
                if(modified < 0) {
                    current = wrap(current-1, numCities);
                    visited = 0;
                    best += modified;

                    if(listener != null) listener.bestSolutionFound(path, best, System.currentTimeMillis());
                    continue;
                }

                currentPoint.setVisited(true);
            }

            // if city is inactive or no moves found, go to next city.
            current = wrap(current+1, numCities);
            visited++;
            iter++;
        }

        // Remove first path, since it is a depot
        path = path.subList(1, path.size());

        if(listener != null) listener.onFinish(path, best, System.currentTimeMillis());
    }

    private double findMove(int current, Point currentPoint, List<Point> path, int numCities) {
        final int prev = wrap(current-1, numCities);
        final int next = wrap(current+1, numCities);
        final Point prevPoint = path.get(prev);
        final Point nextPoint = path.get(next);

        for(int i = wrap(current+2, numCities), j = wrap(current+3, numCities);
            j != current;
            i = j, j = wrap(j+1, numCities)) {

            final Point c = path.get(i);
            final Point d = path.get(j);

            final double delta1 = moveCost(prevPoint, currentPoint, c, d);
            if(delta1 < 0) {
                activate(prevPoint, currentPoint, c, d);
                reverse(path, Math.min(prev, i)+1, Math.max(prev, i));
                return delta1;
            }

            final double delta2 = moveCost(currentPoint, nextPoint, c, d);
            if(delta2 < 0) {
                activate(currentPoint, nextPoint, c, d);
                reverse(path, Math.min(current, i)+1, Math.max(current, i));
                return delta2;
            }

        }

        return 0.0;
    }

    private void reverse(List<Point> x, int from, int to) {
        for(int i = from, j = to; i < j; i++, j--) {
            final Point tmp = x.get(i);
            x.set(i, x.get(j));
            x.set(j, tmp);
        }
    }

    private void activate(Point a, Point b, Point c, Point d) {
        a.setVisited(false); b.setVisited(false);
        c.setVisited(false); d.setVisited(false);
    }

    private double moveCost(Point a, Point b, Point c, Point d) {
        // original edges (ab) (cd)
        final double _ab = distanceFunction.distance(a, b), _cd = distanceFunction.distance(c, d);

        // candidate edges (ac) (bd)
        final double _ac = distanceFunction.distance(a, c), _bd = distanceFunction.distance(b, d);

        // triangle of inequality: at least 1 edge will be shorter.
        // if both will be longer, there will be no improvement.
        // return a positive delta to indicate no improvement.
        if(_ab < _ac && _cd < _bd)
            return 1;

        // otherwise must calculate distance delta.
        return (Math.sqrt(_ac) + Math.sqrt(_bd)) -
                (Math.sqrt(_ab) + Math.sqrt(_cd));
    }

    private int wrap(final int i, final int max) {
        return (max+i) % max;
    }

    public static double invSqrt(double x) {
        final double xhalf = 0.5d*x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6eb50c7b537a9L - (i >> 1);
        x = Double.longBitsToDouble(i);
        x *= (1.5d - xhalf*x*x); // pass 1
        x *= (1.5d - xhalf*x*x); // pass 2
        x *= (1.5d - xhalf*x*x); // pass 3
        x *= (1.5d - xhalf*x*x); // pass 4
        return x;
    }

    public static final double sqrt(final double d) {
        //return Math.sqrt(d);
        //return sqrtnat(d); // no diff (jni overhead.)
        return d*invSqrt(d); // ~10% faster than Math.sqrt.
    }
}
