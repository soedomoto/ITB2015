package com.soedomoto.bundle.sp2010;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.soedomoto.pgfw7.services.IJettyHandler;

public class Activator implements BundleActivator {

	private ServletContextHandler servletContext;

	public void start(BundleContext context) throws Exception {
		ServiceReference reference = context.getServiceReference(IJettyHandler.class.getName());
		IJettyHandler jettyHandler = (IJettyHandler) context.getService(reference);
		
		servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContext.setContextPath("/sp2010");
		servletContext.addServlet(new ServletHolder(new IndexServlet()), "/");
		jettyHandler.registerContextHandler(servletContext);
		
		System.out.println("Servlet Context " + servletContext.getContextPath() + " added");
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Servlet Context " + servletContext.getContextPath() + "removed");
	}

}
