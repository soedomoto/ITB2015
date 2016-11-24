package com.soedomoto.bundle.proxy;

import com.soedomoto.bundle.proxy.service.ContextHandlerService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created by soedomoto on 15/07/16.
 */
public class Activator implements BundleActivator {
    private int _port = 8080;
    private Server _server;
    private HandlerCollection _handlers;

    public void start(BundleContext bundleContext) throws Exception {
        _server = new Server(_port);

        _handlers = new HandlerCollection(true);
        _server.setHandler(_handlers);

        _server.start();

        ContextHandlerService contextHandlerObj = new ContextHandlerService() {
            public void addHandler(Handler handler) throws Exception {
                _handlers.addHandler(handler);
                handler.start();
            }

            public void removeHandler(Handler handler) throws Exception {
                handler.stop();
                _handlers.removeHandler(handler);
            }
        };

        bundleContext.registerService(ContextHandlerService.class.getName(), contextHandlerObj, null);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        _server.stop();
    }

}
