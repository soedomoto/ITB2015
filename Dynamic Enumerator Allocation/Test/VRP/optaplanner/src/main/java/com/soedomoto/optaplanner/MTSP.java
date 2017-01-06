package com.soedomoto.optaplanner;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingImporter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by soedomoto on 02/01/17.
 */
public class MTSP {
    private static final String SOLVER_CONFIG = "solver-config.xml";
    private static final String DATASET = "optaplanner/src/main/resources/pessel-road-time-n182-k15.vrp";

    public static void main(String[] args) throws MalformedURLException {
        // Read dataset
        URL datasetURL = new File(DATASET).toURI().toURL();
        final VehicleRoutingSolution problem = (VehicleRoutingSolution) new VehicleRoutingImporter(true)
                .readSolution(datasetURL);

        // Define solver
        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        final Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();

        // Run Broker
        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.execute(new Broker(problem, solver));
        ex.shutdown();
    }
}
