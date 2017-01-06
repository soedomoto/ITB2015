package com.soedomoto.clustering;

import com.soedomoto.clustering.model.Cluster;
import com.soedomoto.clustering.model.Point;

import java.util.ArrayList;
import java.util.List;

public class KMeans implements ClusteringAlgorithm {
    private DistanceFunction distanceFunction = new EuclideanDistanceFunction();

    private List<Point> points = new ArrayList();
    private List<Cluster> clusters = new ArrayList();

    public KMeans() {}

    //The process to buildClusters the K Means, with iterating method.
    public void buildClusters() {
        boolean finish = false;
        int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while(!finish) {
            //Clear buildClusters state
            clearClusters();

            List<Point> lastCentroids = getCentroids();

            //Assign points to the closer buildClusters
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

            List<Point> currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for(int i = 0; i < lastCentroids.size(); i++) {
                distance += distanceFunction.distance(lastCentroids.get(i), currentCentroids.get(i));
            }

            if(distance == 0) {
                finish = true;
            }

            iteration++;
        }
    }

    private void clearClusters() {
        for(Cluster cluster : clusters) {
            cluster.clear();
        }
    }

    private List getCentroids() {
        List centroids = new ArrayList(clusters.size());
        for(Cluster cluster : clusters) {
            Point aux = cluster.getCentroid();
            Point point = new Point(aux.getX(),aux.getY());
            centroids.add(point);
        }
        return centroids;
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max;
        int cluster = 0;
        double distance = 0.0;

        for(Point point : points) {
            min = max;
            for(int i = 0; i < clusters.size(); i++) {
                Cluster c = clusters.get(i);
                distance = distanceFunction.distance(point, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(clusters.get(cluster));
            clusters.get(cluster).addPoint(point);
        }
    }

    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Point> list = cluster.getPoints();
            int n_points = list.size();

            for(Point point : list) {
                sumX += point.getX();
                sumY += point.getY();
            }

            Point centroid = cluster.getCentroid();
            if(n_points > 0) {
                double newX = sumX / n_points;
                double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
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