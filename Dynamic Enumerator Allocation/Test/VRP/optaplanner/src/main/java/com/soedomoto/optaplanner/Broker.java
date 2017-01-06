package com.soedomoto.optaplanner;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.DistanceType;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by soedomoto on 02/01/17.
 */
public class Broker implements Runnable {
    private final Solver<VehicleRoutingSolution> solver;
    private Map<Long, EnumeratorWorker> enumerators = new HashMap();

    private final DistanceType distanceType;
    private final String distanceUnitOfMeasurement;
    private final Map<Long, Location> locationMap = new HashMap();
    private final Map<Long, Depot> depotMap = new HashMap();
    private final Map<Long, Vehicle> vehicleMap = new HashMap();
    private final Map<Long, Customer> customerMap = new HashMap();
    private final HardSoftLongScore score;

    private List<Long> subscribes = new ArrayList();
    private Map<Long, List<Long>> visits = new HashMap();

    public Broker(VehicleRoutingSolution problem, Solver<VehicleRoutingSolution> solver) {
        this.solver = solver;

        // Extract data from problem
        distanceType = problem.getDistanceType();
        distanceUnitOfMeasurement = problem.getDistanceUnitOfMeasurement();
        for(Location l: problem.getLocationList()) locationMap.put(l.getId(), l);
        for(Depot d: problem.getDepotList()) depotMap.put(d.getId(), d);
        for(Vehicle v: problem.getVehicleList()) vehicleMap.put(v.getId(), v);
        for(Customer c: problem.getCustomerList()) {
            TCustomer tc = new TCustomer();
            tc.setId(c.getId());
            tc.setLocation(c.getLocation());
            tc.setDemand(c.getDemand());
            customerMap.put(c.getId(), tc);
        }
        score = problem.getScore();

        // Create worker for each enumerator
        SubscribeListener listener = new SubscribeListener() {
            public boolean subscribeSolution(Long enumeratorId) {
                subscribes.add(enumeratorId);
                return true;
            }

            public void publishVisit(Long enumeratorId, Long customerId) {
                if(! visits.keySet().contains(enumeratorId)) visits.put(enumeratorId, new ArrayList());
                visits.get(enumeratorId).add(customerId);

                customerMap.remove(customerId);
                locationMap.remove(customerId);
            }
        };

        for(Vehicle en: problem.getVehicleList()) {
            enumerators.put(en.getId(), new EnumeratorWorker(en.getId(), listener));
        }
    }

    private VehicleRoutingSolution createProblem(List<Long> subscribes) {
        VehicleRoutingSolution problem = new VehicleRoutingSolution();
        problem.setDistanceType(distanceType);
        problem.setDistanceUnitOfMeasurement(distanceUnitOfMeasurement);
        problem.setScore(score);

        List<Vehicle> vehicleList = new ArrayList();
        List<Depot> depotList = new ArrayList();
        for(Long vId: vehicleMap.keySet()) {
            if(subscribes.contains(vId)) {
                Long lastVisit = vId;
                if(visits.keySet().contains(vId))
                    visits.get(vId).get(visits.get(vId).size() - 1);

                Depot d= new Depot();
                d.setId(vId);
                d.setLocation(locationMap.get(lastVisit));
                depotList.add(d);

                Vehicle v = vehicleMap.get(vId);
                v.setDepot(d);
                vehicleList.add(v);
            }
        }

        problem.setLocationList(new ArrayList(locationMap.values()));
        problem.setDepotList(depotList);
        problem.setVehicleList(vehicleList);
        problem.setCustomerList(new ArrayList(customerMap.values()));

        for(int v=0; v<problem.getVehicleList().size(); v++) {
            problem.getVehicleList().get(v).setDepot(problem.getDepotList().get(v));
        }

        return problem;
    }

    private VehicleRoutingSolution createInitialProblem() {
        return createProblem(new ArrayList(vehicleMap.keySet()));
    }

    private VehicleRoutingSolution createSolution(final VehicleRoutingSolution problem) {
        final VehicleRoutingSolution solution = solver.solve(problem);
        return solution;
    }

    private VehicleRoutingSolution createInitialSolution(final VehicleRoutingSolution problem) {
        return createSolution(problem);
    }

    public void run() {
        ExecutorService ex = Executors.newCachedThreadPool();
        for(long enId: enumerators.keySet()) {
            ex.execute(enumerators.get(enId));
        }

        VehicleRoutingSolution solution = createInitialSolution(createInitialProblem());
        for (Vehicle vehicle : solution.getVehicleList()) {
            List<Customer> customers = new ArrayList();
            for (Customer customer : solution.getCustomerList()) {
                if (customer.getPreviousStandstill() != null && customer.getVehicle() == vehicle) {
                    customers.add(customer);
                }
            }
            enumerators.get(vehicle.getId()).publishCustomers(customers);
        }

        boolean processing = false;
        while (true) {
            if(customerMap.size() == 0) break;

            try {
                if(subscribes.size() > 0 && !processing) {
                    processing = true;

                    List<Long> processings = new ArrayList(subscribes);
                    subscribes.removeAll(subscribes);

                    solution = createInitialSolution(createProblem(processings));
                    for (Vehicle vehicle : solution.getVehicleList()) {
                        List<Customer> customers = new ArrayList();
                        for (Customer customer : solution.getCustomerList()) {
                            if (customer.getPreviousStandstill() != null && customer.getVehicle() == vehicle) {
                                customers.add(customer);
                            }
                        }
                        enumerators.get(vehicle.getId()).publishCustomers(customers);
                    }

                    processing = false;
                } else {
                    Thread.sleep((long) 10000);
                }
            } catch (InterruptedException e) {

            }
        }

        ex.shutdown();
    }
}
