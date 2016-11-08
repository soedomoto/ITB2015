package com.soedomoto.proxy;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * Created by soedomoto on 05/11/16.
 *
 * Required properties:
 * - com.soedomoto.proxy.port
 * - com.soedomoto.server.port
 */
public class Activator implements BundleActivator {
    public static BundleContext context;
    public static Integer proxyPort;
    public static Integer serverPort;

    private static final int DEFAULT_PROXY_PORT = 5555;

    private JettyServer server;

    private String dataDir;

    public void start(BundleContext context) throws Exception {
        Activator.context = context;


        //  Handle Storage
        String fwDir = context.getProperty("org.osgi.framework.storage");
        File dataFile = new File(fwDir + File.separator + "data" + File.separator +
                context.getBundle().getBundleId());
        dataFile.mkdirs();
        dataDir = dataFile.getAbsolutePath();


        // Prepare keystore for HTTPS Connection
        URL path = context.getBundle().getEntry("jetty.bks");
        File keystore = new File(dataDir + File.separator + "jetty.bks");
        IOUtils.copy(path.openStream(), new FileOutputStream(keystore));

        // Configure Local Server
        String tmpServerPort = context.getProperty("com.soedomoto.server.port");
        if (tmpServerPort != null && !tmpServerPort.equalsIgnoreCase("")) {
            serverPort = Integer.valueOf(tmpServerPort);
        }


        // Configure Proxy JettyServer
        String tmpProxyPort = context.getProperty("com.soedomoto.proxy.port");
        if(tmpProxyPort == null || (tmpProxyPort != null && tmpProxyPort.equalsIgnoreCase(""))) {
            proxyPort = DEFAULT_PROXY_PORT;
        } else {
            proxyPort = Integer.valueOf(tmpProxyPort);
        }

        server = JettyServer.createHttpServer(proxyPort);
        server.start();

        // Add Root Handler
        ServletContextHandler proxyHandler = new ServletContextHandler();
        proxyHandler.setContextPath("/");
        server.getHandlers().addHandler(proxyHandler);
        proxyHandler.start();
        proxyHandler.addServlet(new ServletHolder("default", new ValidationProxy(serverPort)), "/*");
    }

    public void stop(BundleContext context) throws Exception {
        server.stop();
    }
}
