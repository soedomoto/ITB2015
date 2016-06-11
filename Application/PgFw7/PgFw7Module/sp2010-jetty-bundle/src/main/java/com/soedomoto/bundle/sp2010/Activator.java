package com.soedomoto.bundle.sp2010;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	private static BundleContext bundleContext;
	private com.soedomoto.bundle.jetty.Activator jettyActivator;
	private ServletContextHandler handler;

	public void start(BundleContext context) throws Exception {
		Activator.bundleContext = context;
		
		ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContext.setContextPath("/");

        class Hello extends HttpServlet {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
                    ServletException, IOException {
                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("<h1>Hellos SimpleServlet</h1>");
            }
        }
        servletContext.addServlet(new ServletHolder(new Hello()), "/");
		
		jettyActivator.jettyHandlers().addHandler(servletContext);
	}

	public void stop(BundleContext context) throws Exception {
		jettyActivator.jettyHandlers().removeHandler(handler);
	}

}
