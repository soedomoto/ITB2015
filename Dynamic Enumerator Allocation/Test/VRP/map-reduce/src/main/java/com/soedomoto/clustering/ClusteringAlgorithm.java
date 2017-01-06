package com.soedomoto.clustering;

import com.soedomoto.clustering.model.Cluster;
import com.soedomoto.clustering.model.Point;

import java.util.List;

/**
 * Created by soedomoto on 27/12/16.
 */
public interface ClusteringAlgorithm {
    public void setPoints(List<Point> points);
    public void setClusters(List<Cluster> clusters);
    public void setDistanceFunction(DistanceFunction distanceFunction);
    public DistanceFunction getDistanceFunction();
    public void buildClusters();
}
