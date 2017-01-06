package com.soedomoto.optaplanner;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingImporter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by soedomoto on 23/12/16.
 */
public class SolverManager {
    private static final String SOLVER_CONFIG = "solver-config.xml";
    private static final String IMPORT_DATASET = "optaplanner/src/main/resources/pessel-road-time-n182-k15.vrp";

    public static void main(String[] args) throws MalformedURLException {
        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);

        final Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();
        solver.addEventListener(new SolverEventListener<VehicleRoutingSolution>() {
            public void bestSolutionChanged(BestSolutionChangedEvent<VehicleRoutingSolution> event) {
                VehicleRoutingSolution bestSolution = event.getNewBestSolution();
                System.out.println("Change to : " + bestSolution.getScore().toString());
            }
        });


        URL unsolvedSolutionURL = new File(IMPORT_DATASET).toURI().toURL();
        final VehicleRoutingSolution unsolvedSolution = (VehicleRoutingSolution) new VehicleRoutingImporter(true)
                .readSolution(unsolvedSolutionURL);
        for(int v=0; v<unsolvedSolution.getVehicleList().size(); v++) {
            unsolvedSolution.getVehicleList().get(v).setDepot(unsolvedSolution.getDepotList().get(v));
        }

        ExecutorService executor = Executors.newFixedThreadPool(2); // Only 2 because the other examples have their own Executor
        executor.submit(new Runnable() {
            public void run() {
                VehicleRoutingSolution solution = solver.solve(unsolvedSolution);
                for (Vehicle vehicle : solution.getVehicleList()) {
                    String row = vehicle.getDepot().getLocation().getName() + " -> ";
                    for (Customer customer : solution.getCustomerList()) {
                        if (customer.getPreviousStandstill() != null && customer.getVehicle() == vehicle) {
                            row += customer.getLocation().getName() + " -> ";
                        }
                    }

                    System.out.println(row);
                }
            }
        });
        executor.shutdown();
    }

}
