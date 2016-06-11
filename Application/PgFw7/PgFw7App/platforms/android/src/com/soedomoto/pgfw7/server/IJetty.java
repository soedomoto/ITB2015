package com.soedomoto.pgfw7.server;

import android.content.Context;
import android.util.Log;

import com.soedomoto.pgfw7.ProxyApplication;

import org.apache.cordova.LOG;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Soedomoto on 6/1/2016.
 */
public class IJetty {
    private static String TAG = IJetty.class.getSimpleName();
    private static final Context _context = ProxyApplication.getAppContext();

    private Server server;
    private boolean __isRunning = false;

    private int _port = 8080;
    private int _sslPort = 8443;

    private boolean _useNIO = true;
    private boolean _useSSL = false;
    private String _keystoreFile;
    private String _truststoreFile;
    private String _keystorePassword;
    private String _keymgrPassword;
    private String _truststorePassword;

    public IJetty() {}

    public boolean startHttpServer(int port) throws Exception {
        return this.startHttpServer(port, true);
    }

    public boolean startHttpServer(int port, boolean useNIO) throws Exception {
        this._port = port;
        this._useNIO = useNIO;
        this._useSSL = false;

        return this._start();
    }

    public boolean startHttpsServer(int port, File keystore, File truststore, String keystorePwd,
                                    String keymgrPwd, String truststorePwd) throws Exception {
        return this.startHttpsServer(port, true, keystore, truststore, keystorePwd, keymgrPwd,
                truststorePwd);
    }

    public boolean startHttpsServer(int port, boolean useNIO, File keystore, File truststore,
                                    String keystorePwd, String keymgrPwd, String truststorePwd) throws Exception {
        this._sslPort = port;
        this._useNIO = useNIO;
        this._useSSL = true;
        this._keystoreFile = keystore.getAbsolutePath();
        this._truststoreFile = truststore.getAbsolutePath();
        this._keystorePassword = keystorePwd;
        this._keymgrPassword = keymgrPwd;
        this._truststorePassword = truststorePwd;

        return this._start();
    }

    public boolean stopServer() {
        return this._stop();
    }

    private boolean _start() throws Exception {
        System.setProperty("java.net.preferIPv6Addresses", "false");

        try {
            server = new Server(_port);
            configureConnectors();
            configureHandlers();
            server.start();

            __isRunning = true;
            LOG.e(TAG, "Jetty started");

            return true;
        } catch (Exception e) {
            LOG.e(TAG, e.getLocalizedMessage(), e);
            throw e;
        }
    }

    private boolean _stop() {
        try {
            server.stop();
            server = null;

            __isRunning = false;
            LOG.e(TAG, "Jetty stopped");

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);

            return false;
        }
    }

    private void configureConnectors() {
        if (server != null) {
            if (_useSSL) {
                SslContextFactory sslContextFactory = new SslContextFactory();
                //sslContextFactory.setKeyStore(_keystoreFile);
                //sslContextFactory.setTrustStore(_truststoreFile);
                sslContextFactory.setKeyStorePassword(_keystorePassword);
                sslContextFactory.setKeyManagerPassword(_keymgrPassword);
                sslContextFactory.setKeyStoreType("bks");
                sslContextFactory.setTrustStorePassword(_truststorePassword);
                sslContextFactory.setTrustStoreType("bks");
            }
        }
    }

    private void configureHandlers() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (server != null) {
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            class Hello extends HttpServlet {
                @Override
                protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
                        ServletException, IOException {
                    response.setContentType("text/html");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().println("<h1>Hellos SimpleServlet</h1>");
                }
            }
            context.addServlet(new ServletHolder(new Hello()), "/");

            /*ResourceConfig config = new ResourceConfig();
            config.setClassLoader(classLoader);
            config.packages(Hello.class.getPackage().getName());
            ServletHolder servlet = new ServletHolder(new ServletContainer(config));
            context.addServlet(servlet, "*//*");*/

            /*try {
                File dx = ClassUtil.openAssetFile("dex/services.jar");
                for(Class c : ClassUtil.getDexClasses(dx, "com.soedomoto.api")) {
                    LOG.e(TAG, c.getName());

                    context.addServlet(c, "/");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            server.setHandler(context);
        }
    }

    public boolean isRunning() {
        return this.__isRunning;
    }
}
