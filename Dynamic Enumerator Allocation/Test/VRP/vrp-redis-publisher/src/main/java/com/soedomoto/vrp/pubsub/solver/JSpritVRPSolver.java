package com.soedomoto.vrp.pubsub.solver;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.soedomoto.vrp.pubsub.App;
import com.soedomoto.vrp.pubsub.model.CensusBlock;
import com.soedomoto.vrp.pubsub.model.DistanceMatrix;
import com.soedomoto.vrp.pubsub.model.Enumerator;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by soedomoto on 19/01/17.
 */
public abstract class JSpritVRPSolver implements Runnable, VRPSolver {
    private static final Logger LOG = Logger.getLogger(JSpritVRPSolver.class.getName());
    private static final Map<Long, Map<Long, Double>> DURATION_MATRIX = new HashMap();
    private static final Map<Long, Map<Long, Double>> DISTANCE_MATRIX = new HashMap();

    private final App app;
    private final String channel;

    public JSpritVRPSolver(App app, String channel) {
        this.app = app;
        this.channel = channel;
    }

    public void run() {
        try {
            this.onStarted(this.channel);

            List<Enumerator> es = app.getEnumeratorDao().queryForAll();
            Map<Long, Enumerator> allEnumerators = new HashMap();
            for(Enumerator e : es) {
                allEnumerators.put(e.getId(), e);
            }

            List<CensusBlock> bses = app.getCensusBlockDao().queryForAll();
            Map<Long, CensusBlock> allBses = new HashMap();
            Map<Long, CensusBlock> unassignedBses = new HashMap();
            for(CensusBlock bs : bses) {
                allBses.put(bs.getId(), bs);
                if(bs.getAssignedTo() == null) unassignedBses.put(bs.getId(), bs);
            }

            if(DURATION_MATRIX.size() == 0 || DISTANCE_MATRIX.size() == 0) {
                List<DistanceMatrix> dm = app.getDistanceMatrixDao().queryForAll();
                for (DistanceMatrix m : dm) {
                    if (!DURATION_MATRIX.keySet().contains(m.getLocationA()))
                        DURATION_MATRIX.put(m.getLocationA(), new HashMap());
                    DURATION_MATRIX.get(m.getLocationA()).put(m.getLocationB(), m.getDuration());

                    if (!DISTANCE_MATRIX.keySet().contains(m.getLocationA()))
                        DISTANCE_MATRIX.put(m.getLocationA(), new HashMap());
                    DISTANCE_MATRIX.get(m.getLocationA()).put(m.getLocationB(), m.getDistance());
                }
            }

            // Build problem
            VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance().setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);


            for(CensusBlock bs : unassignedBses.values()) {
                Service.Builder builder = Service.Builder.newInstance(String.valueOf(bs.getId()));
                builder.addSizeDimension(0, 1);
                //builder.setTimeWindow(TimeWindow.newInstance(0.0, 0.0));
                //builder.setServiceTime(Double.parseDouble(line[3]));

                Location loc = Location.Builder.newInstance()
                        .setId(String.valueOf(bs.getId()))
                        .setCoordinate(Coordinate.newInstance(bs.getLon(), bs.getLat()))
                        .build();
                builder.setLocation(loc);

                Service node = builder.build();
                vrpBuilder.addJob(node);
            }


            VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("enumerator");
            vehicleTypeBuilder.addCapacityDimension(0, (unassignedBses.size() / allEnumerators.size()) + 1);
            //vehicleTypeBuilder.setCostPerDistance(1.0);
            vehicleTypeBuilder.setCostPerDistance(0);
            vehicleTypeBuilder.setCostPerTransportTime(1);
            vehicleTypeBuilder.setCostPerServiceTime(1);
            VehicleType vehicleType = vehicleTypeBuilder.build();


            CensusBlock bs = allBses.get(Long.valueOf(channel));
            VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(String.valueOf(bs.getId()));

            Location loc = Location.Builder.newInstance()
                    .setId(String.valueOf(bs.getId()))
                    .setCoordinate(Coordinate.newInstance(bs.getLon(), bs.getLat()))
                    .build();
            builder.setStartLocation(loc);

            builder.setType(vehicleType);
            VehicleImpl vehicle = builder.build();
            vrpBuilder.addVehicle(vehicle);


            VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
                    .newInstance(true);

            for(Long a : DURATION_MATRIX.keySet()) {
                for(Long b : DURATION_MATRIX.get(a).keySet()) {
                    costMatrixBuilder.addTransportDistance(String.valueOf(a), String.valueOf(b), DISTANCE_MATRIX.get(a).get(b));
                    costMatrixBuilder.addTransportTime(String.valueOf(a), String.valueOf(b), DURATION_MATRIX.get(a).get(b));
                }
            }

            VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
            vrpBuilder.setRoutingCost(costMatrix);
            LOG.debug(String.format("Using: %s enumerator(s), %s location(s)", 1, unassignedBses.size()));


            VehicleRoutingProblem problem = vrpBuilder.build();

            int numProcessors = Runtime.getRuntime().availableProcessors();
            VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
                    .setProperty(Jsprit.Parameter.THREADS, String.valueOf(numProcessors))
                    .buildAlgorithm();
            algorithm.setMaxIterations(app.getMaxIteration());
            LOG.debug(String.format("Finding solution using %s thread(s) and %s iteration(s)", numProcessors, app.getMaxIteration()));

            Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
            LOG.debug(String.format("%s solution(s) found. Finding the best", solutions.size()));

            VehicleRoutingProblemSolution solution = this.findBest(solutions);

            for(VehicleRoute route : solution.getRoutes()) {
                Vehicle routeVehicle = route.getVehicle();
                Iterator<Job> jobIt = route.getTourActivities().getJobs().iterator();
                if(jobIt.hasNext()) {
                    Job job = jobIt.next();
                    if (job instanceof Service) {
                        Service activity = (Service) job;

                        double duration = 0.0;
                        long a = Long.parseLong(routeVehicle.getStartLocation().getId());
                        long b = Long.parseLong(activity.getLocation().getId());
                        if(DURATION_MATRIX.keySet().contains(a)) {
                            if(DURATION_MATRIX.get(a).keySet().contains(b)) {
                                duration = DURATION_MATRIX.get(a).get(b);
                            }
                        }

                        CensusBlock currBs = allBses.get(Long.valueOf(activity.getLocation().getId()));

                        onSolution(route.getVehicle().getId(), routeVehicle, activity, duration, currBs.getServiceTime());
                    }
                }
            }

            onFinished(this.channel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VehicleRoutingProblemSolution findBest(Collection<VehicleRoutingProblemSolution> solutions) {
        VehicleRoutingProblemSolution bestSolution = null;
        double leastDuration = Double.MAX_VALUE;

        for (VehicleRoutingProblemSolution s : solutions) {
            double duration = 0.0;

            for(VehicleRoute r : s.getRoutes()) {
                Location depot = r.getVehicle().getStartLocation();
                if(r.getActivities().size() > 0) {
                    TourActivity firstAct = r.getActivities().get(0);

                    long a = Long.parseLong(depot.getId());
                    long b = Long.parseLong(firstAct.getLocation().getId());
                    if(DURATION_MATRIX.keySet().contains(a)) {
                        if(DURATION_MATRIX.get(a).keySet().contains(b)) {
                            duration += DURATION_MATRIX.get(a).get(b);
                        }
                    }
                }
            }

            if(duration < leastDuration) {
                bestSolution = s;
                leastDuration = duration;
            }
        }

        LOG.debug(String.format("Best solution found, with total 'first-job' transport time is %s", leastDuration));

        return bestSolution;
    }
}
