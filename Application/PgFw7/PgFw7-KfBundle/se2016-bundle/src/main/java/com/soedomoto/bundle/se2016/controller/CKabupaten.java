package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKabupaten;
import com.soedomoto.bundle.se2016.model.MPropinsi;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.soedomoto.bundle.se2016.Activator.connectionSource;
import static com.soedomoto.bundle.se2016.controller.CPropinsi.propinsiDao;

/**
 * Created by soedomoto on 8/5/16.
 */
public class CKabupaten {
    public static Dao<MKabupaten, String> kabupatenDao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKabupaten.class);
        kabupatenDao = DaoManager.createDao(connectionSource, MKabupaten.class);

        //populateData();
    }

    private static void populateData() {
        try {
            MPropinsi propinsi = propinsiDao.queryForId("13");

            kabupatenDao.create(new MKabupaten("01", "Kab. Kepulauan Mentawai", propinsi));
            kabupatenDao.create(new MKabupaten("02", "Kab. Pesisir Selatan", propinsi));
            kabupatenDao.create(new MKabupaten("03", "Kab. Solok", propinsi));
        } catch (SQLException e) {}
    }

    public static class KabupatenByPropinsi extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePropinsi = req.getParameter("propinsi");

            try {
                MPropinsi propinsi = propinsiDao.queryForId(kodePropinsi);
                List<MKabupaten> kabs = kabupatenDao.queryForMatching(new MKabupaten(propinsi));

                resp.getWriter().println(new Gson().toJson(kabs));
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
