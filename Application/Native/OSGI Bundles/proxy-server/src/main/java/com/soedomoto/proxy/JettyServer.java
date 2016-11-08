package com.soedomoto.proxy;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Created by soedomoto on 8/4/16.
 */
public class JettyServer {
    private org.eclipse.jetty.server.Server _server;
    private HandlerCollection _handlers;

    public static JettyServer createHttpServer(int port) {
        return new JettyServer(port);
    }

    public static JettyServer createHttpsServer(int port, File keystore) {
        JettyServer proxy = new JettyServer(port);
        org.eclipse.jetty.server.Server server = proxy._server;


        try {
            SslContextFactory sslContextFactory = new SslContextFactory();

            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(new FileInputStream(keystore), "12345678".toCharArray());

            sslContextFactory.setKeyStore(keyStore);
            sslContextFactory.setKeyStorePassword("12345678");
            sslContextFactory.setKeyManagerPassword("12345678");
            sslContextFactory.setCertAlias("ssl");
            sslContextFactory.setKeyStoreType("bks");

            sslContextFactory.setIncludeProtocols("TLSv1", "TLSv1.1", "TLSv1.2");
            sslContextFactory.setExcludeProtocols("SSLv3");
            sslContextFactory.setIncludeCipherSuites("TLS_DHE_RSA_WITH_AES_128_CBC_SHA");

            ServerConnector sslConnector = new ServerConnector(server,
                    new SslConnectionFactory(/*sslContextFactory, "http/1.1"*/));
            sslConnector.setPort(port);

            server.setConnectors(new Connector[] { sslConnector });
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return proxy;
    }

    private JettyServer(int port) {
        _server = new org.eclipse.jetty.server.Server(port);
        _initHandlers();
    }

    private void _initHandlers() {
        _handlers = new HandlerCollection(true);
        _server.setHandler(_handlers);
    }

    public void start() throws Exception {
        _server.start();
    }

    public void stop() throws Exception {
        _server.stop();
    }

    public HandlerCollection getHandlers() {
        return _handlers;
    }

}
