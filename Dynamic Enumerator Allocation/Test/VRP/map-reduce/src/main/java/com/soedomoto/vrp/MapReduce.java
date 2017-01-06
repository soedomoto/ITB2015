package com.soedomoto.vrp;

import com.soedomoto.clustering.ClusteringAlgorithm;
import com.soedomoto.clustering.model.Cluster;
import com.soedomoto.clustering.model.Point;
import com.soedomoto.tsp.Greedy2Opt;
import com.soedomoto.tsp.TspAlgorithm;
import com.soedomoto.tsp.TspListener;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by soedomoto on 28/12/16.
 */
public class MapReduce implements VrpAlgorithm {
    ExecutorService threadExecutor = Executors.newCachedThreadPool();
    private ClusteringAlgorithm clusteringAlgorithm;
    private List<Point> customers = new ArrayList();
    private List<Point> vehicles = new ArrayList();
    private TerminationCriteria terminationCriteria = new TerminationCriteria();

    private List<Cluster> createClusters() {
        clusteringAlgorithm.setPoints(customers);

        List<Cluster> clusters = new ArrayList();
        for(int r=0; r<vehicles.size(); r++) {
            Cluster cluster = new Cluster(vehicles.get(r).getId());
            cluster.setCentroid(vehicles.get(r));
            clusters.add(cluster);
        }
        clusteringAlgorithm.setClusters(clusters);
        clusteringAlgorithm.buildClusters();
        return clusters;
    }

    public void search() {
        List<Cluster> clusters = this.createClusters();

        // Solve each cluster in separated thread
        Map<Cluster, ClusterSolver> clusterSolvers = new HashMap();
        for(Cluster cluster: clusters) {
            ClusterSolver solver = new ClusterSolver(cluster);
            clusterSolvers.put(cluster, solver);
            threadExecutor.execute(solver);
        }

        // threadExecutor.execute(new GlobalSolver(clusterSolvers));

        threadExecutor.shutdown();
    }

    public class GlobalSolver implements Runnable {
        private final Map<Cluster, ClusterSolver> clusterSolvers;

        public GlobalSolver(Map<Cluster, ClusterSolver> clusterSolvers) {
            this.clusterSolvers = clusterSolvers;
        }

        @Override
        public void run() {
            while(true) {
                System.out.println("===");

                for(Cluster cluster: clusterSolvers.keySet()) {
                    ClusterSolver solver = clusterSolvers.get(cluster);
//                    Iterator<Map.Entry<List<Point>, Double>> it = solver.getBestSolutionsIterator();
//
//                    while(it.hasNext()) {
//                        List<Point> solutions = it.next().getKey();
//                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ClusterSolver implements Runnable {
        private final Cluster cluster;
        private final Map<List<Point>, Double> localSolutions = new HashMap();
        private double bestCosts = Double.MAX_VALUE;
        private int i = 0;
        private int j = 1;

        public ClusterSolver(Cluster cluster) {
            this.cluster = cluster;
        }

        @Override
        public void run() {
            List<Point> path = new ArrayList<>(cluster.getPoints());
            TspAlgorithm tspAlgorithm = new Greedy2Opt(clusteringAlgorithm.getDistanceFunction());

            TspListener listener = new TspListener() {
                @Override
                public void bestSolutionFound(List<Point> path, double score, long time) {

                }

                @Override
                public void onStart(List<Point> path, double score, long time) {
                    System.out.println(String.format("Start %s (%s)(%s): %s", cluster, time, score, path));
                }

                @Override
                public void onFinish(List<Point> path, double score, long time) {
                    System.out.println(String.format("Finish %s (%s)(%s): %s", cluster, time, score, path));
                }
            };

            tspAlgorithm.setListener(listener);
            tspAlgorithm.setTerminationCriteria(terminationCriteria);
            tspAlgorithm.solve(cluster.getCentroid(), path);

            // Check termination criteria
        }
    }

    public List<Point> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Point> customers) {
        this.customers = customers;
    }

    public void addCustomer(Point customer) {
        this.customers.add(customer);
    }

    public List<Point> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Point> vehicles) {
        this.vehicles = vehicles;
    }

    public void addVehicle(Point vehicle) {
        this.vehicles.add(vehicle);
    }

    public ClusteringAlgorithm getClusteringAlgorithm() {
        return clusteringAlgorithm;
    }

    public void setClusteringAlgorithm(ClusteringAlgorithm clusteringAlgorithm) {
        this.clusteringAlgorithm = clusteringAlgorithm;
    }

    public TerminationCriteria getTerminationCriteria() {
        return terminationCriteria;
    }

    public void setTerminationCriteria(TerminationCriteria terminationCriteria) {
        this.terminationCriteria = terminationCriteria;
    }
}
