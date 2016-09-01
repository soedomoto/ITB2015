package com.soedomoto.bundle.se2016;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.soedomoto.bundle.proxy.service.ContextHandlerService;
import com.soedomoto.bundle.se2016.controller.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * Created by soedomoto on 16/07/16.
 */
public class Activator implements BundleActivator {
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
    public static ThreadGroup threads;

    public static String DB_NAME;
    public static String REAL_HOST;
    public static String CONTEXT_PATH;

    public static ConnectionSource connectionSource;
    public static String dataDir;

    private ServletContextHandler _servletContext;
    private ContextHandlerService _contextHandlerService;
    private BundleContext _bundleContext;

    public void start(BundleContext context) throws Exception {
        _bundleContext = context;
        threads = new ThreadGroup(_bundleContext.getBundle().getSymbolicName());
        threads.setDaemon(true);

        Dictionary headers = _bundleContext.getBundle().getHeaders();
        CONTEXT_PATH = String.valueOf(headers.get("Context-Path"));
        REAL_HOST = String.valueOf(headers.get("Real-Host"));
        DB_NAME = String.valueOf(headers.get("Database-Name"));

        System.out.println(String.format("Properties : %s, %s, %s", CONTEXT_PATH, REAL_HOST, DB_NAME));

        String fwDir = context.getProperty("org.osgi.framework.storage");
        File dataFile = new File(fwDir + File.separator + "data" + File.separator +
                                 _bundleContext.getBundle().getBundleId());
        dataFile.mkdirs();
        dataDir = dataFile.getAbsolutePath();

        ServiceReference sr = context.getServiceReference(ContextHandlerService.class.getName());
        _contextHandlerService = (ContextHandlerService) context.getService(sr);

        _servletContext = new ServletContextHandler();
        _servletContext.setContextPath(CONTEXT_PATH);

        _configureDatabase(context);
        _mapServlet();

        _contextHandlerService.addHandler(_servletContext);
    }

    public void stop(BundleContext context) throws Exception {
        _contextHandlerService.removeHandler(_servletContext);
        if(connectionSource != null) connectionSource.close();

        threads.stop();
        threads = null;
    }

    private void _configureDatabase(BundleContext context) throws SQLException {
        connectionSource = new JdbcConnectionSource("jdbc:h2:file:"+ dataDir + File.separator + DB_NAME +
                ";FILE_LOCK=FS;PAGE_SIZE=1024;CACHE_SIZE=8192;DB_CLOSE_DELAY=-1");

        CPropinsi.createDao();
        CKabupaten.createDao();
        CKecamatan.createDao();
        CKelurahan.createDao();
        CBlokSensus.createDao();
        CSubBlokSensus.createDao();
        CNks.createDao();
        CSls.createDao();
        CKriteriaBlokSensus.createDao();
        CPenggunaanBangunanSensus.createDao();
        CLokasiTempatUsaha.createDao();
        CPencacah.createDao();
        CWilayahCacah.createDao();
        CFormL1.createDao();
        CSync.createDao();
    }

    private void _mapServlet() throws IOException {
        ServletHolder defaultHolder = new ServletHolder("default", new DefaultServlet());
        defaultHolder.setInitParameter("resourceBase", _getResourceBase());

        // Servlet Mapping
        _servletContext.addServlet(defaultHolder, "/*");
        CPropinsi.registerServlets(_servletContext);
        CKabupaten.registerServlets(_servletContext);
        CKecamatan.registerServlets(_servletContext);
        CKelurahan.registerServlets(_servletContext);
        CBlokSensus.registerServlets(_servletContext);
        CSubBlokSensus.registerServlets(_servletContext);
        CNks.registerServlets(_servletContext);
        CSls.registerServlets(_servletContext);
        CKriteriaBlokSensus.registerServlets(_servletContext);
        CPenggunaanBangunanSensus.registerServlets(_servletContext);
        CLokasiTempatUsaha.registerServlets(_servletContext);
        CPencacah.registerServlets(_servletContext);
        CWilayahCacah.registerServlets(_servletContext);
        CFormL1.registerServlets(_servletContext);
        CSync.registerServlets(_servletContext);
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
