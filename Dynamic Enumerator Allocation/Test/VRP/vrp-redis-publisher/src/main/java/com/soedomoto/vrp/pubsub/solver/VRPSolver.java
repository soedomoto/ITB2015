package com.soedomoto.vrp.pubsub.solver;

import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;

/**
 * Created by soedomoto on 22/01/17.
 */
public interface VRPSolver {
    public void onStarted(String channel);
    public void onSolution(String channel, Vehicle routeVehicle, Service activity, double duration, double serviceTime);
    public void onFinished(String channel);
}
