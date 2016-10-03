package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKabupaten;
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

import static com.soedomoto.bundle.se2016.Activator.connectionSource;
import static com.soedomoto.bundle.se2016.Activator.gson;
import static com.soedomoto.bundle.se2016.controller.CPropinsi.v101Dao;

/**
 * Created by soedomoto on 8/5/16.
 */
public class CKabupaten {
    public static Dao<MKabupaten, String> v102Dao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKabupaten.class);
        v102Dao = DaoManager.createDao(connectionSource, MKabupaten.class);
    }

    public static void registerServlets(ServletContextHandler context) {
        ServletHolder kabByProv = new ServletHolder(new KabupatenByPropinsi());
        kabByProv.setAsyncSupported(true);
        context.addServlet(kabByProv, KabupatenByPropinsi.PATH);

        ServletHolder kabByKode = new ServletHolder(new KabupatenByKode());
        kabByKode.setAsyncSupported(true);
        context.addServlet(kabByKode, KabupatenByKode.PATH);
    }

    public static class KabupatenByPropinsi extends HttpServlet {
        public static String PATH = "/kabupaten";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String kodePropinsi = req.getParameter("propinsi");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MPropinsi propinsi = v101Dao.queryForId(kodePropinsi);
                            List<MKabupaten> kabs = v102Dao.queryForMatching(new MKabupaten(propinsi));

                            resp.getWriter().println(gson.toJson(kabs));
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

    public static class KabupatenByKode extends HttpServlet {
        public static String PATH = "/kabupaten/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String fullKode = req.getParameter("fullKode");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MKabupaten kab = v102Dao.queryForId(fullKode);

                            resp.getWriter().println(gson.toJson(kab));
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
