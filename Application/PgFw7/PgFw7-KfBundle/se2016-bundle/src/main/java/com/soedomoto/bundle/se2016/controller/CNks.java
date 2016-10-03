package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MBlokSensus;
import com.soedomoto.bundle.se2016.model.MNks;
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
public class CNks {
    private static CNks cNks;
    public static CNks instance() {
        if(cNks == null) cNks = new CNks();
        return cNks;
    }

    private Dao<MNks, String> v107Dao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MNks.class);
        v107Dao = DaoManager.createDao(connectionSource, MNks.class);
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder nksByBs = new ServletHolder(new NKSByBlokSensus());
        nksByBs.setAsyncSupported(true);
        context.addServlet(nksByBs, NKSByBlokSensus.PATH);

        ServletHolder nksByKode = new ServletHolder(new NKSByKode());
        nksByKode.setAsyncSupported(true);
        context.addServlet(nksByKode, NKSByKode.PATH);
    }

    public Dao<MNks, String> getV107Dao() {
        return v107Dao;
    }

    public static class NKSByBlokSensus extends HttpServlet {
        public static String PATH = "/nks";

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
                            List<MNks> nkss = CNks.instance().getV107Dao().queryForMatching(new MNks(blokSensus));

                            resp.getWriter().println(gson.toJson(nkss));
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

    public static class NKSByKode extends HttpServlet {
        public static String PATH = "/nks/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String fullKode = req.getParameter("fullKode");
            final String refresh = req.getParameter("refreshForeign");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MNks nks = CNks.instance().getV107Dao().queryForId(fullKode);
                            if(Boolean.valueOf(refresh)) {
                                CBlokSensus.instance().getV105Dao().refresh(nks.getBlokSensus());
                                CKelurahan.instance().getV104Dao().refresh(nks.getBlokSensus().getKelurahan());
                                CKecamatan.instance().getV103Dao().refresh(nks.getBlokSensus().getKelurahan().getKecamatan());
                                CKabupaten.instance().getV102Dao().refresh(nks.getBlokSensus().getKelurahan().getKecamatan().getKabupaten());
                                CPropinsi.instance().getV101Dao().refresh(nks.getBlokSensus().getKelurahan().getKecamatan().getKabupaten().getPropinsi());
                            }

                            resp.getWriter().println(gson.toJson(nks));
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
