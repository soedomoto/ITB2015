package com.soedomoto.sakernas.osgi;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.knopflerfish.framework.Main;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * Created by soedomoto on 10/30/16.
 */

public class Knopflerfish {
    public static final String TAG = Knopflerfish.class.getSimpleName();

    private final String _frameworkDir = "fwdir";
    private final Context appCtx;
    private Map _config = new HashMap();
    private Framework _framework;
    private boolean started = false;

    public Knopflerfish(Context appCtx) {
        this(appCtx, new HashMap());
    }

    public Knopflerfish(Context appCtx, Map config) {
        this.appCtx = appCtx;

        //  Create Framework Directory
        File fwdir = new File(appCtx.getFilesDir().getAbsolutePath() + File.separator +
                this._frameworkDir);
        fwdir.mkdirs();

        Log.i(TAG, String.format("OSGi Framework storage is %s", fwdir.getAbsolutePath()));

        // OSGi Framework Config
        _config.put(Constants.FRAMEWORK_STORAGE, fwdir.getAbsolutePath());
        _config.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "1");
        _config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, TextUtils.join(";", getAllPackages(
                new String[] {  "org.bouncycastle", "com.j256.ormlite", "org.h2", "com.google.gson",
                                "org.osgi.service.jdbc", "org.apache.commons", "org.eclipse.jetty",
                                "javax.servlet", "com.soedomoto.sakernas.model"}
            )));
        _config.putAll(config);

        _framework = new Main().getFrameworkFactory().newFramework(_config);

        try {
            _framework.init();
            Log.i(TAG, String.format("OSGi Framework is initialized"));
        } catch (BundleException e) {
            Log.e(TAG, String.format("OSGi Framework is failed to initialize"), e);
        }
    }

    private String[] getAllPackages(String[] parents) {
        ArrayList<String> packages = new ArrayList<String>();

        try {
            String packageCodePath = appCtx.getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
                String className = iter.nextElement();

                String[] classFrags = className.split("\\.");
                List<String> packageFrags = new ArrayList<>();
                for(int c=0; c<classFrags.length-1; c++) packageFrags.add(classFrags[c]);
                String packageName = TextUtils.join(".", packageFrags);

                boolean matchParent = false;
                for(String parent : parents) matchParent = matchParent || packageName.startsWith(parent);
                
                if(matchParent && !packages.contains(packageName)) {
                    packages.add(packageName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return packages.toArray(new String[packages.size()]);
    }

    public void start() throws BundleException {
        _framework.start();
        started = true;
    }

    public void stop() throws BundleException {
        _framework.stop();
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public Bundle installBundle(final String url) throws BundleException {
        return _framework.getBundleContext().installBundle(url);
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
        return _framework;
    }
}
