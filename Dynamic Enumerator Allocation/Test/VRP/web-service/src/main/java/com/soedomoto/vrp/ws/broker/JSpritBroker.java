package com.soedomoto.vrp.ws.broker;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
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
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.DistanceMatrix;
import com.soedomoto.vrp.ws.model.Enumerator;
import com.soedomoto.vrp.ws.model.Subscriber;

import javax.servlet.ServletContext;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

import static com.graphhopper.jsprit.core.algorithm.box.Jsprit.createAlgorithm;

/**
 * Created by soedomoto on 08/01/17.
 */
public class JSpritBroker extends AbstractBroker implements Runnable {
    public JSpritBroker(ScheduledExecutorService executor, ServletContext context) {
        super(executor, context);
    }

    @Override
    public void run() {
        try {
            List<Subscriber> workingSubscribers = subscriberDao.queryBuilder().where().eq("is_processed", false).query();
            List<CensusBlock> unassignedBses = censusBlockDao.queryBuilder().where().isNull("ASSIGNEDTO").query();
            Map<Long, CensusBlock> workingBses = new HashMap();
            for(CensusBlock bs : unassignedBses) workingBses.put(bs.id, bs);


            VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance().setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);


            for(CensusBlock bs : workingBses.values()) {
                Service.Builder builder = Service.Builder.newInstance(String.valueOf(bs.id));
                builder.addSizeDimension(0, 1);
                //builder.setTimeWindow(TimeWindow.newInstance(0.0, 0.0));
                //builder.setServiceTime(Double.parseDouble(line[3]));

                Location loc = Location.Builder.newInstance()
                        .setId(String.valueOf(bs.id))
                        .setCoordinate(Coordinate.newInstance(bs.lon, bs.lat))
                        .build();
                builder.setLocation(loc);

                Service node = builder.build();
                vrpBuilder.addJob(node);
            }


            VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("enumerator");
            vehicleTypeBuilder.addCapacityDimension(0, workingBses.size() / workingSubscribers.size());
            //vehicleTypeBuilder.setCostPerDistance(1.0);
            vehicleTypeBuilder.setCostPerDistance(0);
            vehicleTypeBuilder.setCostPerTransportTime(1);
            vehicleTypeBuilder.setCostPerServiceTime(1);
            VehicleType vehicleType = vehicleTypeBuilder.build();

            try {
                for(Subscriber s : workingSubscribers) {
                    Enumerator e = enumeratorDao.queryForId(s.subscriber);
                    VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(String.valueOf(e.id));

                    CensusBlock bs = workingBses.get(e.depot); //censusBlockDao.queryForId(e.depot);
                    Location loc = Location.Builder.newInstance()
                            .setId(String.valueOf(bs.id))
                            .setCoordinate(Coordinate.newInstance(bs.lon, bs.lat))
                            .build();
                    builder.setStartLocation(loc);

                    builder.setType(vehicleType);
                    VehicleImpl vehicle = builder.build();
                    vrpBuilder.addVehicle(vehicle);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            try {
                VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
                        .newInstance(true);

                List<DistanceMatrix> durMatrix = matrixDao.queryForAll();
                for(DistanceMatrix m : durMatrix) {
                    costMatrixBuilder.addTransportDistance(String.valueOf(m.locationA), String.valueOf(m.locationB), m.distance);
                    costMatrixBuilder.addTransportTime(String.valueOf(m.locationA), String.valueOf(m.locationB), m.duration);
                }

                VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
                vrpBuilder.setRoutingCost(costMatrix);
            } catch (SQLException e) {
                e.printStackTrace();
            }


            VehicleRoutingProblem problem = vrpBuilder.build();

            VehicleRoutingAlgorithm algorithm = createAlgorithm(problem);
            algorithm.setMaxIterations(100);

            Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
            VehicleRoutingProblemSolution solution = Solutions.bestOf(solutions);


            Map<Long, VehicleRoute> routeMap = new HashMap();
            for(VehicleRoute route : solution.getRoutes()) {
                routeMap.put(Long.valueOf(route.getVehicle().getId()), route);
            }

            for(Subscriber s : workingSubscribers) {
                if(routeMap.keySet().contains(s.subscriber)) {
                    Map<String, Object> currPath = new HashMap();

                    final Location vehicleLoc = routeMap.get(s.subscriber).getVehicle().getStartLocation();
                    currPath.put("enumerator", routeMap.get(s.subscriber).getVehicle().getId());
                    currPath.put("depot", vehicleLoc.getId());
                    currPath.put("depot-coord", new Double[] {vehicleLoc.getCoordinate().getY(),
                            vehicleLoc.getCoordinate().getX()});

                    if(routeMap.get(s.subscriber).getActivities().size() > 0) {
                        final TourActivity activity = routeMap.get(s.subscriber).getActivities().get(0);
                        currPath.put("customer", activity.getLocation().getId());
                        currPath.put("customer-coord", new Double[] {activity.getLocation().getCoordinate().getY(),
                                activity.getLocation().getCoordinate().getX()});

                        List<DistanceMatrix> mx = matrixDao.queryForMatching(new DistanceMatrix() {{
                            locationA = Long.parseLong(vehicleLoc.getId());
                            locationB = Long.parseLong(activity.getLocation().getId());
                        }});
                        currPath.put("duration", mx.size() > 0 ? mx.get(0).duration : 0.0);

                        CensusBlock bs = workingBses.get(Long.valueOf(activity.getLocation().getId())); //censusBlockDao.queryForId(Long.valueOf(activity.getLocation().getId()));
                        currPath.put("service-time", bs.serviceTime);

                        bs.assignedTo = s.subscriber;
                        censusBlockDao.update(bs);

                        s.isProcessed = true;
                        subscriberDao.update(s);
                    }

                    asyncResponseMap.get(s.id).resume(currPath);
                }
            }

            listener.finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        LinkedList<Long> runningSubscribers = new LinkedList(subscribers);
//        LinkedList<AsyncResponse> runningAsyncResponses = new LinkedList(asyncResponses);
//
//        subscribers = new LinkedList();
//        asyncResponses = new LinkedList();
//
//
//        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance().setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
//
//
//        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("enumerator");
//        //vehicleTypeBuilder.addCapacityDimension(0, 100);
//        //vehicleTypeBuilder.setCostPerDistance(1.0);
//        vehicleTypeBuilder.setCostPerDistance(0);
//        vehicleTypeBuilder.setCostPerTransportTime(1);
//        vehicleTypeBuilder.setCostPerServiceTime(1);
//        VehicleType vehicleType = vehicleTypeBuilder.build();
//
//        try {
//            for(Long s : runningSubscribers) {
//                Enumerator e = enumeratorDao.queryForId(s);
//                VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(String.valueOf(e.id));
//
//                CensusBlock bs = censusBlockDao.queryForId(e.depot);
//                Location loc = Location.Builder.newInstance()
//                        .setId(String.valueOf(bs.id))
//                        .setCoordinate(Coordinate.newInstance(bs.lon, bs.lat))
//                        .build();
//                builder.setStartLocation(loc);
//
//                builder.setType(vehicleType);
//                VehicleImpl vehicle = builder.build();
//                vrpBuilder.addVehicle(vehicle);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            QueryBuilder<CensusBlock, Long> qb = censusBlockDao.queryBuilder();
//            List<CensusBlock> bses = qb.where().isNull("ASSIGNEDTO").query();
//            for(CensusBlock bs : bses) {
//                Service.Builder builder = Service.Builder.newInstance(String.valueOf(bs.id));
//                //builder.addSizeDimension(0, 1);
//                //builder.setTimeWindow(TimeWindow.newInstance(0.0, 0.0));
//                //builder.setServiceTime(Double.parseDouble(line[3]));
//
//                Location loc = Location.Builder.newInstance()
//                        .setId(String.valueOf(bs.id))
//                        .setCoordinate(Coordinate.newInstance(bs.lon, bs.lat))
//                        .build();
//                builder.setLocation(loc);
//
//                Service node = builder.build();
//                vrpBuilder.addJob(node);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
//                    .newInstance(true);
//
//            List<DistanceMatrix> durMatrix = matrixDao.queryForAll();
//            for(DistanceMatrix m : durMatrix) {
//                costMatrixBuilder.addTransportDistance(String.valueOf(m.locationA), String.valueOf(m.locationB), m.distance);
//                costMatrixBuilder.addTransportTime(String.valueOf(m.locationA), String.valueOf(m.locationB), m.duration);
//            }
//
//            VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
//            vrpBuilder.setRoutingCost(costMatrix);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//        VehicleRoutingProblem problem = vrpBuilder.build();
//
//        VehicleRoutingAlgorithm algorithm = createAlgorithm(problem);
//        algorithm.setMaxIterations(100);
//
//        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
//        VehicleRoutingProblemSolution solution = Solutions.bestOf(solutions);
//
//
//        Map<Long, VehicleRoute> routeMap = new HashMap();
//        for(VehicleRoute route : solution.getRoutes()) {
//            routeMap.put(Long.valueOf(route.getVehicle().getId()), route);
//        }
//
//        for(int s=0; s<runningSubscribers.size(); s++) {
//            if(routeMap.keySet().contains(runningSubscribers.get(s))) {
//                Map<String, Object> currPath = new HashMap();
//
//                final Location vehicleLoc = routeMap.get(runningSubscribers.get(s)).getVehicle().getStartLocation();
//                currPath.put("enumerator", routeMap.get(runningSubscribers.get(s)).getVehicle().getId());
//                currPath.put("depot", vehicleLoc.getId());
//                currPath.put("depot-coord", new Double[] {vehicleLoc.getCoordinate().getY(), vehicleLoc.getCoordinate().getX()});
//
//                if(routeMap.get(runningSubscribers.get(s)).getActivities().size() > 0) {
//                    final TourActivity activity = routeMap.get(runningSubscribers.get(s)).getActivities().get(0);
//                    currPath.put("customer", activity.getLocation().getId());
//                    currPath.put("customer-coord", new Double[] {activity.getLocation().getCoordinate().getY(),
//                            activity.getLocation().getCoordinate().getX()});
//
//                    try {
//                        List<DistanceMatrix> mx = matrixDao.queryForMatching(new DistanceMatrix() {{
//                            locationA = Long.parseLong(vehicleLoc.getId());
//                            locationB = Long.parseLong(activity.getLocation().getId());
//                        }});
//                        currPath.put("duration", mx.size() > 0 ? mx.get(0).duration : 0.0);
//
//                        CensusBlock bs = censusBlockDao.queryForId(Long.valueOf(activity.getLocation().getId()));
//                        currPath.put("service-time", bs.serviceTime);
//
//                        bs.assignedTo = Long.parseLong(routeMap.get(runningSubscribers.get(s)).getVehicle().getId());
//                        censusBlockDao.update(bs);
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                runningAsyncResponses.get(s).resume(currPath);
//            }
//        }
    }
}
