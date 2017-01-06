package com.soedomoto.vrp.app;

import com.opencsv.CSVReader;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.*;
import weka.core.converters.ConverterUtils;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by soedomoto on 27/12/16.
 */
public class Bootstrap {

    public static class TimeDistance
            extends NormalizableDistance
            implements Cloneable, TechnicalInformationHandler {

        private final Map<String, Map<String, Double>> durationMatrix;

        public TimeDistance(Map<String, Map<String, Double>> durationMatrix) {
            this.durationMatrix = durationMatrix;
        }

        @Override
        public double distance(Instance first, Instance second) {
            String locA = String.format("%.0f", first.classValue());
            String locB = String.format("%.0f", second.classValue());
            double duration = durationMatrix.get(locA).getOrDefault(locB, 0.0);

            return duration;
        }

        protected double updateDistance(double currDist, double diff) {
            return 0;
        }

        public String globalInfo() {
            return null;
        }

        public String getRevision() {
            return null;
        }

        public TechnicalInformation getTechnicalInformation() {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        // Import data
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("/media/soedomoto/DATA/ITB2015/EL5090 - " +
                "Research Method/Research/Dynamic Enumerator Allocation/Test/Data/enumeration_problem.csv");
        Instances data = source.getDataSet();
        data.setClassIndex(0);


        // Instantiate distance function
        List<String[]> costMatrix = new CSVReader(new FileReader("/media/soedomoto/DATA/ITB2015/EL5090 - Research " +
                "Method/Research/Dynamic Enumerator Allocation/Test/Data/cost_matrix_nagari_2014.csv")).readAll();

        Map<String, Map<String, Double>> durationMatrix = new HashMap();
        for(int r=1; r<costMatrix.size()-1; r++) {
            String[] row = costMatrix.get(r);
            String locA = row[0], locB = row[1], distance = row[2], duration = row[3];
            durationMatrix.putIfAbsent(locA, new HashMap());
            durationMatrix.get(locA).put(locB, Double.parseDouble(duration));
        }

        TimeDistance timeDistance = new TimeDistance(durationMatrix);

        Classifier ibk = new IBk();
        ibk.buildClassifier(data);

//        SimpleKMeans clusterer = new SimpleKMeans();
//        clusterer.setDebug(true);
//        clusterer.setNumClusters(15);
//        clusterer.setDistanceFunction(timeDistance);
//
//        clusterer.buildClusterer(data);
//
//        System.out.println(clusterer.getClusterSizes());

        // Instantiate clusterer
//        HierarchicalClusterer clusterer = new HierarchicalClusterer();
//        clusterer.setOptions(new String[] {"-L", "NEIGHBOR_JOINING"});
//        clusterer.setDebug(true);
//        clusterer.setNumClusters(15);
//        clusterer.setDistanceFunction(timeDistance);
//        clusterer.setDistanceIsBranchLength(true);
//
//        // Cluster network
//        clusterer.buildClusterer(data);
//
//        clusterer.setPrintNewick(true);
//        System.out.println(clusterer.toString());
//
//        System.out.println(ClusterEvaluation.evaluateClusterer(clusterer, args));
//
//        // Print normal
//        clusterer.setPrintNewick(false);
//        System.out.println(clusterer.graph());
//
//
//        // Print Newick
//        clusterer.setPrintNewick(true);
//        System.out.println(clusterer.graph());
//
//        // Let's try to show this clustered data!
//        JFrame mainFrame = new JFrame("Weka Test");
//        mainFrame.setSize(600, 400);
//        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        Container content = mainFrame.getContentPane();
//        content.setLayout(new GridLayout(1, 1));
//
//        HierarchyVisualizer visualizer = new HierarchyVisualizer(clusterer.graph());
//        content.add(visualizer);
//
//        mainFrame.setVisible(true);
    }

}
