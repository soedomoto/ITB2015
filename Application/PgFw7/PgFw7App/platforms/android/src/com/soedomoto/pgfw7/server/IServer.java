package com.soedomoto.pgfw7.server;

import android.webkit.JavascriptInterface;

import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class IServer {
    private static String TAG = IServer.class.getSimpleName();

    private final IOsgi osgi;

    public IServer() {
        this.osgi = new IOsgi();
    }

    @JavascriptInterface
    public String start() {
        return String.valueOf(true);
    }

    @JavascriptInterface
    public String stop() {
        return String.valueOf(true);
    }

    @JavascriptInterface
    public boolean status() {
        return false;
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
            return e.getLocalizedMessage();
        }

        /*BundleRevision br = (BundleRevision) bundle.adapt(BundleRevision.class);
        boolean frag = (br.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0;
        if (!frag) {
            try {
                bundle.start(Bundle.START_ACTIVATION_POLICY);
                LOG.e(TAG, String.format("Start bundle %s successfully", bundle.getLocation()));
                return String.valueOf(true);
            } catch (BundleException e) {
                LOG.e(TAG, String.format("Start bundle %s failed", bundle.getLocation()), e);
                return e.getLocalizedMessage();
            }
        }

        return String.valueOf(false);*/
    }

}


//package com.soedomoto.pgfw7.server;
//
//import android.webkit.JavascriptInterface;
//
//import org.apache.cordova.LOG;
//import org.osgi.framework.BundleContext;
//import org.osgi.framework.BundleException;
//
///**
// * Created by Soedomoto on 6/11/2016.
// */
//public class IServer extends IKnopflerfish {
//    private static String TAG = IKnopflerfish.class.getSimpleName();
//    private IJetty jetty;
//
//    public IServer() {
//        super();
//
//        jetty = new IJetty();
////        try {
////            jetty.startHttpServer(8080);
////        } catch (Exception e) {
////            LOG.e(TAG, e.getLocalizedMessage(), e);
////        }
//    }
//
//    @Override
//    public void registerServices(BundleContext bc) {
//        super.registerServices(bc);
//
////        try {
////            InputStream is = ProxyApplication.getAppContext().getAssets()
////                                .open("jetty-bundle-0.0.1-SNAPSHOT-dex.jar");
////            Bundle jettyBundle = bc.installBundle("jetty-bundle-0.0.1-SNAPSHOT-dex", is);
////            jettyBundle.start();
////        } catch (IOException e) {
////            LOG.e(TAG, e.getLocalizedMessage(), e);
////        } catch (BundleException e) {
////            LOG.e(TAG, e.getLocalizedMessage(), e);
////        }
////
////        IJettyHandler jettyHandler = new JettyHandler(jetty);
////        bc.registerService(IJettyHandler.class.getName(), jettyHandler, null);
//    }
//
//    @JavascriptInterface
//    public String start() {
//        try {
//            this.startFramework();
//            this.scanBundles();
//            return String.valueOf(true);
//        } catch (BundleException e) {
//            return e.getLocalizedMessage();
//        }
//    }
//
//    @JavascriptInterface
//    public String stop() {
//        try {
//            this.stopFramework();
//            return String.valueOf(true);
//        } catch (Throwable e) {
//            return e.getLocalizedMessage();
//        }
//    }
//
//    @JavascriptInterface
//    public boolean status() {
//        return this.isRunning();
//    }
//
//}
