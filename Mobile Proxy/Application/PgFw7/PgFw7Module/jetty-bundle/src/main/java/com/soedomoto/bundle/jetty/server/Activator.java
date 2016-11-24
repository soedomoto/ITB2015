package com.soedomoto.bundle.jetty.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	private Server server;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Registering IJettyHandler service...");
		
		IJettyHandler jettyHandler = new JettyHandler();
		context.registerService(IJettyHandler.class.getName(), jettyHandler, null);
		
		System.out.println("Starting jetty...");
		
		ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContext.setContextPath("/");
		servletContext.addServlet(new ServletHolder(new IndexServlet()), "/");
		jettyHandler.addHandler(servletContext);
		
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
