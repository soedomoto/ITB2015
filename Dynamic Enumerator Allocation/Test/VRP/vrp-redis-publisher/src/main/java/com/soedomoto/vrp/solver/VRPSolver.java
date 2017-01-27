package com.soedomoto.vrp.solver;

import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.soedomoto.vrp.model.CensusBlock;

import java.util.Collection;
import java.util.List;

/**
 * Created by soedomoto on 22/01/17.
 */
public interface VRPSolver {
    public void onStarted(String channel, List<CensusBlock> depots, Collection<CensusBlock> locations);
    public void onSolution(String channel, Vehicle routeVehicle, Service activity, double duration, double serviceTime);
    public void onFinished(String channel, List<CensusBlock> depots, Collection<CensusBlock> locations);
}
