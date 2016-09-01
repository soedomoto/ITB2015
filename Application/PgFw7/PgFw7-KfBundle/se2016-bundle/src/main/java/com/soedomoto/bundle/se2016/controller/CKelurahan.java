package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKecamatan;
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
import static com.soedomoto.bundle.se2016.controller.CKecamatan.v103Dao;

/**
 * Created by soedomoto on 8/6/16.
 */
public class CKelurahan {
    public static Dao<MKelurahan, String> v104Dao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKelurahan.class);
        v104Dao = DaoManager.createDao(connectionSource, MKelurahan.class);

        //populateData();
    }

    public static void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new KelurahanByKecamatan()), KelurahanByKecamatan.PATH);
        context.addServlet(new ServletHolder(new KelurahanByKode()), KelurahanByKode.PATH);
    }

    private static void populateData() {
        try {
            MKecamatan kecamatan = v103Dao.queryForId("1302090");

            v104Dao.create(new MKelurahan("001", "Sago Salido", new Date(), kecamatan));
            v104Dao.create(new MKelurahan("002", "Salido", new Date(), kecamatan));
            v104Dao.create(new MKelurahan("003", "Bunga Pasang Salido", new Date(), kecamatan));
        } catch (SQLException e) {}
    }

    public static class KelurahanByKecamatan extends HttpServlet {
        public static String PATH = "/kelurahan";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePropinsi = req.getParameter("propinsi");
            String kodeKabupaten = req.getParameter("kabupaten");
            String kodeKecamatan = req.getParameter("kecamatan");

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
        }
    }

    public static class KelurahanByKode extends HttpServlet {
        public static String PATH = "/kelurahan/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String fullKode = req.getParameter("fullKode");

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
        }
    }
}
