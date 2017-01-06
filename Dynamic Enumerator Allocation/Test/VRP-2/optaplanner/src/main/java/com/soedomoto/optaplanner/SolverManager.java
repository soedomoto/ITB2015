package com.soedomoto.optaplanner;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingImporter;
import org.slf4j.Logger;

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
    private static final String IMPORT_DATASET = "optaplanner/src/main/resources/enumeration_problem_octaplanner-n50-k10.vrp";
    private static final String EXPORT_DATASET = "optaplanner/src/main/resources/enumeration_problem_octaplanner-n50-k10.xml";

    public static void main(String[] args) throws MalformedURLException {
        String in = new File(IMPORT_DATASET).getAbsolutePath();
        String out = new File(EXPORT_DATASET).getAbsolutePath();

        VehicleRoutingImporter importer = new VehicleRoutingImporter(true);
        importer.convert(in, out);


        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);

        // Always terminate a solver after 2 minutes
        TerminationConfig terminationConfig = new TerminationConfig();
        // terminationConfig.setStepCountLimit(500);
        terminationConfig.setMinutesSpentLimit(5L);
        solverFactory.getSolverConfig().setTerminationConfig(terminationConfig);

        final Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();
        solver.addEventListener(new SolverEventListener<VehicleRoutingSolution>() {
            @Override
            public void bestSolutionChanged(BestSolutionChangedEvent<VehicleRoutingSolution> event) {
                VehicleRoutingSolution bestSolution = event.getNewBestSolution();
                System.out.println("Change to : " + bestSolution.getScore().toString());
            }
        });


        URL unsolvedSolutionURL = new File(IMPORT_DATASET).toURI().toURL();

        VehicleRoutingSolution unsolvedSolution = (VehicleRoutingSolution) new VehicleRoutingImporter(true)
                .readSolution(unsolvedSolutionURL);

        ExecutorService executor = Executors.newFixedThreadPool(5); // Only 2 because the other examples have their own Executor
        executor.submit(new Runnable() {
            @Override
            public void run() {
                VehicleRoutingSolution solution = solver.solve(unsolvedSolution);
                for (Vehicle vehicle : solution.getVehicleList()) {
                    Location depotLocation = vehicle.getDepot().getLocation();
                    String row = depotLocation.getName() + " : ";

                    Customer customer = vehicle.getNextCustomer();
                    while (customer != null) {
                        Location customerLocation = customer.getLocation();
                        row += customerLocation.getName() + " : ";

                        customer = customer.getNextCustomer();
                    }

                    System.out.println(row);
                }

                executor.shutdown();
            }
        });
    }

}
