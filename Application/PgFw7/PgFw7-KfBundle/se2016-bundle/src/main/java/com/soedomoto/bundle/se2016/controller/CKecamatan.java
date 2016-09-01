package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKabupaten;
import com.soedomoto.bundle.se2016.model.MKecamatan;
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

/**
 * Created by soedomoto on 8/6/16.
 */
public class CKecamatan {
    public static Dao<MKecamatan, String> v103Dao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKecamatan.class);
        v103Dao = DaoManager.createDao(connectionSource, MKecamatan.class);

        //populateData();
    }

    public static void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new KecamatanByKabupaten()), KecamatanByKabupaten.PATH);
        context.addServlet(new ServletHolder(new KecamatanByKode()), KecamatanByKode.PATH);
    }

    private static void populateData() {
        try {
            MKabupaten kabupaten = v102Dao.queryForId("1302");

            v103Dao.create(new MKecamatan("080", "Batang Kapas", new Date(), kabupaten));
            v103Dao.create(new MKecamatan("090", "IV Jurai", new Date(), kabupaten));
            v103Dao.create(new MKecamatan("110", "Koto XI Tarusan", new Date(), kabupaten));
        } catch (SQLException e) {}
    }

    public static class KecamatanByKabupaten extends HttpServlet {
        public static String PATH = "/kecamatan";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePropinsi = req.getParameter("propinsi");
            String kodeKabupaten = req.getParameter("kabupaten");

            try {
                MKabupaten kabupaten = v102Dao.queryForId(kodePropinsi + kodeKabupaten);
                List<MKecamatan> kecs = v103Dao.queryForMatching(new MKecamatan(kabupaten));

                resp.getWriter().println(gson.toJson(kecs));
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (SQLException e) {
                resp.getWriter().println("Error in database connection: " + e.getMessage());
                resp.setContentType("text/plain");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    public static class KecamatanByKode extends HttpServlet {
        public static String PATH = "/kecamatan/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String fullKode = req.getParameter("fullKode");

            try {
                MKecamatan kec = v103Dao.queryForId(fullKode);

                resp.getWriter().println(gson.toJson(kec));
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
