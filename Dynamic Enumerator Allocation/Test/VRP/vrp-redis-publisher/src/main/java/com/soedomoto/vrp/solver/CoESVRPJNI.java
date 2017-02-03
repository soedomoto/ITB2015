package com.soedomoto.vrp.solver;

/**
 * Created by soedomoto on 03/02/17.
 */
public class CoESVRPJNI {
    static {
        System.loadLibrary("lib_coes");
        System.loadLibrary("lib_jcoes");
    }

    public native void setVehicles(int size, float[] xDepots, float[] yDepots, float[] durations, int[] capacity);
    public native void setCustomers(int size, float[] xPoints, float[] yPoints, int[] demand);
    public native int solve();
}
