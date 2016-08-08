package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKabupaten;
import com.soedomoto.bundle.se2016.model.MKecamatan;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.soedomoto.bundle.se2016.Activator.connectionSource;
import static com.soedomoto.bundle.se2016.controller.CKabupaten.kabupatenDao;

/**
 * Created by soedomoto on 8/6/16.
 */
public class CKecamatan {
    public static Dao<MKecamatan, String> kecamatanDao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKecamatan.class);
        kecamatanDao = DaoManager.createDao(connectionSource, MKecamatan.class);

        //populateData();
    }

    private static void populateData() {
        try {
            MKabupaten kabupaten = kabupatenDao.queryForId("1302");

            kecamatanDao.create(new MKecamatan("080", "Batang Kapas", kabupaten));
            kecamatanDao.create(new MKecamatan("090", "IV Jurai", kabupaten));
            kecamatanDao.create(new MKecamatan("110", "Koto XI Tarusan", kabupaten));
        } catch (SQLException e) {}
    }

    public static class KecamatanByKabupaten extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePropinsi = req.getParameter("propinsi");
            String kodeKabupaten = req.getParameter("kabupaten");

            try {
                MKabupaten kabupaten = kabupatenDao.queryForId(kodePropinsi + kodeKabupaten);
                List<MKecamatan> kecs = kecamatanDao.queryForMatching(new MKecamatan(kabupaten));

                resp.getWriter().println(new Gson().toJson(kecs));
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
