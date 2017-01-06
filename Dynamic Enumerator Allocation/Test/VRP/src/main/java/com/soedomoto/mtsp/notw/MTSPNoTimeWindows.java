package com.soedomoto.mtsp.notw;

import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.analysis.toolbox.StopWatch;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListener;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.soedomoto.mtsp.MTSPPrinter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by soedomoto on 07/12/16.
 */
public class MTSPNoTimeWindows {
    private VehicleRoutingProblem.Builder vrpBuilder;

    public MTSPNoTimeWindows() {
        vrpBuilder = VehicleRoutingProblem.Builder.newInstance().setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
    }

    public Object[] createSolution(Integer iterations, VehicleRoutingAlgorithmListener... listeners) {
        VehicleRoutingProblem problem = vrpBuilder.build();
        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
                .setProperty(Jsprit.Parameter.THREADS, "50").buildAlgorithm();
        algorithm.setMaxIterations(iterations);

        for(VehicleRoutingAlgorithmListener listener : listeners) {
            algorithm.getAlgorithmListeners().addListener(listener);
        }

        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        return new Object[] {problem, bestSolution};
    }

    public void setRouteCost(String csvCost) {
        VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
                .newInstance(true);

        try {
            CSVReader reader = new CSVReader(new FileReader(csvCost));
            reader.readNext();

            String [] line;
            while ((line = reader.readNext()) != null) {
                try {
                    costMatrixBuilder.addTransportDistance(line[0], line[1], Double.parseDouble(line[2]));
                    costMatrixBuilder.addTransportTime(line[0], line[1], Double.parseDouble(line[3]));
                } catch (Exception e) {
                    costMatrixBuilder.addTransportDistance(line[0], line[1], 0.0);
                    costMatrixBuilder.addTransportTime(line[0], line[1], 0.0);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
        vrpBuilder.setRoutingCost(costMatrix);
    }

    public void setEnumerators(String csvEnum) {
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("enumerator");
        //vehicleTypeBuilder.addCapacityDimension(0, 100);
        //vehicleTypeBuilder.setCostPerDistance(1.0);
        vehicleTypeBuilder.setCostPerDistance(0);
        vehicleTypeBuilder.setCostPerTransportTime(1);
        vehicleTypeBuilder.setCostPerServiceTime(1);
        VehicleType vehicleType = vehicleTypeBuilder.build();

        try {
            CSVReader reader = new CSVReader(new FileReader(csvEnum));
            reader.readNext();

            String [] line;
            while ((line = reader.readNext()) != null) {
                VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(line[0]);

                try {
                    Location loc = Location.Builder.newInstance()
                            .setId(line[0])
                            .setCoordinate(Coordinate.newInstance(Double.parseDouble(line[2]),
                                    Double.parseDouble(line[1])))
                            .build();
                    builder.setStartLocation(loc);
                } catch (Exception e) {}


                builder.setType(vehicleType);
                VehicleImpl vehicle = builder.build();
                vrpBuilder.addVehicle(vehicle);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLocations(String csvLocs) {
        try {
            CSVReader reader = new CSVReader(new FileReader(csvLocs));
            reader.readNext();

            String [] line;
            while ((line = reader.readNext()) != null) {
                Service.Builder builder = Service.Builder.newInstance(line[0]);
                //builder.addSizeDimension(0, 1);
                //builder.setTimeWindow(TimeWindow.newInstance(0.0, 0.0));
                //builder.setServiceTime(Double.parseDouble(line[3]));

                try {
                    Location loc = Location.Builder.newInstance()
                        .setId(line[0])
                        .setCoordinate(
                            Coordinate.newInstance(Double.parseDouble(line[2]), Double.parseDouble(line[1]))).build();
                    builder.setLocation(loc);
                } catch (Exception e) {}

                Service node = builder.build();
                vrpBuilder.addJob(node);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        MTSPNoTimeWindows mtsp = new MTSPNoTimeWindows();

        mtsp.setEnumerators( "/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research" +
                "/Dynamic Enumerator Allocation/Test/Data/enumerators.csv");
        mtsp.setLocations("/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research" +
                "/Dynamic Enumerator Allocation/Test/Data/enumeration_problem.csv");
        mtsp.setRouteCost("/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research" +
                "/Dynamic Enumerator Allocation/Test/Data/cost_matrix_nagari_2014.csv");

        StopWatch stopWatch = new StopWatch();
        AlgorithmSearchProgressChartListener progress = new AlgorithmSearchProgressChartListener(
                "output/mtsp_no_time_windows_progress.png");

        Object[] problemSolution = mtsp.createSolution(2000, stopWatch, progress);
        VehicleRoutingProblem problem = (VehicleRoutingProblem) problemSolution[0];
        VehicleRoutingProblemSolution solution = (VehicleRoutingProblemSolution) problemSolution[1];

        CSVWriter out = new CSVWriter(new FileWriter("output/mtsp_no_time_windows.csv"));
        out.writeNext(new String[] {"Enumerator", "Activity", "Location", "Lat", "Lon",
                "Duration", "Start", "Stop", "Cost"});
        for(VehicleRoute route : solution.getRoutes()) {
            String enumerator = route.getVehicle().getId();
            TourActivity prevAct = route.getStart();
            for(TourActivity activity : route.getActivities()) {
                double transportCost = problem.getTransportCosts().getTransportCost(prevAct.getLocation(),
                        activity.getLocation(), prevAct.getEndTime(), route.getDriver(),
                        route.getVehicle());
                double activityCost = problem.getActivityCosts().getActivityCost(activity, activity.getArrTime(),
                        route.getDriver(), route.getVehicle());
                out.writeNext(new String[] {
                        enumerator,
                        activity.getName(),
                        activity.getLocation().getId(),
                        String.valueOf(activity.getLocation().getCoordinate().getY()),
                        String.valueOf(activity.getLocation().getCoordinate().getX()),
                        String.valueOf(activity.getOperationTime()),
                        String.valueOf(activity.getArrTime()),
                        String.valueOf(activity.getEndTime()),
                        String.valueOf(activityCost)});

                prevAct = activity;
            }
        }
        out.flush();
        out.close();


        MTSPPrinter.print(problem, solution, MTSPPrinter.Print.VERBOSE);
        new Plotter(problem, solution).plot("output/mtsp_no_time_windows.png","MTSP With Cost Matrix");
        new GraphStreamViewer(problem, solution).labelWith(GraphStreamViewer.Label.ID).setRenderDelay(50).display();
    }

}
