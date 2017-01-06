package com.soedomoto.mtsp.dyntw;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListener;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.opencsv.CSVWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by soedomoto on 13/12/16.
 */
public class Broker implements Runnable {
    private Logger logger = Logger.getLogger(Broker.class.getSimpleName());
    private CSVWriter writer = new CSVWriter(new OutputStreamWriter(new ByteArrayOutputStream()));

    private boolean running = false;

    private Map<String, Enumerator> enumerators = new HashMap<>();
    private Map<String, CensusBlock> censusBlocks = new HashMap<>();
    private Map<String, InterLocationWeight> weightMatrix = new HashMap<>();

    private List<Enumerator> subscribes = new ArrayList<>();
    private Map<String, List<CensusBlock>> visits = new HashMap<>();


    public Broker() {
        try {
            FileHandler fh = new FileHandler("output/mtsp_dyn_time_windows_broker.log");
            this.logger.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ProblemSolution createInitialSolution(Integer iterations, VehicleRoutingAlgorithmListener... listeners) {
        logger.info("Creating initial solution");
        ProblemSolution ps = createSolution(iterations, enumerators.values(), listeners);
        logger.info("Initial solution created");

        return ps;
    }

    public ProblemSolution createSolution(Integer iterations, Collection<Enumerator> enumerators,
                                          VehicleRoutingAlgorithmListener... listeners) {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance()
                .setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);

        // Define census-block
        for(CensusBlock bs : censusBlocks.values()) {
            Location loc = Location.Builder.newInstance()
                    .setId(bs.getId()).setCoordinate(Coordinate.newInstance(bs.getLat(), bs.getLon()))
                    .build();

            Service.Builder builder = Service.Builder.newInstance(bs.getId());
            builder.setLocation(loc);
            Service job = builder.build();
            vrpBuilder.addJob(job);
        }

        // Define enumerators
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("enumerator");
        vehicleTypeBuilder.setCostPerDistance(0);
        vehicleTypeBuilder.setCostPerTransportTime(1);
        vehicleTypeBuilder.setCostPerServiceTime(1);
        VehicleTypeImpl vehicleType = vehicleTypeBuilder.build();

        for(Enumerator pcl : enumerators) {
            Location loc = Location.Builder.newInstance()
                    .setId(pcl.getId()).setCoordinate(Coordinate.newInstance(pcl.getDepot()[0], pcl.getDepot()[1]))
                    .build();

            VehicleImpl vehicle = VehicleImpl.Builder.newInstance(pcl.getId())
                    .setStartLocation(loc).setType(vehicleType).build();
            vrpBuilder.addVehicle(vehicle);
        }

        // Set weight
        VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
                .newInstance(true);

        for(InterLocationWeight weight : weightMatrix.values()) costMatrixBuilder
                .addTransportDistance(weight.getFrom().getId(), weight.getTo().getId(), weight.getDistance())
                .addTransportTime(weight.getFrom().getId(), weight.getTo().getId(), weight.getTime());

        VehicleRoutingTransportCostsMatrix costMatrix = costMatrixBuilder.build();
        vrpBuilder.setRoutingCost(costMatrix);

        // Build problem
        VehicleRoutingProblem problem = vrpBuilder.build();
        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
                .setProperty(Jsprit.Parameter.THREADS, "50").buildAlgorithm();
        algorithm.setMaxIterations(iterations);

        for (VehicleRoutingAlgorithmListener listener : listeners) {
            algorithm.getAlgorithmListeners().addListener(listener);
        }

        // Find solution
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        VehicleRoutingProblemSolution solution = getBestSolution(solutions);

        Map<String, List> enumeratorRoutes = new HashMap<String, List>();
        for (VehicleRoute route : solution.getRoutes()) {
            enumeratorRoutes.putIfAbsent(route.getVehicle().getId(), new ArrayList());
            for(TourActivity act : route.getActivities()) {
                enumeratorRoutes.get(route.getVehicle().getId()).add(act.getLocation().getId());
            }
        }

        List<String> lstEnumeratorRoutes = new ArrayList<String>();
        for(String id : enumeratorRoutes.keySet())
            lstEnumeratorRoutes.add(id + " = " + String.join("-->", enumeratorRoutes.get(id)));

        logger.info(String.join("\n", lstEnumeratorRoutes));

        return new ProblemSolution(problem, solution);
    }

    public VehicleRoutingProblemSolution getBestSolution(Collection<VehicleRoutingProblemSolution> coll) {
        List<VehicleRoutingProblemSolution> solutions = new ArrayList<>(coll);
        List<Double> solutionTimes = new ArrayList();
        boolean allZero = true;

        for(VehicleRoutingProblemSolution solution : solutions) {
            Double totalTime = 0.0;
            for (VehicleRoute route : solution.getRoutes()) {
                String enumeratorId = route.getVehicle().getId();
                String lastVisit = null;
                if(visits.containsKey(enumeratorId)) {
                    List<CensusBlock> allVisits = visits.get(enumeratorId);
                    lastVisit = allVisits.get(allVisits.size() - 1).getId();
                }

                if(route.getActivities().size() > 0) {
                    TourActivity firstActivity = route.getActivities().get(0);
                    String currVisit = firstActivity.getLocation().getId();

                    if(lastVisit != null) {
                        InterLocationWeight weight = weightMatrix.get(String.format("%s-%s", lastVisit, currVisit));
                        totalTime += weight.getTime();
                    }
                }
            }

            solutionTimes.add(totalTime);
            if(totalTime > 0) allZero = false;
        }

        VehicleRoutingProblemSolution bestSolution = null;
        if(allZero) {
            for (VehicleRoutingProblemSolution s : solutions) {
                if (bestSolution == null) bestSolution = s;
                else if (s.getCost() < bestSolution.getCost()) bestSolution = s;
            }
        } else {
            Double bestTime = -1.0;
            for(int i=0; i<solutionTimes.size(); i++) {
                if(solutionTimes.get(i) > bestTime) {
                    bestSolution = solutions.get(i);
                    bestTime = solutionTimes.get(i);
                }
            }
        }

        return bestSolution;
    }

    public boolean subscribeSolution(Enumerator enumerator) {
        subscribes.add(enumerator);
        return true;
    }

    public void publishVisit(Enumerator enumerator, CensusBlock bs) {
        visits.putIfAbsent(enumerator.getId(), new ArrayList<>());

        // Compute output
        double start = 0;
        CensusBlock lvbs = null;
        for(CensusBlock vbs : visits.get(enumerator.getId())) {
            if(lvbs != null) {
                InterLocationWeight weight = weightMatrix.get(String.format("%s-%s", lvbs.getId(), vbs.getId()));
                if(weight == null) weight = weightMatrix.get(String.format("%s-%s", vbs.getId(), lvbs.getId()));
                start += weight.getTime();
            }
            start += vbs.getServiceTime();
            lvbs = vbs;
        }

        if(lvbs != null) {
            String key = String.format("%s-%s", lvbs.getId(), bs.getId());
            if(! weightMatrix.containsKey(key)) key = String.format("%s-%s", bs.getId(), lvbs.getId());

            InterLocationWeight weight = weightMatrix.get(key);
            start += weight.getTime();
        }

        writer.writeNext(new String[] {enumerator.getId(), "service", bs.getId(), String.valueOf(bs.getLat()),
                String.valueOf(bs.getLon()), String.valueOf(bs.getServiceTime()), String.valueOf(start),
                String.valueOf(start + bs.getServiceTime()), "0.0"});

        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bs.setVisited(true);
        visits.get(enumerator.getId()).add(bs);
        censusBlocks.remove(bs.getId());
    }

    public void run() {
        this.running = true;
        logger.info("Broker is started");

        ProblemSolution problemSolution = this.createInitialSolution(1000);

        for(Enumerator enumerator : enumerators.values()) {
            enumerator.publishSolution(problemSolution);
            Thread thread = new Thread(enumerator);
            thread.start();
        }

        boolean processing = false;
        while (true) {
            if(censusBlocks.size() == 0) break;

            try {
                if(subscribes.size() > 0 && !processing) {
                    processing = true;

                    List<Enumerator> processings = new ArrayList<>(subscribes);
                    subscribes.removeAll(subscribes);

                    ProblemSolution ps = createSolution(500, processings);
                    for(Enumerator en : processings) en.publishSolution(ps);

                    processing = false;
                } else {
                    Thread.sleep((long) 10000);
                }
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        this.running = false;
        logger.info("Broker is finished");
    }

    public CensusBlock getCensusBlock(Location location) {
        return censusBlocks.get(location.getId());
    }

    public Map<String, Enumerator> getEnumerators() {
        return enumerators;
    }

    public void addEnumerator(String id, Enumerator enumerator) {
        enumerator.setBroker(this);
        this.enumerators.put(id, enumerator);
    }

    public Map<String, CensusBlock> getCensusBlocks() {
        return censusBlocks;
    }

    public void addCensusBlock(String id, CensusBlock censusBlock) {
        this.censusBlocks.put(id, censusBlock);
    }

    public Map<String, InterLocationWeight> getWeightMatrix() {
        return weightMatrix;
    }

    public void addInterLocationWeight(InterLocationWeight weight) {
        this.weightMatrix.put(weight.getId(), weight);
    }

    public void setOutputFile(String out) throws IOException {
        writer = new CSVWriter(new FileWriter(out));
        writer.writeNext(new String[] {"Enumerator", "Activity", "Location", "Lat", "Lon",
                "Duration", "Start", "Stop", "Cost"});
    }

    public boolean isRunning() {
        return running;
    }
}
