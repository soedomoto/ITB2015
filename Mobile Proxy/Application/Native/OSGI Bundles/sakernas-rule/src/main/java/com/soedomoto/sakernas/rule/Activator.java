package com.soedomoto.sakernas.rule;

import com.soedomoto.sakernas.rule.servlet.Validate;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.servlet.DefaultServlet;
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
 * - com.soedomoto.proxyServer.port
 */
public class Activator implements BundleActivator {
    public static BundleContext context;
    public static Integer serverPort;

    private static final int DEFAULT_SERVER_PORT = 9999;

    private JettyServer server;
    private String dataDir;

    public void start(BundleContext context) throws Exception {
        Activator.context = context;
        startServer();
    }

    public void startServer() throws Exception {
        //  Handle Storage
        if(context != null) {
            String fwDir = context.getProperty("org.osgi.framework.storage");
            File dataFile = new File(fwDir + File.separator + "data" + File.separator +
                    context.getBundle().getBundleId());
            dataFile.mkdirs();
            dataDir = dataFile.getAbsolutePath();
        }


        // Prepare keystore for HTTPS Connection
        if(context != null && dataDir != null) {
            URL path = context.getBundle().getEntry("jetty.bks");
            File keystore = new File(dataDir + File.separator + "jetty.bks");
            IOUtils.copy(path.openStream(), new FileOutputStream(keystore));
        }


        // Configure Validate Server
        serverPort = DEFAULT_SERVER_PORT;

        if(context != null) {
            String tmpServerPort = context.getProperty("com.soedomoto.server.port");
            if (tmpServerPort != null && !tmpServerPort.equalsIgnoreCase("")) {
                serverPort = Integer.valueOf(tmpServerPort);
            }
        }

        server = JettyServer.createHttpServer(serverPort);
        server.start();

        // Add Root Handler
        ServletContextHandler validationServerHandler = new ServletContextHandler();
        validationServerHandler.setContextPath("/");
        server.getHandlers().addHandler(validationServerHandler);
        validationServerHandler.start();
        validationServerHandler.addServlet(new ServletHolder("default", new DefaultServlet()), "/*");

        // Register Other Handler
        registerServlet(validationServerHandler);
    }

    private void registerServlet(ServletContextHandler handler) {
        ServletHolder info = new ServletHolder(new Validate());
        info.setAsyncSupported(true);
        handler.addServlet(info, Validate.PATH);
    }

    public void stop(BundleContext context) throws Exception {
        server.stop();
    }
}
