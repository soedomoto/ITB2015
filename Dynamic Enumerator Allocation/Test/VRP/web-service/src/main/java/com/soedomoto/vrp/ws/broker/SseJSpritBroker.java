package com.soedomoto.vrp.ws.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.DistanceMatrix;
import com.soedomoto.vrp.ws.model.Enumerator;
import com.soedomoto.vrp.ws.model.Subscriber;
import org.apache.log4j.Logger;

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
public class SseJSpritBroker extends SseAbstractBroker implements Runnable {
    private final static Logger LOG = Logger.getLogger(SseAbstractBroker.class.getName());

    private Map<Long, Map<Long, Double>> durationMatrix = new HashMap();
    private Map<Long, Map<Long, Double>> distanceMatrix = new HashMap();

    public SseJSpritBroker(ScheduledExecutorService executor, ServletContext context) {
        super(executor, context);
    }

    @Override
    public void run() {
        String serverLogDir = (String) context.getAttribute("serverLogDir");

        try {
            LOG.debug(String.format("Processing subscribe(s) from %s", asyncContextMap.keySet()));

            List<Subscriber> workingSubscribers = new ArrayList();
            List<Long> workingSubscriberEnumeratorIds = new ArrayList();
            for(Long sId : asyncContextMap.keySet()) {
                Subscriber s = subscriberDao.queryForId(sId);
                workingSubscribers.add(s);
                workingSubscriberEnumeratorIds.add(s.getSubscriber());
            }

            List<Enumerator> es = enumeratorDao.queryForAll();
            Map<Long, Enumerator> allEnumerators = new HashMap();
            Map<Long, Enumerator> workingEnumerators = new HashMap();
            for(Enumerator e : es) {
                allEnumerators.put(e.getId(), e);
                if(workingSubscriberEnumeratorIds.contains(e.getId())) workingEnumerators.put(e.getId(), e);
            }

            List<CensusBlock> bses = censusBlockDao.queryForAll();
            Map<Long, CensusBlock> allBses = new HashMap();
            Map<Long, CensusBlock> workingBses = new HashMap();
            for(CensusBlock bs : bses) {
                allBses.put(bs.getId(), bs);
                if(bs.getAssignedTo() == null) workingBses.put(bs.getId(), bs);
            }

            List<DistanceMatrix> dm = matrixDao.queryForAll();
            for(DistanceMatrix m : dm) {
                if(!durationMatrix.keySet().contains(m.getLocationA())) durationMatrix.put(m.getLocationA(), new HashMap());
                durationMatrix.get(m.getLocationA()).put(m.getLocationB(), m.getDuration());

                if(!distanceMatrix.keySet().contains(m.getLocationA())) distanceMatrix.put(m.getLocationA(), new HashMap());
                distanceMatrix.get(m.getLocationA()).put(m.getLocationB(), m.getDistance());
            }


            // Build problem
            VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance().setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);


            for(CensusBlock bs : workingBses.values()) {
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
            vehicleTypeBuilder.addCapacityDimension(0, (workingBses.size() / allEnumerators.size()) + 1);
            //vehicleTypeBuilder.setCostPerDistance(1.0);
            vehicleTypeBuilder.setCostPerDistance(0);
            vehicleTypeBuilder.setCostPerTransportTime(1);
            vehicleTypeBuilder.setCostPerServiceTime(1);
            VehicleType vehicleType = vehicleTypeBuilder.build();

            Map<Long, Enumerator> ens = allEnumerators;
            if(workingBses.size() < allEnumerators.size()) ens = workingEnumerators;

            for(Enumerator e : ens.values()) {
                VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(String.valueOf(e.getId()));

                CensusBlock bs = allBses.get(e.getDepot()); //censusBlockDao.queryForId(e.depot);
                Location loc = Location.Builder.newInstance()
                        .setId(String.valueOf(bs.getId()))
                        .setCoordinate(Coordinate.newInstance(bs.getLon(), bs.getLat()))
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

            LOG.debug(String.format("Using: %s enumerator(s), %s location(s)", ens.size(), workingBses.size()));


            VehicleRoutingProblem problem = vrpBuilder.build();

            int numProcessors = Runtime.getRuntime().availableProcessors();
            int maxIteration = 2000;

            VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
                    .setProperty(Jsprit.Parameter.THREADS, String.valueOf(numProcessors))
                    .buildAlgorithm();
            algorithm.setMaxIterations(maxIteration);

            LOG.debug(String.format("Finding solution using %s thread(s) and %s iteration(s)", numProcessors, maxIteration));

            Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

            LOG.debug(String.format("%s solution(s) found. Finding the best", solutions.size()));

            VehicleRoutingProblemSolution solution = this.findBest(solutions);


            Map<Long, VehicleRoute> routeMap = new HashMap();
            for(VehicleRoute route : solution.getRoutes()) {
                routeMap.put(Long.valueOf(route.getVehicle().getId()), route);
            }

            for(Subscriber s : workingSubscribers) {
                if(routeMap.keySet().contains(s.getSubscriber())) {
                    Map<String, Object> currPath = new HashMap();

                    final Location vehicleLoc = routeMap.get(s.getSubscriber()).getVehicle().getStartLocation();
                    currPath.put("enumerator", routeMap.get(s.getSubscriber()).getVehicle().getId());
                    currPath.put("depot", vehicleLoc.getId());
                    currPath.put("depot-coord", new Double[] {vehicleLoc.getCoordinate().getY(),
                            vehicleLoc.getCoordinate().getX()});

                    Iterator<Job> jobIt = routeMap.get(s.getSubscriber()).getTourActivities().getJobs().iterator();
                    if(jobIt.hasNext()) {
                        Job job = jobIt.next();
                        if(job instanceof Service) {
                            Service activity = (Service) job;
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
                            currPath.put("service-time", bs.getServiceTime());

                            bs.setAssignedTo(s.getSubscriber());
                            bs.setAssignDate(new Date());
                            censusBlockDao.update(bs);

                            s.setProcessed(true);
                            subscriberDao.update(s);
                        }
                    }

//                    if(routeMap.get(s.getSubscriber()).getActivities().size() > 0) {
//                        final TourActivity activity = routeMap.get(s.getSubscriber()).getActivities().get(0);
//                        currPath.put("customer", activity.getLocation().getId());
//                        currPath.put("customer-coord", new Double[] {activity.getLocation().getCoordinate().getY(),
//                                activity.getLocation().getCoordinate().getX()});
//
//                        double duration = 0.0;
//                        long a = Long.parseLong(vehicleLoc.getId());
//                        long b = Long.parseLong(activity.getLocation().getId());
//                        if(durationMatrix.keySet().contains(a)) {
//                            if(durationMatrix.get(a).keySet().contains(b)) {
//                                duration = durationMatrix.get(a).get(b);
//                            }
//                        }
//                        currPath.put("duration", duration);
//
//                        CensusBlock bs = allBses.get(Long.valueOf(activity.getLocation().getId())); //censusBlockDao.queryForId(Long.valueOf(activity.getLocation().getId()));
//                        currPath.put("service-time", bs.getServiceTime());
//
//                        bs.setAssignedTo(s.getSubscriber());
//                        bs.setAssignDate(new Date());
//                        censusBlockDao.update(bs);
//
//                        s.setProcessed(true);
//                        subscriberDao.update(s);
//                    }

                    ObjectMapper mapper = new ObjectMapper();
                    String strCurrPath = mapper.writeValueAsString(currPath);

                    try {
                        printWriterMap.get(s.getId()).write(String.format("event: %s\ndata: %s\n\n", "location", strCurrPath));
                        printWriterMap.get(s.getId()).flush();
                    } catch (Exception e) {
                        s.setResponse(strCurrPath);
                        subscriberDao.update(s);
                    }

                    asyncContextMap.get(s.getId()).complete();
                    asyncContextMap.remove(s.getId());
                    printWriterMap.remove(s.getId());

                    try {
                        File clientLogFile = new File(serverLogDir + File.separator + currPath.get("enumerator") + ".log");
                        clientLogFile.getParentFile().mkdirs();
                        PrintWriter out = new PrintWriter(new FileWriter(clientLogFile, true));
                        out.println(String.format("%s,%s,%s,%s", currPath.get("depot"), currPath.get("customer"),
                                currPath.get("duration"), currPath.get("service-time")));
                        out.flush();
                    } catch (IOException e) { }
                }
            }

            for(long rid : asyncContextMap.keySet()) {
                asyncContextMap.get(rid).complete();
                asyncContextMap.remove(rid);
                printWriterMap.remove(rid);
            }

            listener.finish();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
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
                    if(durationMatrix.keySet().contains(a)) {
                        if(durationMatrix.get(a).keySet().contains(b)) {
                            duration += durationMatrix.get(a).get(b);
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
