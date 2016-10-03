package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
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

import static com.soedomoto.bundle.se2016.Activator.connectionSource;
import static com.soedomoto.bundle.se2016.Activator.gson;
import static com.soedomoto.bundle.se2016.controller.CBlokSensus.v105Dao;
import static com.soedomoto.bundle.se2016.controller.CKabupaten.v102Dao;
import static com.soedomoto.bundle.se2016.controller.CKecamatan.v103Dao;
import static com.soedomoto.bundle.se2016.controller.CKelurahan.v104Dao;
import static com.soedomoto.bundle.se2016.controller.CPropinsi.v101Dao;

/**
 * Created by soedomoto on 8/9/16.
 */
public class CNks {
    public static Dao<MNks, String> v107Dao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MNks.class);
        v107Dao = DaoManager.createDao(connectionSource, MNks.class);
    }

    public static void registerServlets(ServletContextHandler context) {
        ServletHolder nksByBs = new ServletHolder(new NKSByBlokSensus());
        nksByBs.setAsyncSupported(true);
        context.addServlet(nksByBs, NKSByBlokSensus.PATH);

        ServletHolder nksByKode = new ServletHolder(new NKSByKode());
        nksByKode.setAsyncSupported(true);
        context.addServlet(nksByKode, NKSByKode.PATH);
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
                            MBlokSensus blokSensus = v105Dao.queryForId(kodePropinsi + kodeKabupaten + kodeKecamatan +
                                    kodeKelurahan + kodeBlokSensus);
                            List<MNks> nkss = v107Dao.queryForMatching(new MNks(blokSensus));

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
                            MNks nks = v107Dao.queryForId(fullKode);
                            if(Boolean.valueOf(refresh)) {
                                v105Dao.refresh(nks.getBlokSensus());
                                v104Dao.refresh(nks.getBlokSensus().getKelurahan());
                                v103Dao.refresh(nks.getBlokSensus().getKelurahan().getKecamatan());
                                v102Dao.refresh(nks.getBlokSensus().getKelurahan().getKecamatan().getKabupaten());
                                v101Dao.refresh(nks.getBlokSensus().getKelurahan().getKecamatan().getKabupaten().getPropinsi());
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
