package com.soedomoto.sakernas.osgi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.soedomoto.validation.core.service.IValidation;

import org.knopflerfish.framework.Main;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.util.tracker.ServiceTracker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by soedomoto on 10/30/16.
 */

public class KfOsgi {
    private final String TAG = getClass().getSimpleName();

    private final String osgiFrameworkDir = "osgiFrameworkDir";
    private Framework osgiFramework;
    private boolean started = false;

    public KfOsgi(Context appCtx) {
        //  Create Framework Directory
        File fwdir = new File(appCtx.getFilesDir().getAbsolutePath() +
                File.separator + this.osgiFrameworkDir);

        fwdir.mkdirs();
        Log.i(TAG, String.format("OSGi Framework storage is %s", fwdir.getAbsolutePath()));

        // Felix Config
        Map configMap = new HashMap();
        configMap.put(Constants.FRAMEWORK_STORAGE, fwdir.getAbsolutePath());
        configMap.put("felix.embedded.execution", "true");
        configMap.put("org.osgi.osgiFramework.startlevel.beginning", "1");
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "com.soedomoto.validation.core");

        osgiFramework = new Main().getFrameworkFactory().newFramework(configMap);

        try {
            osgiFramework.init();
            Log.i(TAG, String.format("OSGi Framework is initialized"));
        } catch (BundleException e) {
            Log.e(TAG, String.format("OSGi Framework is failed to initialize"), e);
        }

        BundleContext bc = osgiFramework.getBundleContext();
    }

    public void start() throws BundleException {
        osgiFramework.start();
        started = true;
    }

    public void stop() throws BundleException {
        osgiFramework.stop();
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public Bundle installBundle(final String url) throws BundleException {
        return osgiFramework.getBundleContext().installBundle(url);
    }

    public void startBundle(Bundle bundle) throws BundleException {
        bundle.start();
    }

    public void stopBundle(Bundle bundle) throws BundleException {
        bundle.stop();
    }

    public void uninstallBundle(Bundle bundle) throws BundleException {
        bundle.uninstall();
    }

    public Framework getFramework() {
        return osgiFramework;
    }
}
