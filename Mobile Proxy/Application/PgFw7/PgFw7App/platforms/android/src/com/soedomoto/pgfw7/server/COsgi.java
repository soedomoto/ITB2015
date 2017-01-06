package com.soedomoto.pgfw7.server;

import com.soedomoto.pgfw7.ProxyApplication;
import com.soedomoto.pgfw7.util.StorageUtil;

import org.apache.cordova.LOG;
import org.knopflerfish.framework.Main;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by soedomoto on 6/17/16.
 */
public class COsgi {
    private static String TAG = COsgi.class.getSimpleName();

    private final String fwdir = "fwdir";
    private final String bundlesDir = "bundles";

    private Framework framework = null;
    private List<Bundle> bundles = new ArrayList<Bundle>();

    public COsgi() {
        //  Create Framework Directory
        File fwdir = new File(ProxyApplication.getAppContext().getFilesDir().getAbsolutePath() +
                File.separator + this.fwdir);

        /*if(fwdir.exists()) {
            StorageUtil.delete(fwdir);
        }*/

        fwdir.mkdirs();
        LOG.e(TAG, String.format("OSGi Framework storage %s", fwdir.getAbsolutePath()));

        // Felix Config
        Map configMap = new HashMap();
        configMap.put(Constants.FRAMEWORK_STORAGE, fwdir.getAbsolutePath());
        configMap.put("felix.embedded.execution", "true");
        configMap.put("org.osgi.framework.startlevel.beginning", "1");

        framework = new Main().getFrameworkFactory().newFramework(configMap);

        /*this.bundles = this.autoInstallBundles();*/
    }

    public List<Bundle> autoInstallBundles() {
        BundleContext bc = this.framework.getBundleContext();

        File extAppFilesDir = StorageUtil.getExternalFilesDir();
        File bundlesDir = new File(extAppFilesDir.getAbsolutePath() + File.separator + this.bundlesDir);
        if(!bundlesDir.exists()) {
            bundlesDir.mkdirs();
        }

        LOG.e(TAG, String.format("OSGi Bundles Directory %s", bundlesDir.getAbsolutePath()));

        File[] jars = bundlesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
            if (filename.endsWith("jar")) return true;
            return false;
            }
        });

        List<Bundle> bundles = new ArrayList<Bundle>();
        for(File jar : jars) {
            try {
                Bundle bundle = bc.installBundle(jar.getAbsolutePath(), new FileInputStream(jar.getAbsolutePath()));
                bundles.add(bundle);

                LOG.e(TAG, String.format("Bundle %s is installed successfully", jar.getAbsoluteFile()));
            } catch (BundleException e) {
                LOG.e(TAG, String.format("Installing bundle %s is failed", jar.getAbsoluteFile()), e);
            } catch (FileNotFoundException e) {
                LOG.e(TAG, String.format("Installing bundle %s is failed", jar.getAbsoluteFile()), e);
            }
        }

        return bundles;
    }

    public Framework getFramework() {
        return framework;
    }

    public Bundle[] getBundles() {
        return framework.getBundleContext().getBundles();
    }

    public Bundle getBundle(int id) {
        return framework.getBundleContext().getBundle(id);
    }
}
