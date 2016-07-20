package com.soedomoto.bundle.jetty;

import org.eclipse.jetty.server.Server;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.soedomoto.bundle.jetty.handler.IJettyHandler;

public class Activator implements BundleActivator {
	private Server server;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting jetty...");
		
		ServiceReference reference = context.getServiceReference(IJettyHandler.class.getName());
		IJettyHandler jettyHandler = (IJettyHandler) context.getService(reference);
		
		server = new Server(8888);
		server.setHandler(jettyHandler.getHandlers());
        server.start();
        
        System.out.println("Jetty started...");
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Jetty stopped...");
		server.stop();
        server = null;
	}

}
