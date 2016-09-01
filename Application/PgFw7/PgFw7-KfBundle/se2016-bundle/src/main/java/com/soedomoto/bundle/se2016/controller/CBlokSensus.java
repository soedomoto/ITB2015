package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MBlokSensus;
import com.soedomoto.bundle.se2016.model.MKelurahan;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
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

        //populateData();
    }

    public static void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new BlokSensusByKelurahan()), BlokSensusByKelurahan.PATH);
        context.addServlet(new ServletHolder(new BlokSensusByKode()), BlokSensusByKode.PATH);
    }

    private static void populateData() {
        try {
            MKelurahan kelurahan = v104Dao.queryForId("1302090010");

            v105Dao.create(new MBlokSensus("001", "001B", new Date(), kelurahan));
            v105Dao.create(new MBlokSensus("002", "002B", new Date(), kelurahan));
            v105Dao.create(new MBlokSensus("003", "003B", new Date(), kelurahan));
        } catch (SQLException e) {}
    }

    public static class BlokSensusByKelurahan extends HttpServlet {
        public static String PATH = "/bloksensus";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePropinsi = req.getParameter("propinsi");
            String kodeKabupaten = req.getParameter("kabupaten");
            String kodeKecamatan = req.getParameter("kecamatan");
            String kodeKelurahan = req.getParameter("kelurahan");

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
        }
    }

    public static class BlokSensusByKode extends HttpServlet {
        public static String PATH = "/bloksensus/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String fullKode = req.getParameter("fullKode");
            String refresh = req.getParameter("refreshForeign");

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
        }
    }
}
