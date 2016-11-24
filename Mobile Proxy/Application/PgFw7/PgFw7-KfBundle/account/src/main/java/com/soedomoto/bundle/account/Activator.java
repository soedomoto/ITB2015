package com.soedomoto.bundle.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.soedomoto.bundle.account.controller.CAccount;
import com.soedomoto.bundle.account.service.SessionHandlerService;
import com.soedomoto.bundle.proxy.service.ContextHandlerService;
import com.soedomoto.bundle.proxy.tools.IpChecker;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.h2.util.IOUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * Created by soedomoto on 10/1/16.
 */
public class Activator implements BundleActivator {
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

    public static String JDBC_URL;
    public static String JDBC_USERNAME;
    public static String JDBC_PASSWORD;
    public static String REAL_HOST;
    public static String CONTEXT_PATH;

    public static String REAL_HOST_IP;
    public static String THIS_IP;

    public static String SESSION_USER_ID;

    private String dataDir;
    private JdbcConnectionSource _connectionSource;

    private BundleContext _bundleContext;
    private ContextHandlerService _contextHandlerService;
    private ServletContextHandler _servletContext;

    public void start(BundleContext context) throws Exception {
        _bundleContext = context;

        //  Handle Properties
        CONTEXT_PATH = context.getProperty("org.osgi.bundle.account.servlet.context");
        REAL_HOST = context.getProperty("org.osgi.bundle.account.host");
        JDBC_URL = context.getProperty("org.osgi.bundle.account.jdbc.url");
        JDBC_USERNAME = context.getProperty("org.osgi.bundle.account.jdbc.username");
        JDBC_PASSWORD = context.getProperty("org.osgi.bundle.account.jdbc.password");

        Dictionary mftHeaders = context.getBundle().getHeaders();
        if(CONTEXT_PATH == null) CONTEXT_PATH = String.valueOf( mftHeaders.get("Context-Path"));
        if(REAL_HOST == null) REAL_HOST = String.valueOf( mftHeaders.get("Real-Host"));
        if(JDBC_URL == null) JDBC_URL = String.valueOf( mftHeaders.get("Jdbc-Url"));
        if(JDBC_USERNAME == null) JDBC_USERNAME = String.valueOf( mftHeaders.get("Jdbc-Username"));
        if(JDBC_PASSWORD == null) JDBC_PASSWORD = String.valueOf( mftHeaders.get("Jdbc-Password"));

        System.out.println(String.format("\n" +
                "=== Properties === \n" +
                "+ Servlet Context : %s \n" +
                "+ Real Host       : %s \n" +
                "+ JDBC URL        : %s\n" +
                "==================\n", CONTEXT_PATH, REAL_HOST, JDBC_URL));

        //  Get Curr IP and Remote IP
        REAL_HOST_IP = InetAddress.getByName(new URI(REAL_HOST).getHost()).getHostAddress();
        THIS_IP = IpChecker.getIp();

        //  Handle Storage
        String fwDir = context.getProperty("org.osgi.framework.storage");
        File dataFile = new File(fwDir + File.separator + "data" + File.separator +
                _bundleContext.getBundle().getBundleId());
        dataFile.mkdirs();
        dataDir = dataFile.getAbsolutePath();

        if(! (JDBC_URL == null || JDBC_URL.equalsIgnoreCase("null") || JDBC_URL.length() == 0)) {
            _configureDatabase(context);
        }

        //  Handle Servlet
        ServiceReference sr = context.getServiceReference(ContextHandlerService.class.getName());
        _contextHandlerService = (ContextHandlerService) context.getService(sr);

        _servletContext = new ServletContextHandler();
        _servletContext.setContextPath(CONTEXT_PATH);
        _mapServlet();

        _contextHandlerService.addHandler(_servletContext);

        // Register services
        //  Account Handler
        SessionHandlerService accountHandlerObj = new SessionHandlerService() {
            public String getUserID() { return SESSION_USER_ID; }
        };

        _bundleContext.registerService(SessionHandlerService.class.getName(), accountHandlerObj, null);
    }

    public void stop(BundleContext context) throws Exception {
        _contextHandlerService.removeHandler(_servletContext);
        if(_connectionSource != null) _connectionSource.close();
    }

    private void _configureDatabase(BundleContext context) throws SQLException {
        _connectionSource = new JdbcConnectionSource(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        CAccount.instance().createDao(_connectionSource);
    }

    private void _mapServlet() throws IOException {
        ServletHolder defaultHolder = new ServletHolder("default", new DefaultServlet());
        defaultHolder.setInitParameter("resourceBase", _getResourceBase());

        // Servlet Mapping
        _servletContext.addServlet(defaultHolder, "/*");
        CAccount.instance().registerServlets(_servletContext);
    }

    private String _getResourceBase() throws IOException {
        String webroot = "webroot";

        FileUtils.deleteDirectory(new File(dataDir + File.separator + webroot));
        Enumeration enumEntries = _bundleContext.getBundle().findEntries("/" + webroot, "*.*", true);
        while(enumEntries.hasMoreElements()) {
            URL path = (URL) enumEntries.nextElement();
            File f = new File(dataDir + path.getPath());
            f.getParentFile().mkdirs();
            IOUtils.copy(path.openStream(), new FileOutputStream(f));
        }

        return dataDir + File.separator + webroot;
    }
}
