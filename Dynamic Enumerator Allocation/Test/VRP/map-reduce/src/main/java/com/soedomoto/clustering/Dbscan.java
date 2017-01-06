package com.soedomoto.clustering;

import com.soedomoto.clustering.model.Cluster;
import com.soedomoto.clustering.model.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by soedomoto on 27/12/16.
 */
public class Dbscan implements ClusteringAlgorithm {
    private DistanceFunction distanceFunction = new EuclideanDistanceFunction();

    private List<Point> points = new ArrayList();
    private List<Cluster> clusters = new ArrayList();

    private double max_distance;
    private int min_points;

    private boolean[] visited;

    public Dbscan(double max_distance, int min_points) {
        this.max_distance = max_distance;
        this.min_points = min_points;
    }

    public void buildClusters() {
        Iterator<Point> it = points.iterator();
        int n = 0;

        while(it.hasNext()) {
            if(!visited[n]) {
                Point d = it.next();
                visited[n] = true;
                List neighbors = getNeighbors(d);

                if(neighbors.size() >= min_points) {
                    Cluster c = new Cluster(clusters.size() + "");
                    buildCluster(d, c, neighbors);
                    clusters.add(c);
                }
            }
        }
    }

    private void buildCluster(Point d, Cluster c, List<Integer> neighbors) {
        c.addPoint(d);

        for (Integer point : neighbors) {
            Point p = points.get(point);
            if(!visited[point]) {
                visited[point] = true;
                List newNeighbors = getNeighbors(p);
                if(newNeighbors.size() >= min_points) {
                    neighbors.addAll(newNeighbors);
                }
            }
            if(p.getCluster() == null) {
                c.addPoint(p);
            }
        }
    }

    private List getNeighbors(Point d) {
        List neighbors = new ArrayList();
        int i = 0;
        for (Point point : points) {
            double distance = distanceFunction.distance(d, point);

            if(distance <= max_distance) {
                neighbors.add(i);
            }
            i++;
        }

        return neighbors;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
        this.visited = new boolean[points.size()];
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
    }

    public void setDistanceFunction(DistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    @Override
    public DistanceFunction getDistanceFunction() {
        return distanceFunction;
    }

}
