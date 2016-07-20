//package com.soedomoto.pgfw7.server.handlers;
//
//import com.soedomoto.pgfw7.server.IJetty;
//import com.soedomoto.pgfw7.services.IJettyHandler;
//
//import org.eclipse.jetty.servlet.ServletContextHandler;
//
///**
// * Created by soedomoto on 6/15/16.
// */
//public class JettyHandler implements IJettyHandler {
//
//    private final IJetty iJetty;
//
//    public JettyHandler(IJetty iJetty) {
//        this.iJetty = iJetty;
//    }
//
//    @Override
//    public void registerContextHandler(ServletContextHandler servletContextHandler) {
//        iJetty.addHandler(servletContextHandler);
//    }
//
//}
