package com.soedomoto.vrp.ws;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.DistanceMatrix;
import com.soedomoto.vrp.ws.model.Enumerator;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.DistanceType;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;

import javax.servlet.ServletContext;
import javax.ws.rs.container.AsyncResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by soedomoto on 07/01/17.
 */
public class Broker implements Runnable {
    private final ScheduledExecutorService executor;
    private final ServletContext context;
    private final Dao<Enumerator, Long> enumeratorDao;
    private final Dao<CensusBlock, Long> censusBlockDao;
    private final Dao<DistanceMatrix, Long> matrixDao;

    private Long startTask;

    private List<Long> enumerators = new LinkedList();
    private List<AsyncResponse> asyncResponses = new LinkedList();
    private ScheduledFuture<?> currTask;
    private BrokerListener listener;

    public Broker(ScheduledExecutorService executor, ServletContext context) {
        this.executor = executor;
        this.context = context;

        enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");
        censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        matrixDao = (Dao<DistanceMatrix, Long>) context.getAttribute("distanceMatrixDao");
    }

    private void dumpSolution(VehicleRoutingSolution obj, String label) throws IOException {
        String now = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        FileOutputStream fout = new FileOutputStream(String.format("output/%s_%s.ser", label, now));
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(obj);
    }

    public void run() {
        LinkedList<Long> runningEnumerators = new LinkedList(enumerators);
        LinkedList<AsyncResponse> runningAsyncResponses = new LinkedList(asyncResponses);

        enumerators = new LinkedList();
        asyncResponses = new LinkedList();
        startTask = null;

        try {
            VehicleRoutingSolution problem = createProblem(runningEnumerators);
            Solver<VehicleRoutingSolution> solver = (Solver<VehicleRoutingSolution>) context.getAttribute("solver");
            VehicleRoutingSolution solution = solver.solve(problem);

            dumpSolution(problem, "problem");
            dumpSolution(solution, "solution");

            for(int e=0; e<runningEnumerators.size(); e++) {
                Map<String, Object> currPath = new HashMap();

                for (Customer customer : solution.getCustomerList()) {
                    if (customer.getPreviousStandstill() != null && customer.getVehicle().getId().equals(runningEnumerators.get(e))) {
                        CensusBlock bs = censusBlockDao.queryForId(customer.getLocation().getId());

                        currPath.put("depot", customer.getVehicle().getDepot().getLocation().getId());
                        currPath.put("depot-coord", new Double[] {customer.getVehicle().getDepot().getLocation().getLatitude(), customer.getVehicle().getDepot().getLocation().getLongitude()});
                        currPath.put("customer", customer.getLocation().getId());
                        currPath.put("customer-coord", new Double[] { customer.getLocation().getLatitude(), customer.getLocation().getLongitude() });
                        Double duration = ((RoadLocation) customer.getVehicle().getDepot().getLocation())
                                .getTravelDistanceMap().get(customer.getLocation());
                        currPath.put("duration", duration);
                        currPath.put("service-time", bs.serviceTime);

                        bs.assignedTo = customer.getVehicle().getId();
                        censusBlockDao.update(bs);

                        break;
                    }
                }

                runningAsyncResponses.get(e).resume(currPath);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(listener != null) listener.finish();
    }

    private VehicleRoutingSolution createProblem(LinkedList<Long> runningEnumerators) throws SQLException {
        Map<Long, Location> locations = new HashMap();
        List<Customer> customers = new ArrayList();
        List<Depot> depots = new ArrayList();
        List<Vehicle> vehicles = new ArrayList();

        List<Long> enumeratorLocs = new ArrayList();
        for(long eId : runningEnumerators) {
            Enumerator en = enumeratorDao.queryForId(eId);
            CensusBlock bs = censusBlockDao.queryForId(en.depot);

            Location loc = new RoadLocation();
            loc.setId(en.depot);
            loc.setName(String.valueOf(en.depot));
            loc.setLatitude(bs != null ? bs.lat : en.lat);
            loc.setLongitude(bs != null ? bs.lon : en.lon);

            enumeratorLocs.add(loc.getId());
            locations.put(loc.getId(), loc);
        }

        List<Long> bsLocs = new ArrayList();
        QueryBuilder<CensusBlock, Long> qb = censusBlockDao.queryBuilder();
        List<CensusBlock> censusBlocks = qb.where().isNull("ASSIGNEDTO").query();
        for(CensusBlock bs: censusBlocks) {
            Location loc = new RoadLocation();
            loc.setId(bs.id);
            loc.setName(String.valueOf(bs.id));
            loc.setLatitude(bs.lat);
            loc.setLongitude(bs.lon);

            bsLocs.add(loc.getId());
            locations.put(loc.getId(), loc);
        }

        Map<Long, Map<Long, Double>> dm = new HashMap();
        for(DistanceMatrix d : matrixDao.queryForAll()) {
            if(! dm.keySet().contains(d.locationA)) dm.put(d.locationA, new HashMap());
            dm.get(d.locationA).put(d.locationB, d.duration);
        }

        for(Location locA : locations.values()) {
            Map<RoadLocation, Double> travelDistanceMap = new LinkedHashMap();
            for(Location locB : locations.values()) {
                Double duration = dm.get(locA.getId()).get(locB.getId());
                if(locA == locB) duration = 0.0;

                travelDistanceMap.put((RoadLocation) locB, duration);
            }
            ((RoadLocation) locA).setTravelDistanceMap(travelDistanceMap);
        }

        int capacity = new Double(bsLocs.size() / enumeratorLocs.size()).intValue() + 1;
        for(Long enLocId : enumeratorLocs) {
            Location loc = locations.get(enLocId);

            Depot dep = new Depot();
            dep.setLocation(loc);
            dep.setId(loc.getId());
            depots.add(dep);

            Vehicle veh = new Vehicle();
            veh.setDepot(dep);
            veh.setId(loc.getId());
            veh.setCapacity(capacity);
            vehicles.add(veh);
        }

        for(Long bsLocId : bsLocs) {
            Location loc = locations.get(bsLocId);

            Customer cust = new Customer();
            cust.setId(loc.getId());
            cust.setLocation(loc);
            cust.setDemand(1);
            customers.add(cust);
        }

        VehicleRoutingSolution problem = new VehicleRoutingSolution();
        problem.setDistanceType(DistanceType.ROAD_DISTANCE);
        problem.setDistanceUnitOfMeasurement("sec");
        problem.setLocationList(new ArrayList(locations.values()));
        problem.setCustomerList(customers);
        problem.setDepotList(depots);
        problem.setVehicleList(vehicles);

        return problem;
    }

    public void subscribe(String enumeratorId, AsyncResponse asyncResponse) {
        if(! enumerators.contains(Long.valueOf(enumeratorId))) {
            enumerators.add(Long.valueOf(enumeratorId));
            asyncResponses.add(asyncResponse);
        }

        if(enumerators.size() > 0 && (currTask == null || (currTask != null && currTask.isDone()))) {
            listener = new BrokerListener() {
                @Override
                public void finish() {
                    if(enumerators.size() > 0) currTask = executor.schedule(Broker.this, 15, TimeUnit.SECONDS);
                }
            };

            currTask = executor.schedule(this, 15, TimeUnit.SECONDS);
        }
    }

    public static abstract interface BrokerListener {
        public void finish();
    }
}
