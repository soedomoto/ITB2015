package com.soedomoto.bundle.proxy;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;

/**
 * Created by soedomoto on 8/4/16.
 */
public class ProxyServer {
    private int _port = 8080;
    private org.eclipse.jetty.server.Server _server;
    private HandlerCollection _handlers;

    public ProxyServer() {
        _server = new Server(_port);
        _initHandlers();
    }

    public ProxyServer(int port) {
        _server = new Server(port);
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

    public static void main(String[] args) throws Exception {
        ProxyServer server = new ProxyServer();
        server.start();
    }

}
