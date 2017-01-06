package com.soedomoto.vrp.app;

import com.opencsv.CSVReader;
import com.soedomoto.clustering.ClusteringAlgorithm;
import com.soedomoto.clustering.DistanceFunction;
import com.soedomoto.clustering.KNN;
import com.soedomoto.clustering.model.Cluster;
import com.soedomoto.clustering.model.Point;
import com.soedomoto.vrp.MapReduce;
import com.soedomoto.vrp.TerminationCriteria;
import com.soedomoto.vrp.VrpAlgorithm;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by soedomoto on 27/12/16.
 */
public class KMeansTest {

    public static void main(String[] args) throws IOException {
        // Instantiate locations
        List<String[]> table = new CSVReader(new FileReader("/media/soedomoto/DATA/ITB2015/EL5090 - " +
                "Research Method/Research/Dynamic Enumerator Allocation/Test/Data/enumeration_problem.csv")).readAll();

        List<Point> customers = new ArrayList();
        for(int r=1; r<table.size(); r++) {
            String[] row = table.get(r);
            customers.add(new Point(row[0], Double.parseDouble(row[2]), Double.parseDouble(row[1])));
        }

        // Instantiate enumarators
        table = new CSVReader(new FileReader("/media/soedomoto/DATA/ITB2015/EL5090 - Research " +
                "Method/Research/Dynamic Enumerator Allocation/Test/Data/enumerators.csv")).readAll();

        List<Point> vehicles = new ArrayList();
        for(int r=1; r<table.size(); r++) {
            String[] row = table.get(r);
            vehicles.add(new Point(row[0], Double.parseDouble(row[2]), Double.parseDouble(row[1])));
        }

        List<Cluster> clusters = new ArrayList();
        for(int r=1; r<table.size(); r++) {
            String[] row = table.get(r);
            Cluster cluster = new Cluster(row[0]);
            cluster.setCentroid(new Point(row[0], Double.parseDouble(row[2]), Double.parseDouble(row[1])));
            clusters.add(cluster);
        }

        // Instantiate distance matrix
        Map<String, Map<String, Double>> durationMatrix = new HashMap();
        table = new CSVReader(new FileReader("/media/soedomoto/DATA/ITB2015/EL5090 - Research " +
                "Method/Research/Dynamic Enumerator Allocation/Test/Data/cost_matrix_nagari_2014.csv")).readAll();
        for(int r=1; r<table.size(); r++) {
            String[] row = table.get(r);
            String locA = row[0];
            String locB = row[1];
            double duration = Double.parseDouble(row[3]);

            durationMatrix.putIfAbsent(locA, new HashMap());
            durationMatrix.get(locA).put(locB, duration);
        }

        // Using Map-Reduce algorithm
        VrpAlgorithm vrpAlg = new MapReduce();
        vrpAlg.setCustomers(customers);
        vrpAlg.setVehicles(vehicles);

        ClusteringAlgorithm knn = new KNN();
        knn.setDistanceFunction(new DistanceFunction() {
            @Override
            public double distance(Point a, Point b) {
                try {
                    return durationMatrix.get(a.getId()).get(b.getId());
                } catch (NullPointerException e) {
                    return 0.0;
                }
            }
        });
        vrpAlg.setClusteringAlgorithm(knn);

        vrpAlg.setTerminationCriteria(new TerminationCriteria() {{
            setTspTimeLimit(30);
        }});
        vrpAlg.search();




//        KNN knn = new KNN();
//        knn.setPoints(points);
//        knn.setClusters(clusters);
//
//        knn.setDistanceFunction(new DistanceFunction() {
//            @Override
//            public double distance(Point a, Point b) {
//                try {
//                    return durationMatrix.get(a.getId()).get(b.getId());
//                } catch (NullPointerException e) {
//                    return 0.0;
//                }
//            }
//        });
//
//        knn.buildClusters();
//
//        for (int i = 0; i < clusters.size(); i++) {
//            Cluster c = clusters.get(i);
//
//            System.out.println(String.format("%s. Centroid: %s, Points: %s",
//                    c.id, c.getCentroid(), c.getPoints().size()));
//        }
    }

}
