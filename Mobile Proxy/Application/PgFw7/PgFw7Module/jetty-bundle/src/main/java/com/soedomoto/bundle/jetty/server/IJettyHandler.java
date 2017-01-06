package com.soedomoto.bundle.jetty.server;

import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;

public interface IJettyHandler {
	public void addHandler(ServletContextHandler handler);
	public void setHandlers(HandlerCollection handlers);
	public HandlerCollection getHandlers();
}
