package com.soedomoto.bundle.se2016;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.soedomoto.bundle.proxy.service.ContextHandlerService;
import com.soedomoto.bundle.se2016.controller.*;
import com.soedomoto.bundle.se2016.model.*;
import com.soedomoto.bundle.se2016.service.AccountHandlerService;
import com.soedomoto.bundle.se2016.service.DaoHandlerService;
import com.soedomoto.bundle.se2016.service.PropertyHandlerService;
import com.soedomoto.bundle.se2016.tools.IpChecker;
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
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by soedomoto on 16/07/16.
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

    public static Integer SYNC_INTERVAL = 10000;

    public static ConnectionSource connectionSource;
    public static String dataDir;

    private ServletContextHandler _servletContext;
    private ContextHandlerService _contextHandlerService;
    private BundleContext _bundleContext;

    public void start(BundleContext context) throws Exception {
        _bundleContext = context;

        //  Handle Properties
        CONTEXT_PATH = context.getProperty("org.osgi.bundle.se2016.servlet.context");
        REAL_HOST = context.getProperty("org.osgi.bundle.se2016.host");
        JDBC_URL = context.getProperty("org.osgi.bundle.se2016.jdbc.url");
        JDBC_USERNAME = context.getProperty("org.osgi.bundle.se2016.jdbc.username");
        JDBC_PASSWORD = context.getProperty("org.osgi.bundle.se2016.jdbc.password");
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

        _configureDatabase(context);

        //  Handle Servlet
        ServiceReference sr = context.getServiceReference(ContextHandlerService.class.getName());
        _contextHandlerService = (ContextHandlerService) context.getService(sr);

        _servletContext = new ServletContextHandler();
        _servletContext.setContextPath(CONTEXT_PATH);
        _mapServlet();

        _contextHandlerService.addHandler(_servletContext);

        //  Register Service
        //  Property Handler
        PropertyHandlerService propertyHandlerObj = new PropertyHandlerService() {
            public String getJdbcUrl() { return JDBC_URL; }
            public String getJdbcUsername() { return JDBC_USERNAME; }
            public String getJdbcPassword() { return JDBC_PASSWORD; }
            public String getRealHost() { return REAL_HOST; }
            public String getContextPath() { return CONTEXT_PATH; }
            public String getDataDirectory() { return dataDir; }
        };

        _bundleContext.registerService(PropertyHandlerService.class.getName(), propertyHandlerObj, null);

        //  Dao Handler
        DaoHandlerService daoHandlerObj = new DaoHandlerService() {
            public Dao<MPropinsi, String> v101Dao() { return CPropinsi.v101Dao; }
            public Dao<MKabupaten, String> v102Dao() { return CKabupaten.v102Dao; }
            public Dao<MKecamatan, String> v103Dao() { return CKecamatan.v103Dao; }
            public Dao<MKelurahan, String> v104Dao() { return CKelurahan.v104Dao; }
            public Dao<MBlokSensus, String> v105Dao() { return CBlokSensus.v105Dao; }
            public Dao<MSubBlokSensus, String> v106Dao() { return CSubBlokSensus.v106Dao; }
            public Dao<MNks, String> v107Dao() { return CNks.v107Dao; }
            public Dao<MSls, String> v108Dao() { return CSls.v108Dao; }
            public Dao<MKriteriaBlokSensus, String> v109Dao() { return CKriteriaBlokSensus.v109Dao; }
            public Dao<MPenggunaanBangunanSensus, String> v504Dao() { return CPenggunaanBangunanSensus.v504Dao; }
            public Dao<MLokasiTempatUsaha, String> v510Dao() { return CLokasiTempatUsaha.v510Dao; }
            public Dao<MPencacah, String> pencacahDao() { return CPencacah.pencacahDao; }
            public Dao<MWilayahCacah, String> wilayahCacahDao() { return CWilayahCacah.wilayahCacahDao; }
            public Dao<MFormL1, String> formL1Dao() { return CFormL1.formL1Dao; }
            public Dao<MFormL1B5, String> formL1B5Dao() { return CFormL1.formL1B5Dao; }
            public Dao<MFormL1B5Usaha, String> formL1B5UsahaDao() { return CFormL1.formL1B5UsahaDao; }
        };

        _bundleContext.registerService(DaoHandlerService.class.getName(), daoHandlerObj, null);

        //  Account Handler
        AccountHandlerService accountHandlerObj = new AccountHandlerService() {
            public String getPencacahID() { return "198706152009021004"; }
        };

        _bundleContext.registerService(AccountHandlerService.class.getName(), accountHandlerObj, null);
    }

    public void stop(BundleContext context) throws Exception {
        _contextHandlerService.removeHandler(_servletContext);
        if(connectionSource != null) connectionSource.close();
    }

    private void _configureDatabase(BundleContext context) throws SQLException {
//        connectionSource = new JdbcConnectionSource("jdbc:h2:file:"+ dataDir + File.separator + DB_NAME +
//                ";FILE_LOCK=FS;PAGE_SIZE=1024;CACHE_SIZE=8192;DB_CLOSE_DELAY=-1");
        connectionSource = new JdbcConnectionSource(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);

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
