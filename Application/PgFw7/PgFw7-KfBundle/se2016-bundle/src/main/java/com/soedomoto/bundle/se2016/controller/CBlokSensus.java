package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MBlokSensus;
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
import static com.soedomoto.bundle.se2016.controller.CKabupaten.v102Dao;
import static com.soedomoto.bundle.se2016.controller.CKecamatan.v103Dao;
import static com.soedomoto.bundle.se2016.controller.CKelurahan.v104Dao;
import static com.soedomoto.bundle.se2016.controller.CPropinsi.v101Dao;

/**
 * Created by soedomoto on 8/9/16.
 */
public class CBlokSensus {
    public static Dao<MBlokSensus, String> v105Dao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MBlokSensus.class);
        v105Dao = DaoManager.createDao(connectionSource, MBlokSensus.class);
    }

    public static void registerServlets(ServletContextHandler context) {
        ServletHolder bsByKel = new ServletHolder(new BlokSensusByKelurahan());
        bsByKel.setAsyncSupported(true);
        context.addServlet(bsByKel, BlokSensusByKelurahan.PATH);

        ServletHolder bsByKode = new ServletHolder(new BlokSensusByKode());
        bsByKode.setAsyncSupported(true);
        context.addServlet(bsByKode, BlokSensusByKode.PATH);
    }

    public static class BlokSensusByKelurahan extends HttpServlet {
        public static String PATH = "/bloksensus";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String kodePropinsi = req.getParameter("propinsi");
            final String kodeKabupaten = req.getParameter("kabupaten");
            final String kodeKecamatan = req.getParameter("kecamatan");
            final String kodeKelurahan = req.getParameter("kelurahan");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MKelurahan kelurahan = v104Dao.queryForId(kodePropinsi + kodeKabupaten + kodeKecamatan + kodeKelurahan);
                            List<MBlokSensus> bss = v105Dao.queryForMatching(new MBlokSensus(kelurahan));

                            resp.getWriter().println(gson.toJson(bss));
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

    public static class BlokSensusByKode extends HttpServlet {
        public static String PATH = "/bloksensus/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String fullKode = req.getParameter("fullKode");
            final String refresh = req.getParameter("refreshForeign");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MBlokSensus bs = v105Dao.queryForId(fullKode);
                            if(Boolean.valueOf(refresh)) {
                                v104Dao.refresh(bs.getKelurahan());
                                v103Dao.refresh(bs.getKelurahan().getKecamatan());
                                v102Dao.refresh(bs.getKelurahan().getKecamatan().getKabupaten());
                                v101Dao.refresh(bs.getKelurahan().getKecamatan().getKabupaten().getPropinsi());
                            }

                            resp.getWriter().println(gson.toJson(bs));
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
