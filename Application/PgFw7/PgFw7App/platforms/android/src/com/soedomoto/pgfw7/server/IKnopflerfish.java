package com.soedomoto.pgfw7.server;

import com.soedomoto.pgfw7.ProxyApplication;
import com.soedomoto.pgfw7.util.StorageUtil;

import org.apache.cordova.LOG;
import org.knopflerfish.framework.Main;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleRevision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Soedomoto on 6/1/2016.
 */
public class IKnopflerfish {
    private static String TAG = IKnopflerfish.class.getSimpleName();

    private final String fwdir = "fwdir";
    private final String bundlesDir = "bundles";
    private final Framework framework;
    private final Map<String,String> config;
    private boolean started = false;

    public IKnopflerfish() {
        File fwdir = new File(ProxyApplication.getAppContext().getFilesDir().getAbsolutePath() +
                File.separator + this.fwdir);
        if(!fwdir.exists()) {
            fwdir.mkdirs();
        }

        LOG.e(TAG, String.format("IKnopflerfish storage %s", fwdir.getAbsolutePath()));

        this.config = new HashMap<String,String>();
        this.getConfig().put(Constants.FRAMEWORK_STORAGE, fwdir.getAbsolutePath());

        this.framework = new Main().getFrameworkFactory().newFramework(getConfig());
    }

    public void startFramework() throws BundleException {
        this.framework.init();
        this.framework.start();
        FrameworkStartLevel fsl = (FrameworkStartLevel) framework.adapt(FrameworkStartLevel.class);
        if (fsl!=null) {
            fsl.setStartLevel(1, new FrameworkListener[]{});
            fsl.setInitialBundleStartLevel(1);
        }

        this.started = true;
        LOG.e(TAG, String.format("Framework started with start level %s", fsl.getStartLevel()));
    }

    public void stopFramework() throws Throwable {
        this.framework.stop();

//        while (true) {
//            try {
//                FrameworkEvent stopEvent = this.framework.waitForStop(0L);
//                switch (stopEvent.getType()) {
//                    case FrameworkEvent.STOPPED:
//                        this.started = false;
//                        LOG.e(TAG, "Framework is stopped");
//                        return;
//                    case FrameworkEvent.STOPPED_UPDATE:
//                        break;
//                    case FrameworkEvent.STOPPED_BOOTCLASSPATH_MODIFIED:
//                        this.started = false;
//                        LOG.e(TAG, "Framework is stopped");
//                        return; // TODO
//                    case FrameworkEvent.ERROR:
//                        this.started = false;
//                        throw stopEvent.getThrowable();
//                    case FrameworkEvent.WAIT_TIMEDOUT:
//                        break;
//                }
//            } catch (InterruptedException e) {}
//        }

    }

    public void scanBundles() {
        BundleContext bc = this.framework.getBundleContext();

        File extAppFilesDir = StorageUtil.getExternalFilesDir();
        File bundlesDir = new File(extAppFilesDir.getAbsolutePath() + File.separator + this.bundlesDir);
        if(!bundlesDir.exists()) {
            bundlesDir.mkdirs();
        }

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

                LOG.e(TAG, String.format("Install bundle %s successfully", jar.getAbsoluteFile()));
            } catch (BundleException e) {
                LOG.e(TAG, String.format("Install bundle %s is failed", jar.getAbsoluteFile()), e);
            } catch (FileNotFoundException e) {
                LOG.e(TAG, String.format("Install bundle %s is failed", jar.getAbsoluteFile()), e);
            }
        }

        for(Bundle bundle : bundles) {
            BundleRevision br = (BundleRevision) bundle.adapt(BundleRevision.class);
            boolean frag = (br.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0;
            if (!frag) {
                try {
                    bundle.start(Bundle.START_ACTIVATION_POLICY);
                    LOG.e(TAG, String.format("Start bundle %s successfully", bundle.getLocation()));
                } catch (BundleException e) {
                    LOG.e(TAG, String.format("Start bundle %s failed", bundle.getLocation()), e);
                }
            }
        }
    }

    public Framework getFramework() {
        return framework;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public boolean isRunning() {
        return this.started;
    }
}
