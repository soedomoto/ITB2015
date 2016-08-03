package com.soedomoto.bundle.sp2010;

import com.soedomoto.bundle.jetty.service.ContextHandlerService;
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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by soedomoto on 16/07/16.
 */
public class Activator implements BundleActivator {
    private ServletContextHandler _context;
    private ContextHandlerService _contextHandlerService;

    public void start(BundleContext context) throws Exception {
        ServiceReference sr = context.getServiceReference(ContextHandlerService.class.getName());
        _contextHandlerService = (ContextHandlerService) context.getService(sr);

        _context = new ServletContextHandler();
        _context.setContextPath("/sp2010");

        ServletHolder holderPwd = new ServletHolder("default", new DefaultServlet());
        holderPwd.setInitParameter("resourceBase", _getResourceBase(context));

        _context.addServlet(holderPwd, "/*");

        ServletMapper mapper = new ServletMapper(_context);
        mapper.routes();

        _contextHandlerService.addHandler(_context);
    }

    public void stop(BundleContext context) throws Exception {
        _contextHandlerService.removeHandler(_context);
    }

    private String _getResourceBase(BundleContext context) throws IOException {
        String tmp = context.getBundle().getLocation().replace(".jar", "");
        FileUtils.deleteDirectory(new File(tmp));

        JarFile jar = new JarFile(context.getBundle().getLocation());
        Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            if(file.getName().startsWith("webroot")) {
                File f = new File(tmp + File.separator + file.getName());
                if(! file.isDirectory()) {
                    f.getParentFile().mkdirs();

                    InputStream is = jar.getInputStream(file); // get the input stream
                    FileOutputStream fos = new FileOutputStream(f);
                    while (is.available() > 0) {  // write contents of 'is' to 'fos'
                        fos.write(is.read());
                    }
                    fos.close();
                    is.close();
                }
            }
        }

        return tmp + File.separator + "webroot";
    }
}
