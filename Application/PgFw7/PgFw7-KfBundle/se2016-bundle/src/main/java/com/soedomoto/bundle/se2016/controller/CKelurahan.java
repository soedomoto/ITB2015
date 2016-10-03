package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKecamatan;
import com.soedomoto.bundle.se2016.model.MKelurahan;
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
import static com.soedomoto.bundle.se2016.controller.CKecamatan.v103Dao;

/**
 * Created by soedomoto on 8/6/16.
 */
public class CKelurahan {
    public static Dao<MKelurahan, String> v104Dao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKelurahan.class);
        v104Dao = DaoManager.createDao(connectionSource, MKelurahan.class);
    }

    public static void registerServlets(ServletContextHandler context) {
        ServletHolder kelByKec = new ServletHolder(new KelurahanByKecamatan());
        kelByKec.setAsyncSupported(true);
        context.addServlet(kelByKec, KelurahanByKecamatan.PATH);

        ServletHolder kelByKode = new ServletHolder(new KelurahanByKode());
        kelByKode.setAsyncSupported(true);
        context.addServlet(kelByKode, KelurahanByKode.PATH);
    }

    public static class KelurahanByKecamatan extends HttpServlet {
        public static String PATH = "/kelurahan";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String kodePropinsi = req.getParameter("propinsi");
            final String kodeKabupaten = req.getParameter("kabupaten");
            final String kodeKecamatan = req.getParameter("kecamatan");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MKecamatan kecamatan = v103Dao.queryForId(kodePropinsi + kodeKabupaten + kodeKecamatan);
                            List<MKelurahan> kels = v104Dao.queryForMatching(new MKelurahan(kecamatan));

                            resp.getWriter().println(gson.toJson(kels));
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

    public static class KelurahanByKode extends HttpServlet {
        public static String PATH = "/kelurahan/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String fullKode = req.getParameter("fullKode");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MKelurahan kel = v104Dao.queryForId(fullKode);

                            resp.getWriter().println(gson.toJson(kel));
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
