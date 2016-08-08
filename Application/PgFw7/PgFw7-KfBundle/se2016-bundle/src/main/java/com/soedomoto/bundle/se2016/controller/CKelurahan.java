package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKabupaten;
import com.soedomoto.bundle.se2016.model.MKecamatan;
import com.soedomoto.bundle.se2016.model.MKelurahan;
import com.soedomoto.bundle.se2016.model.MPropinsi;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.soedomoto.bundle.se2016.Activator.connectionSource;
import static com.soedomoto.bundle.se2016.controller.CKabupaten.kabupatenDao;
import static com.soedomoto.bundle.se2016.controller.CKecamatan.kecamatanDao;
import static com.soedomoto.bundle.se2016.controller.CPropinsi.propinsiDao;

/**
 * Created by soedomoto on 8/6/16.
 */
public class CKelurahan {
    public static Dao<MKelurahan, String> kelurahanDao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKelurahan.class);
        kelurahanDao = DaoManager.createDao(connectionSource, MKelurahan.class);

        //populateData();
    }

    private static void populateData() {
        try {
            MKecamatan kecamatan = kecamatanDao.queryForId("1302090");

            kelurahanDao.create(new MKelurahan("001", "Sago Salido", kecamatan));
            kelurahanDao.create(new MKelurahan("002", "Salido", kecamatan));
            kelurahanDao.create(new MKelurahan("003", "Bunga Pasang Salido", kecamatan));
        } catch (SQLException e) {}
    }

    public static class KelurahanByKecamatan extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePropinsi = req.getParameter("propinsi");
            String kodeKabupaten = req.getParameter("kabupaten");
            String kodeKecamatan = req.getParameter("kecamatan");

            try {
                MKecamatan kecamatan = kecamatanDao.queryForId(kodePropinsi + kodeKabupaten + kodeKecamatan);
                List<MKelurahan> kels = kelurahanDao.queryForMatching(new MKelurahan(kecamatan));

                resp.getWriter().println(new Gson().toJson(kels));
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
