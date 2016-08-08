package com.soedomoto.bundle.se2016;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.soedomoto.bundle.proxy.service.ContextHandlerService;
import com.soedomoto.bundle.se2016.controller.CKabupaten;
import com.soedomoto.bundle.se2016.controller.CKecamatan;
import com.soedomoto.bundle.se2016.controller.CKelurahan;
import com.soedomoto.bundle.se2016.controller.CPropinsi;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by soedomoto on 16/07/16.
 */
public class Activator implements BundleActivator {
    public static ConnectionSource connectionSource;
    public static String jarFile;
    public static String dataDir;

    private ServletContextHandler _context;
    private ContextHandlerService _contextHandlerService;

    public void start(BundleContext context) throws Exception {
        jarFile = context.getBundle().getLocation().replace("file:", "");
        dataDir = jarFile.replace(".jar", "");

        _configureDatabase(context);

        ServiceReference sr = context.getServiceReference(ContextHandlerService.class.getName());
        _contextHandlerService = (ContextHandlerService) context.getService(sr);

        _context = new ServletContextHandler();
        _context.setContextPath("/se2016");

        ServletHolder defaultHolder = new ServletHolder("default", new DefaultServlet());
        defaultHolder.setInitParameter("resourceBase", _getResourceBase(context));

        // Servlet Mapping
        _context.addServlet(defaultHolder, "/*");
        _context.addServlet(new ServletHolder(new CPropinsi.ListPropinsi()), "/propinsi");
        _context.addServlet(new ServletHolder(new CKabupaten.KabupatenByPropinsi()), "/kabupaten");
        _context.addServlet(new ServletHolder(new CKecamatan.KecamatanByKabupaten()), "/kecamatan");
        _context.addServlet(new ServletHolder(new CKelurahan.KelurahanByKecamatan()), "/kelurahan");

        _contextHandlerService.addHandler(_context);
    }

    public void stop(BundleContext context) throws Exception {
        _contextHandlerService.removeHandler(_context);
    }

    private void _configureDatabase(BundleContext context) throws SQLException {
        connectionSource = new JdbcConnectionSource("jdbc:h2:file:"+ dataDir + File.separator +"se2016.db;" +
                "FILE_LOCK=FS;PAGE_SIZE=1024;CACHE_SIZE=8192");

        CPropinsi.createDao();
        CKabupaten.createDao();
        CKecamatan.createDao();
        CKelurahan.createDao();
    }

    private String _getResourceBase(BundleContext context) throws IOException {
        FileUtils.deleteDirectory(new File(dataDir + File.separator + "webroot"));

        JarFile jar = new JarFile(jarFile);
        Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry entry = (JarEntry) enumEntries.nextElement();
            if(entry.getName().startsWith("webroot")) {
                File f = new File(dataDir + File.separator + entry.getName());
                if(! entry.isDirectory()) {
                    f.getParentFile().mkdirs();

                    InputStream is = jar.getInputStream(entry); // get the input stream
                    FileOutputStream fos = new FileOutputStream(f);
                    while (is.available() > 0) {  // write contents of 'is' to 'fos'
                        fos.write(is.read());
                    }
                    fos.close();
                    is.close();
                }
            }
        }

        return dataDir + File.separator + "webroot";
    }
}
