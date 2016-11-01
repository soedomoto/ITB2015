package com.soedomoto.pgfw7.server;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class JIOsgi {
    private static String TAG = JIOsgi.class.getSimpleName();

    private final Activity activity;
    private final COsgi osgi;
    private boolean isFrameworkRunning = false;

    public JIOsgi(Activity activity) {
        this.activity = activity;
        this.osgi = new COsgi();
    }

    @JavascriptInterface
    public String startFramework() {
        Framework framework = osgi.getFramework();
        try {
            framework.init();
            framework.start();
            LOG.e(TAG, "Start framework successfully");
            isFrameworkRunning = true;
            return String.valueOf(true);
        } catch (BundleException e) {
            LOG.e(TAG, "Start framework failed", e);
            return e.getCause().getMessage();
        }
    }

    @JavascriptInterface
    public String stopFramework() {
        Framework framework = osgi.getFramework();
        try {
            framework.stop();
            LOG.e(TAG, "Stop framework successfully");
            isFrameworkRunning = false;
            return String.valueOf(true);
        } catch (BundleException e) {
            LOG.e(TAG, "Stop framework failed", e);
            return e.getCause().getMessage();
        }
    }

    @JavascriptInterface
    public boolean isRunning() {
        return isFrameworkRunning;
    }

    @JavascriptInterface
    public String listBundles() {
        Bundle[] bundles = osgi.getBundles();

        JSONArray lstBundles = new JSONArray();
        for(Bundle b: bundles) {
            JSONObject props = new JSONObject();
            try {
                props.put("id", b.getBundleId());
                props.put("name", b.getHeaders().get("Bundle-Name"));
                props.put("context", b.getHeaders().get("Context-Path"));

                String stateString = "";
                switch (b.getState()) {
                    case 0x00000001: stateString = "Uninstalled"; break;
                    case 0x00000002: stateString = "Installed"; break;
                    case 0x00000004: stateString = "Resolved"; break;
                    case 0x00000008: stateString = "Starting"; break;
                    case 0x00000010: stateString = "Stopping"; break;
                    case 0x00000020: stateString = "Active"; break;
                }

                props.put("state", new JSONArray(String.format("[%s,%s]", b.getState(), stateString)));
            } catch (JSONException e) {}

            lstBundles.put(props);
        }

        return lstBundles.toString();
    }

    @JavascriptInterface
    public String installBundle(String uri) {
        try {
            BundleContext context = osgi.getFramework().getBundleContext();
            Bundle bundle = context.installBundle(uri, new URL(uri).openStream());
            LOG.e(TAG, String.format("Install bundle %s successfully", uri));
            return String.valueOf(true);
        } catch (BundleException e) {
            LOG.e(TAG, String.format("Install bundle %s failed", uri), e);
            return e.getCause().getMessage();
        } catch (FileNotFoundException e) {
            LOG.e(TAG, String.format("Install bundle %s failed", uri), e);
            return e.getCause().getMessage();
        } catch (MalformedURLException e) {
            LOG.e(TAG, String.format("Install bundle %s failed", uri), e);
            return e.getCause().getMessage();
        } catch (IOException e) {
            LOG.e(TAG, String.format("Install bundle %s failed", uri), e);
            return e.getCause().getMessage();
        }
    }

    @JavascriptInterface
    public String startBundle(int id) {
        Bundle bundle = osgi.getBundle(id);
        try {
            bundle.start();
            LOG.e(TAG, String.format("Start bundle %s successfully", bundle.getLocation()));
            return String.valueOf(true);
        } catch (BundleException e) {
            LOG.e(TAG, String.format("Start bundle %s failed", bundle.getLocation()), e);
            return e.getCause().getMessage();
        }
    }

    @JavascriptInterface
    public String stopBundle(int id) {
        Bundle bundle = osgi.getBundle(id);
        try {
            bundle.stop();
            LOG.e(TAG, String.format("Stop bundle %s successfully", bundle.getLocation()));
            return String.valueOf(true);
        } catch (BundleException e) {
            LOG.e(TAG, String.format("Stop bundle %s failed", bundle.getLocation()), e);
            return e.getCause().getMessage();
        }
    }

    @JavascriptInterface
    public String updateBundle(int id) {
        Bundle bundle = osgi.getBundle(id);
        try {
            bundle.update();
            LOG.e(TAG, String.format("Update bundle %s successfully", bundle.getLocation()));
            return String.valueOf(true);
        } catch (BundleException e) {
            LOG.e(TAG, String.format("Update bundle %s failed", bundle.getLocation()), e);
            return e.getCause().getMessage();
        }
    }

    @JavascriptInterface
    public String uninstallBundle(int id) {
        Bundle bundle = osgi.getBundle(id);
        try {
            bundle.uninstall();
            LOG.e(TAG, String.format("Uninstall bundle %s successfully", bundle.getLocation()));
            return String.valueOf(true);
        } catch (BundleException e) {
            LOG.e(TAG, String.format("Uninstall bundle %s failed", bundle.getLocation()), e);
            return e.getCause().getMessage();
        }
    }

}
