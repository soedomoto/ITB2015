package com.soedomoto.sakernas;

import android.os.AsyncTask;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.soedomoto.sakernas.osgi.Knopflerfish;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soedomoto on 10/30/16.
 */

public class ProxyApplication extends MultiDexApplication {
    private final String TAG = getClass().getSimpleName();

    private Knopflerfish knopflerfish;
    private int proxyPort;
    private int serverPort;

    @Override
    public void onCreate() {
        super.onCreate();

        Map config = new HashMap();

        try {
            proxyPort = generatePort();
            config.put("com.soedomoto.proxy.port", String.valueOf(proxyPort));

            Log.i(TAG, String.format("Proxy will be listening on port %s", proxyPort));
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to create socket"), e);
        }

        try {
            do {
                serverPort = generatePort();
                config.put("com.soedomoto.server.port", String.valueOf(serverPort));
            } while (proxyPort == serverPort);

            Log.i(TAG, String.format("Server will be listening on port %s", serverPort));
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to create socket"), e);
        }

        knopflerfish = new Knopflerfish(getApplicationContext(), config);

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                configureKnopflerfish();
                return null;
            }
        }.execute();
    }

    public int generatePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();

        return port;
    }

    private void configureKnopflerfish() {
        try {
            knopflerfish.start();
            Log.i(Knopflerfish.TAG, String.format("Framework is started"));
        } catch (BundleException e) {
            Log.e(Knopflerfish.TAG, String.format("Framework is failed to start"), e);
        }

        // Install Required Bundle
        try {
            Bundle proxyBundle = knopflerfish.getFramework().getBundleContext()
                    .getBundle("proxy-server-2016.1-dex.jar");
            if(proxyBundle == null || (proxyBundle != null && proxyBundle.getState() == Bundle.UNINSTALLED)) {
                proxyBundle = knopflerfish.getFramework().getBundleContext()
                        .installBundle(
                                "proxy-server",
                                getAssets().open("proxy-server-2016.1-dex.jar")
                        );
            }

            proxyBundle.start();


            Bundle ruleBundle = knopflerfish.getFramework().getBundleContext()
                    .getBundle("sakernas-rule-2016.1.1-dex.jar");
            if(ruleBundle == null || (ruleBundle != null && ruleBundle.getState() == Bundle.UNINSTALLED)) {
                ruleBundle = knopflerfish.getFramework().getBundleContext()
                        .installBundle(
                                "sakernas-rule",
                                getAssets().open("sakernas-rule-2016.1.1-dex.jar")
                        );
            }

            ruleBundle.start();


            Log.i(Knopflerfish.TAG, String.format("Bundle is started"));
        } catch (BindException e) {

        } catch (BundleException e) {
            Log.e(Knopflerfish.TAG, String.format("Bundles is failed to start"), e);
        } catch (IOException e) {
            Log.e(Knopflerfish.TAG, String.format("Failed to access assets folder"), e);
        }
    }

    public Knopflerfish getKnopflerfish() {
        return knopflerfish;
    }

    public int getProxyPort() {
        return proxyPort;
    }
}
