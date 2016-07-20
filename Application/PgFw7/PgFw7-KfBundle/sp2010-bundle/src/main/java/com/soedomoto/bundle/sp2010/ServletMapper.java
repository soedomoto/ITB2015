package com.soedomoto.bundle.sp2010;

import com.soedomoto.bundle.sp2010.controller.HomeServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by soedomoto on 18/07/16.
 */
public class ServletMapper {
    private final ServletContextHandler _contextHandler;

    public ServletMapper(ServletContextHandler contextHandler) {
        _contextHandler = contextHandler;
    }

    public void routes() {
        _contextHandler.addServlet(new ServletHolder(new HomeServlet()), "/home");
    }
}
