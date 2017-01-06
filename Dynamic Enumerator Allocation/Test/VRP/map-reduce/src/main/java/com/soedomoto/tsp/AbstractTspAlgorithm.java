package com.soedomoto.tsp;

import com.soedomoto.clustering.DistanceFunction;
import com.soedomoto.clustering.model.Point;
import com.soedomoto.vrp.TerminationCriteria;

import java.util.List;

/**
 * Created by soedomoto on 31/12/16.
 */
public abstract class AbstractTspAlgorithm implements TspAlgorithm {
    protected DistanceFunction distanceFunction;
    protected TerminationCriteria terminationCriteria;
    protected TspListener listener;

    protected AbstractTspAlgorithm(DistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    protected double pathCost(List<Point> path) {
        return pathCost(path, true);
    }

    protected double pathCost(List<Point> path, boolean returnToDepot) {
        double cost = 0;
        if(path.size() > 0) cost += path.get(0).getServiceTime();
        for(int p=1; p<path.size(); p++) {
            cost += distanceFunction.distance(path.get(p-1), path.get(p)) + path.get(p).getServiceTime();
        }
        if(returnToDepot) cost += distanceFunction.distance(path.get(path.size()-1), path.get(0));

        return cost;
    }

    public TerminationCriteria getTerminationCriteria() {
        return terminationCriteria;
    }

    public void setTerminationCriteria(TerminationCriteria terminationCriteria) {
        this.terminationCriteria = terminationCriteria;
    }

    public TspListener getListener() {
        return listener;
    }

    public void setListener(TspListener listener) {
        this.listener = listener;
    }
}
