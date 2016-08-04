package com.soedomoto.bundle.proxy.servlet;

import org.eclipse.jetty.proxy.ProxyServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * Created by soedomoto on 8/4/16.
 */
public class DefaultProxyServlet extends ProxyServlet {
    private String placesUrl;

    @Override
    public void init() throws ServletException {
        super.init();

        ServletConfig config = getServletConfig();
        placesUrl = config.getInitParameter("PlacesUrl");

        // Allow override with system property
        try {
            placesUrl = System.getProperty("PlacesUrl", placesUrl);
        } catch (SecurityException e) {}

        if (null == placesUrl) {
            placesUrl = "https://maps.googleapis.com/maps/api/place/search/json";
        }
    }
}
