package com.soedomoto.vrp.solver;

import com.soedomoto.vrp.App;
import com.soedomoto.vrp.model.CensusBlock;

import java.sql.SQLException;

/**
 * Created by soedomoto on 03/02/17.
 */
public abstract class CoESVRPSolver extends AbstractVRPSolver implements Runnable, VRPSolver {
    public CoESVRPSolver(App app, String channel) throws SQLException {
        super(app, channel);
    }

    public void run() {
        int vSize = allEnumerators.size();
        float[] vXDepots = new float[vSize];
        float[] vYDepots = new float[vSize];
        float[] vDurations = new float[vSize];
        int[] vCapacities = new int[vSize];

        int iE = 0;
        for(Long eId : allEnumerators.keySet()) {
            long dId = allEnumerators.get(eId).getDepot();
            vXDepots[iE] = (float) allBses.get(dId).getLon();
            vYDepots[iE] = (float) allBses.get(dId).getLat();
            vDurations[iE] = 0;
            vCapacities[iE] = (unassignedBses.size() / allEnumerators.size()) + 1;
            iE++;
        }

        int cSize = unassignedBses.size();
        float[] cXDepots = new float[cSize];
        float[] cYDepots = new float[cSize];
        int[] cDemands = new int[cSize];

        iE = 0;
        for(CensusBlock c : unassignedBses.values()) {
            cXDepots[iE] = (float) c.getLon();
            cYDepots[iE] = (float) c.getLat();
            iE++;
        }


        final CoESVRPJNI coes = new CoESVRPJNI();
        coes.setVehicles(vSize, cXDepots, cYDepots, vDurations, vCapacities);
        coes.setCustomers(cSize, cXDepots, cYDepots, cDemands);
        coes.solve();
    }
}
