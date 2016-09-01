package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MBlokSensus;
import com.soedomoto.bundle.se2016.model.MNks;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.soedomoto.bundle.se2016.Activator.connectionSource;
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

        //populateData();
    }

    public static void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new NKSByBlokSensus()), NKSByBlokSensus.PATH);
        context.addServlet(new ServletHolder(new NKSByKode()), NKSByKode.PATH);
    }

    private static void populateData() {
        try {
            MBlokSensus blokSensus = v105Dao.queryForId("1302090010001");

            v107Dao.create(new MNks("001", "001", blokSensus));
            v107Dao.create(new MNks("002", "002", blokSensus));
            v107Dao.create(new MNks("003", "003", blokSensus));
        } catch (SQLException e) {}
    }

    public static class NKSByBlokSensus extends HttpServlet {
        public static String PATH = "/nks";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePropinsi = req.getParameter("propinsi");
            String kodeKabupaten = req.getParameter("kabupaten");
            String kodeKecamatan = req.getParameter("kecamatan");
            String kodeKelurahan = req.getParameter("kelurahan");
            String kodeBlokSensus = req.getParameter("bloksensus");

            try {
                MBlokSensus blokSensus = v105Dao.queryForId(kodePropinsi + kodeKabupaten + kodeKecamatan +
                        kodeKelurahan + kodeBlokSensus);
                List<MNks> nkss = v107Dao.queryForMatching(new MNks(blokSensus));

                resp.getWriter().println(new Gson().toJson(nkss));
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (SQLException e) {
                resp.getWriter().println("Error in database connection: " + e.getMessage());
                resp.setContentType("text/plain");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    public static class NKSByKode extends HttpServlet {
        public static String PATH = "/nks/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String fullKode = req.getParameter("fullKode");
            String refresh = req.getParameter("refreshForeign");

            try {
                MNks nks = v107Dao.queryForId(fullKode);
                if(Boolean.valueOf(refresh)) {
                    v105Dao.refresh(nks.getBlokSensus());
                    v104Dao.refresh(nks.getBlokSensus().getKelurahan());
                    v103Dao.refresh(nks.getBlokSensus().getKelurahan().getKecamatan());
                    v102Dao.refresh(nks.getBlokSensus().getKelurahan().getKecamatan().getKabupaten());
                    v101Dao.refresh(nks.getBlokSensus().getKelurahan().getKecamatan().getKabupaten().getPropinsi());
                }

                resp.getWriter().println(new Gson().toJson(nks));
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (SQLException e) {
                resp.getWriter().println("Error in database connection: " + e.getMessage());
                resp.setContentType("text/plain");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
