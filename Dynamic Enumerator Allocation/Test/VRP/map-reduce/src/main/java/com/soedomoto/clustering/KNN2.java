package com.soedomoto.clustering;

import com.soedomoto.clustering.model.Cluster;
import com.soedomoto.clustering.model.Point;

import java.util.*;

/**
 * Created by soedomoto on 28/12/16.
 */
public class KNN2 implements ClusteringAlgorithm {
    private DistanceFunction distanceFn = new EuclideanDistanceFunction();

    private List<Point> points = new ArrayList();
    private List<Cluster> clusters = new ArrayList();

    public KNN2() {}

    private <T> Set<T> findDuplicates(Collection<T> list) {
        Set<T> duplicates = new LinkedHashSet<T>();
        Set<T> uniques = new HashSet<T>();

        for(T t : list) {
            if(!uniques.add(t)) {
                duplicates.add(t);
            }
        }

        return duplicates;
    }

    private <T> List getDuplicateIndices(T o, List<T> list) {
        List<Integer> idxs = new ArrayList();
        for(int i=0; i<list.size(); i++) {
            if(list.get(i) == o) {
                idxs.add(i);
            }
        }

        return idxs;
    }

    @Override
    public void buildClusters() {
        // Get neighbors
        Map<Cluster, Iterator<Point>> clusterNeighborIt = new HashMap();
        for(Cluster cluster: clusters) {
            Collections.sort(points, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return new Double(distanceFn.distance(cluster.getCentroid(), o1) -
                            distanceFn.distance(cluster.getCentroid(), o2)).intValue();
                }
            });

            List<Point> neighbors = new ArrayList(points.size());
            for(int p=0; p<points.size(); p++) neighbors.add(p, points.get(p));

            clusterNeighborIt.put(cluster, neighbors.iterator());
        }

        // Assign cluster
        while(true) {
            try {
                List<Point> iNeighbors = new ArrayList(clusterNeighborIt.keySet().size());
                for(int i=0; i<clusterNeighborIt.keySet().size(); i++) {
                    Iterator<Point> it = new ArrayList<>(clusterNeighborIt.values()).get(i);
                    iNeighbors.add(i, it.next());
                }

                Set<Point> iDuplicateNeighbors = this.findDuplicates(iNeighbors);
                if(iDuplicateNeighbors.size() > 0) {
                    for(Point iDuplicateNeighbor: iDuplicateNeighbors) {
                        if(iDuplicateNeighbor.getCluster() != null) {
                            List<Integer> idxs = this.getDuplicateIndices(iDuplicateNeighbor, iNeighbors);

                            double minDist = Double.MAX_VALUE;
                            Integer selIdx = null;
                            for(Integer idx: idxs) {
                                double dist = distanceFn.distance(clusters.get(idx).getCentroid(), iDuplicateNeighbor);
                                if(dist < minDist) {
                                    selIdx = idx;
                                    minDist = dist;
                                }
                            }

                            for(Integer idx: idxs) {
                                if(idx != selIdx) iNeighbors.set(idx, null);
                            }
                        }
                    }
                }

                for(int i=0; i<iNeighbors.size(); i++) {
                    Point iNeighbor = iNeighbors.get(i);
                    // System.out.println(String.format("%s : %s", clusters.get(i), iNeighbor));
                    if(iNeighbor != null && iNeighbor.getCluster() == null) iNeighbor.setCluster(clusters.get(i));
                }

            } catch (NoSuchElementException e) {
                break;
            }
        }

        // Sort points by distance
        for(Cluster c: clusters) {
            Collections.sort(c.getPoints(), new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return new Double(distanceFn.distance(c.getCentroid(), o1) -
                            distanceFn.distance(c.getCentroid(), o2)).intValue();
                }
            });
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

    public void setDistanceFunction(DistanceFunction distanceFn) {
        this.distanceFn = distanceFn;
    }

    public DistanceFunction getDistanceFunction() {
        return distanceFn;
    }
}
