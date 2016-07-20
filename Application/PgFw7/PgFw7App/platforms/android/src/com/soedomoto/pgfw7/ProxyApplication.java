package com.soedomoto.pgfw7;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Soedomoto on 6/1/2016.
 */
public class ProxyApplication extends Application {
    private static final String TAG = ProxyApplication.class.getSimpleName();

    private static final File sdcard = Environment.getExternalStorageDirectory();
    private static final String workingDir = "working";
    private static final String optimizedDir = "optimized";

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ProxyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ProxyApplication.context;
    }

    public static File getWorkingDir() {
        File work = context.getDir(workingDir, Context.MODE_PRIVATE);
        return work;
    }

    public static File getOptimizedDir(File dexFile) {
        File optimized = new File(optimizedDir);
        optimized = context.getDir(optimized.toString(), Context.MODE_PRIVATE);
        optimized = new File(optimized, dexFile.getName());
        optimized.mkdir();

        return optimized;
    }

    public static File getSdcard() {
        return sdcard;
    }

}
