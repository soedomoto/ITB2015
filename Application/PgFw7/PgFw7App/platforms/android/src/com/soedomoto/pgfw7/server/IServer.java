package com.soedomoto.pgfw7.server;

import android.webkit.JavascriptInterface;

import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

public class IServer {
    private static String TAG = IServer.class.getSimpleName();

    private final COsgi osgi;

    public IServer() {
        this.osgi = new COsgi();
    }

    @JavascriptInterface
    public String startFramework() {
        Framework framework = osgi.getFramework();
        try {
            framework.init();
            framework.start();
            LOG.e(TAG, "Start framework successfully");
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
            return String.valueOf(true);
        } catch (BundleException e) {
            LOG.e(TAG, "Stop framework failed", e);
            return e.getCause().getMessage();
        }
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
