package com.soedomoto.vrp.ws.broker;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by soedomoto on 08/01/17.
 */
public class JSpritBroker extends AbstractBroker implements Runnable {
    public JSpritBroker(ScheduledExecutorService executor, ServletContext context) {
        super(executor, context);
    }

    @Override
    public void run() {
        String serverLogDir = (String) context.getAttribute("serverLogDir");

        try {
            List<Subscriber> workingSubscribers = new ArrayList();
            for(Long sId : asyncResponseMap.keySet()) {
                workingSubscribers.add(subscriberDao.queryForId(sId));
            }

            //List<Subscriber> workingSubscribers = subscriberDao.queryBuilder().where().eq("is_processed", false).query();

            List<Enumerator> es = enumeratorDao.queryForAll();
            Map<Long, Enumerator> allEnumerators = new HashMap();
            for(Enumerator e : es) allEnumerators.put(e.id, e);

            List<CensusBlock> bses = censusBlockDao.queryForAll();
            Map<Long, CensusBlock> allBses = new HashMap();
            Map<Long, CensusBlock> workingBses = new HashMap();
            for(CensusBlock bs : bses) {
                allBses.put(bs.id, bs);
                if(bs.assignedTo == null) workingBses.put(bs.id, bs);
            }

            List<DistanceMatrix> dm = matrixDao.queryForAll();
            Map<Long, Map<Long, Double>> durationMatrix = new HashMap();
            Map<Long, Map<Long, Double>> distanceMatrix = new HashMap();
            for(DistanceMatrix m : dm) {
                if(!durationMatrix.keySet().contains(m.locationA)) durationMatrix.put(m.locationA, new HashMap());
                durationMatrix.get(m.locationA).put(m.locationB, m.duration);

                if(!distanceMatrix.keySet().contains(m.locationA)) distanceMatrix.put(m.locationA, new HashMap());
                distanceMatrix.get(m.locationA).put(m.locationB, m.distance);
            }


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
            vehicleTypeBuilder.addCapacityDimension(0, workingBses.size() / allEnumerators.size());
            //vehicleTypeBuilder.setCostPerDistance(1.0);
            vehicleTypeBuilder.setCostPerDistance(0);
            vehicleTypeBuilder.setCostPerTransportTime(1);
            vehicleTypeBuilder.setCostPerServiceTime(1);
            VehicleType vehicleType = vehicleTypeBuilder.build();

            for(Enumerator e : allEnumerators.values()) {
                VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(String.valueOf(e.id));

                CensusBlock bs = allBses.get(e.depot); //censusBlockDao.queryForId(e.depot);
                Location loc = Location.Builder.newInstance()
                        .setId(String.valueOf(bs.id))
                        .setCoordinate(Coordinate.newInstance(bs.lon, bs.lat))
                        .build();
                builder.setStartLocation(loc);

                builder.setType(vehicleType);
                VehicleImpl vehicle = builder.build();
                vrpBuilder.addVehicle(vehicle);
            }


            VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
                    .newInstance(true);

            for(Long a : durationMatrix.keySet()) {
                for(Long b : durationMatrix.get(a).keySet()) {
                    costMatrixBuilder.addTransportDistance(String.valueOf(a), String.valueOf(b), distanceMatrix.get(a).get(b));
                    costMatrixBuilder.addTransportTime(String.valueOf(a), String.valueOf(b), durationMatrix.get(a).get(b));
                }
            }

            VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
            vrpBuilder.setRoutingCost(costMatrix);


            VehicleRoutingProblem problem = vrpBuilder.build();

            VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
                    .setProperty(Jsprit.Parameter.THREADS, String.valueOf(Runtime.getRuntime().availableProcessors()))
                    .buildAlgorithm();
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

                        double duration = 0.0;
                        long a = Long.parseLong(vehicleLoc.getId());
                        long b = Long.parseLong(activity.getLocation().getId());
                        if(durationMatrix.keySet().contains(a)) {
                            if(durationMatrix.get(a).keySet().contains(b)) {
                                duration = durationMatrix.get(a).get(b);
                            }
                        }
                        currPath.put("duration", duration);

                        CensusBlock bs = allBses.get(Long.valueOf(activity.getLocation().getId())); //censusBlockDao.queryForId(Long.valueOf(activity.getLocation().getId()));
                        currPath.put("service-time", bs.serviceTime);

                        bs.assignedTo = s.subscriber;
                        censusBlockDao.update(bs);

                        s.isProcessed = true;
                        subscriberDao.update(s);
                    }

                    asyncResponseMap.get(s.id).resume(currPath);
                    asyncResponseMap.remove(s.id);

                    try {
                        File clientLogFile = new File(serverLogDir + File.separator + currPath.get("enumerator") + ".log");
                        clientLogFile.getParentFile().mkdirs();
                        PrintWriter out = new PrintWriter(new FileWriter(clientLogFile, true));
                        out.println(String.format("%s,%s,%s,%s", currPath.get("depot"), currPath.get("customer"),
                                currPath.get("duration"), currPath.get("service-time")));
                        out.flush();
                    } catch (IOException e) {}
                }
            }

            listener.finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
