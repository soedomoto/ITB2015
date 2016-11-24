package com.soedomoto.pgfw7.services;

import org.eclipse.jetty.servlet.ServletContextHandler;

public interface IJettyHandler {
	public void registerContextHandler(ServletContextHandler servletContext);
}
