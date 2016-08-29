package com.soedomoto.bundle.proxy;

import com.soedomoto.bundle.proxy.service.ContextHandlerService;
import org.eclipse.jetty.server.Handler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created by soedomoto on 15/07/16.
 */
public class Activator implements BundleActivator {
    private int _port = 5555;
    private ProxyServer _server;

    public void start(BundleContext bundleContext) throws Exception {
        _server = new ProxyServer(_port);
        _server.start();

        ContextHandlerService contextHandlerObj = new ContextHandlerService() {
            public void addHandler(Handler handler) throws Exception {
                _server.getHandlers().addHandler(handler);
                handler.start();
            }

            public void removeHandler(Handler handler) throws Exception {
                handler.stop();
                _server.getHandlers().removeHandler(handler);
            }
        };

        bundleContext.registerService(ContextHandlerService.class.getName(), contextHandlerObj, null);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        _server.stop();
    }

}
