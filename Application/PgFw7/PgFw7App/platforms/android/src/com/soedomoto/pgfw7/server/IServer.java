package com.soedomoto.pgfw7.server;

import android.webkit.JavascriptInterface;

import org.osgi.framework.BundleException;

/**
 * Created by Soedomoto on 6/11/2016.
 */
public class IServer extends IKnopflerfish {

    public IServer() {
        super();
    }

    @JavascriptInterface
    public String start() {
        try {
            this.startFramework();
            this.scanBundles();
            return String.valueOf(true);
        } catch (BundleException e) {
            return e.getLocalizedMessage();
        }
    }

    @JavascriptInterface
    public String stop() {
        try {
            this.stopFramework();
            return String.valueOf(true);
        } catch (Throwable e) {
            return e.getLocalizedMessage();
        }
    }

    @JavascriptInterface
    public boolean status() {
        return this.isRunning();
    }

}
