package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
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

import static com.soedomoto.bundle.se2016.Activator.gson;

/**
 * Created by soedomoto on 8/9/16.
 */
public class CBlokSensus {
    private static CBlokSensus cBlokSensus;
    public static CBlokSensus instance() {
        if(cBlokSensus == null) cBlokSensus = new CBlokSensus();
        return cBlokSensus;
    }

    private Dao<MBlokSensus, String> v105Dao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MBlokSensus.class);
        v105Dao = DaoManager.createDao(connectionSource, MBlokSensus.class);
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder bsByKel = new ServletHolder(new BlokSensusByKelurahan());
        bsByKel.setAsyncSupported(true);
        context.addServlet(bsByKel, BlokSensusByKelurahan.PATH);

        ServletHolder bsByKode = new ServletHolder(new BlokSensusByKode());
        bsByKode.setAsyncSupported(true);
        context.addServlet(bsByKode, BlokSensusByKode.PATH);
    }

    public Dao<MBlokSensus, String> getV105Dao() {
        return v105Dao;
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
                            MKelurahan kelurahan = CKelurahan.instance().getV104Dao().queryForId(kodePropinsi + kodeKabupaten + kodeKecamatan + kodeKelurahan);
                            List<MBlokSensus> bss = CBlokSensus.instance().getV105Dao().queryForMatching(new MBlokSensus(kelurahan));

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
                            MBlokSensus bs = CBlokSensus.instance().getV105Dao().queryForId(fullKode);
                            if(Boolean.valueOf(refresh)) {
                                CKelurahan.instance().getV104Dao().refresh(bs.getKelurahan());
                                CKecamatan.instance().getV103Dao().refresh(bs.getKelurahan().getKecamatan());
                                CKabupaten.instance().getV102Dao().refresh(bs.getKelurahan().getKecamatan().getKabupaten());
                                CPropinsi.instance().getV101Dao().refresh(bs.getKelurahan().getKecamatan().getKabupaten().getPropinsi());
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
