package com.soedomoto.clustering;

import com.soedomoto.clustering.model.Cluster;
import com.soedomoto.clustering.model.Point;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by soedomoto on 28/12/16.
 */
public class KNN implements ClusteringAlgorithm {
    private ExecutorService threadExecutor = Executors.newCachedThreadPool();
    private DistanceFunction distanceFn = new EuclideanDistanceFunction();

    private Map<String, Point> points = new HashMap();
    private Map<String, Cluster> clusters = new HashMap();

    private Double maxRouteCost;

    public KNN() {}

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

    private static class TPoint extends Point {
        private int index = 0;

        public TPoint(int idx, String id, double x, double y) {
            super(id, x, y);
            index = idx;
        }

        public TPoint(String id, double x, double y) {
            super(id, x, y);
        }

        public TPoint(double x, double y) {
            super(x, y);
        }

        public int getIndex() {
            return index;
        }
    }

    @Override
    public void buildClusters() {
        // Get neighbors
        Map<String, List<String>> clusterNeighborIds = new HashMap();
        for(Cluster cluster: clusters.values()) {
            List<Point> points = new ArrayList(this.points.values());
            List<TPoint> copy = new ArrayList();
            for(int p=0; p<points.size(); p++)
                copy.add(new TPoint(p, points.get(p).getId(), points.get(p).getX(), points.get(p).getY()));

            Collections.sort(copy, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return new Double(distanceFn.distance(cluster.getCentroid(), o1) -
                            distanceFn.distance(cluster.getCentroid(), o2)).intValue();
                }
            });

            List<String> neighborIds = new ArrayList(copy.size());
            for(TPoint p: copy) neighborIds.add(p.getId());

            clusterNeighborIds.put(cluster.getId(), neighborIds);
        }

        for(String cId: clusterNeighborIds.keySet()) {
            threadExecutor.execute(new ClusterIterator(cId, clusterNeighborIds.get(cId)));
        }

        threadExecutor.shutdown();

        try {
            while (!threadExecutor.awaitTermination(1, TimeUnit.MINUTES)) {
                System.out.println("Not yet. Still waiting for termination");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
        int iter = 0;
        while (true) {
            try {
                List<String> clusterIds = new ArrayList<>(clusterNeighborIds.keySet().size());
                List<String> iNeighbors = new ArrayList(clusterNeighborIds.keySet().size());
                for(String cId: clusterNeighborIds.keySet()) {
                    clusterIds.add(cId);
                    iNeighbors.add(clusterNeighborIds.get(cId).get(iter));
                }

                Set<String> iDuplicateNeighbors = this.findDuplicates(iNeighbors);
                if(iDuplicateNeighbors.size() > 0) {
                    for(String iDuplicateNeighbor: iDuplicateNeighbors) {
                        if(points.get(iDuplicateNeighbor).getCluster() != null) {
                            List<Integer> dIdxs = this.getDuplicateIndices(iDuplicateNeighbor, iNeighbors);

                            double minDist = Double.MAX_VALUE;
                            Integer selIdx = null;

                            for(Integer dIdx: dIdxs) {
                                String dId = iNeighbors.get(dIdx);
                                String cId = clusterIds.get(dIdx);

                                double dist = distanceFn.distance(clusters.get(cId).getCentroid(),
                                        points.get(dId));
                                if(dist < minDist) {
                                    selIdx = dIdx;
                                    minDist = dist;
                                }
                            }

                            for(Integer idx: dIdxs) {
                                if(idx != selIdx) iNeighbors.set(idx, null);
                            }
                        }
                    }
                }

                for(int i=0; i<iNeighbors.size(); i++) {
                    String iNeighbor = iNeighbors.get(i);
                    if(iNeighbor != null && points.get(iNeighbor).getCluster() == null)
                        points.get(iNeighbor).setCluster(clusters.get(clusterIds.get(i)));
                }

                iter++;
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        // Sort points by distance
        for(Cluster c: clusters.values()) {
            Collections.sort(c.getPoints(), new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return new Double(distanceFn.distance(c.getCentroid(), o1) -
                            distanceFn.distance(c.getCentroid(), o2)).intValue();
                }
            });
        }
         */
    }

    public class ClusterIterator implements Runnable {
        private final String clusterId;
        private final List<String> neighborIds;
        private int currIter = 0;

        public ClusterIterator(String clusterId, List<String> neighborIds) {
            this.clusterId = clusterId;
            this.neighborIds = neighborIds;
        }

        @Override
        public void run() {
            while(true) {
                if(currIter >= neighborIds.size()) break;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                double cost = 0;
                for(int p=1; p<clusters.get(clusterId).points.size(); p++) {
                    cost += distanceFn.distance(clusters.get(clusterId).points.get(p-1),
                            clusters.get(clusterId).points.get(p));
                }

                if(maxRouteCost != null && cost > maxRouteCost) continue;

                String nId = neighborIds.get(currIter);
                if(nId != null && points.get(nId).getCluster() == null)
                    points.get(nId).setCluster(clusters.get(clusterId));

                cost = 0;
                for(int p=1; p<clusters.get(clusterId).points.size(); p++) {
                    cost += distanceFn.distance(clusters.get(clusterId).points.get(p-1),
                            clusters.get(clusterId).points.get(p));
                }
                if(maxRouteCost == null || cost > maxRouteCost) maxRouteCost = cost;


                currIter++;
            }
        }
    }

    public Collection<Point> getPoints() {
        return points.values();
    }

    public void setPoints(List<Point> points) {
        this.points = new HashMap<>();
        for(Point p: points) {
            this.points.put(p.getId(), p);
        }
    }

    public Collection<Cluster> getClusters() {
        return clusters.values();
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = new HashMap<>();
        for(Cluster c: clusters) {
            this.clusters.put(c.getId(), c);
        }
    }

    public void setDistanceFunction(DistanceFunction distanceFn) {
        this.distanceFn = distanceFn;
    }

    public DistanceFunction getDistanceFunction() {
        return distanceFn;
    }
}
