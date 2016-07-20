package com.soedomoto.bundle.jetty.handler;

import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class JettyHandler implements IJettyHandler {
	
	private HandlerCollection handlers;

	public JettyHandler() {
		handlers = new HandlerCollection(true);
	}

	public void addHandler(ServletContextHandler handler) {
		handlers.addHandler(handler);
	}

	public HandlerCollection getHandlers() {
		return handlers;
	}

	public void setHandlers(HandlerCollection handlers) {
		this.handlers = handlers;
	}

}
