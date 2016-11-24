package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MPropinsi;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.soedomoto.bundle.se2016.Activator.gson;

/**
 * Created by soedomoto on 15/07/16.
 */
public class CPropinsi {
    private static CPropinsi cPropinsi;
    public static CPropinsi instance() {
        if(cPropinsi == null) cPropinsi = new CPropinsi();
        return cPropinsi;
    }

    private Dao<MPropinsi, String> v101Dao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MPropinsi.class);
        v101Dao = DaoManager.createDao(connectionSource, MPropinsi.class);
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder lsProp = new ServletHolder(new ListPropinsi());
        lsProp.setAsyncSupported(true);
        context.addServlet(lsProp, ListPropinsi.PATH);

        ServletHolder propByKode = new ServletHolder(new PropinsiByKode());
        propByKode.setAsyncSupported(true);
        context.addServlet(propByKode, PropinsiByKode.PATH);
    }

    public Dao<MPropinsi, String> getV101Dao() {
        return v101Dao;
    }

    public static class ListPropinsi extends HttpServlet {
        public static String PATH = "/propinsi";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            List<MPropinsi> provs = CPropinsi.instance().getV101Dao().queryForAll();

                            resp.getWriter().println(gson.toJson(provs));
                            resp.setContentType("application/json");
                            resp.setStatus(HttpServletResponse.SC_OK);
                        } catch (SQLException e) {
                            resp.getWriter().println("Error in database connection: " + e.getMessage());
                            resp.setContentType("text/plain");
                            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    actx.complete();
                }
            });
        }
    }

    public static class PropinsiByKode extends HttpServlet {
        public static String PATH = "/propinsi/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String fullKode = req.getParameter("fullKode");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MPropinsi prop = CPropinsi.instance().getV101Dao().queryForId(fullKode);

                            resp.getWriter().println(gson.toJson(prop));
                            resp.setContentType("application/json");
                            resp.setStatus(HttpServletResponse.SC_OK);
                        } catch (SQLException e) {
                            resp.getWriter().println("Error in database connection: " + e.getMessage());
                            resp.setContentType("text/plain");
                            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    actx.complete();
                }
            });
        }
    }
}
