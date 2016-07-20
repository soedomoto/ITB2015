package com.soedomoto.bundle.jetty.handler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		context.registerService(IJettyHandler.class.getName(), new JettyHandler(), null);
		System.out.println("IJettyHandler is registered");
	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
