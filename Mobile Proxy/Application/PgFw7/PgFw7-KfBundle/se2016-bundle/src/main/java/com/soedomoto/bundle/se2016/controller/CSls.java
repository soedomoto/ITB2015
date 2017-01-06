package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MBlokSensus;
import com.soedomoto.bundle.se2016.model.MSls;
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
public class CSls {
    private static CSls cSls;
    public static CSls instance() {
        if(cSls == null) cSls = new CSls();
        return cSls;
    }

    private Dao<MSls, String> v108Dao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MSls.class);
        v108Dao = DaoManager.createDao(connectionSource, MSls.class);
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder slsByBs = new ServletHolder(new SLSByBlokSensus());
        slsByBs.setAsyncSupported(true);
        context.addServlet(slsByBs, SLSByBlokSensus.PATH);

        ServletHolder slsByKode = new ServletHolder(new SLSByKode());
        slsByKode.setAsyncSupported(true);
        context.addServlet(slsByKode, SLSByKode.PATH);
    }

    public Dao<MSls, String> getV108Dao() {
        return v108Dao;
    }

    public static class SLSByBlokSensus extends HttpServlet {
        public static String PATH = "/sls";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String kodePropinsi = req.getParameter("propinsi");
            final String kodeKabupaten = req.getParameter("kabupaten");
            final String kodeKecamatan = req.getParameter("kecamatan");
            final String kodeKelurahan = req.getParameter("kelurahan");
            final String kodeBlokSensus = req.getParameter("bloksensus");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MBlokSensus blokSensus = CBlokSensus.instance().getV105Dao().queryForId(kodePropinsi + kodeKabupaten + kodeKecamatan +
                                    kodeKelurahan + kodeBlokSensus);
                            List<MSls> slses = CSls.instance().getV108Dao().queryForMatching(new MSls(blokSensus));

                            resp.getWriter().println(gson.toJson(slses));
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

    public static class SLSByKode extends HttpServlet {
        public static String PATH = "/sls/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String fullKode = req.getParameter("fullKode");
            final String refresh = req.getParameter("refreshForeign");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MSls sls = CSls.instance().getV108Dao().queryForId(fullKode);
                            if(Boolean.valueOf(refresh)) {
                                CBlokSensus.instance().getV105Dao().refresh(sls.getBlokSensus());
                                CKelurahan.instance().getV104Dao().refresh(sls.getBlokSensus().getKelurahan());
                                CKecamatan.instance().getV103Dao().refresh(sls.getBlokSensus().getKelurahan().getKecamatan());
                                CKabupaten.instance().getV102Dao().refresh(sls.getBlokSensus().getKelurahan().getKecamatan().getKabupaten());
                                CPropinsi.instance().getV101Dao().refresh(sls.getBlokSensus().getKelurahan().getKecamatan().getKabupaten().getPropinsi());
                            }

                            resp.getWriter().println(gson.toJson(sls));
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
