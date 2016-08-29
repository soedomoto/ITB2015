package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKriteriaBlokSensus;
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

/**
 * Created by soedomoto on 8/9/16.
 */
public class CKriteriaBlokSensus {
    public static Dao<MKriteriaBlokSensus, String> v109Dao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKriteriaBlokSensus.class);
        v109Dao = DaoManager.createDao(connectionSource, MKriteriaBlokSensus.class);

        populateData();
    }

    public static void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new ListKriteriaBlokSensus()), ListKriteriaBlokSensus.PATH);
    }

    private static void populateData() {
        try {
            v109Dao.create(new MKriteriaBlokSensus(1, "Pasar tradisional"));
            v109Dao.create(new MKriteriaBlokSensus(2, "Pusat perbelanjaan"));
            v109Dao.create(new MKriteriaBlokSensus(3, "Pusat perkantoran"));
            v109Dao.create(new MKriteriaBlokSensus(4, "Kawasan industri"));
            v109Dao.create(new MKriteriaBlokSensus(5, "Kawasan pariwisata"));
            v109Dao.create(new MKriteriaBlokSensus(6, "Kawasan pemukiman"));
            v109Dao.create(new MKriteriaBlokSensus(7, "Lainnya"));
        } catch (SQLException e) {}
    }

    public static class ListKriteriaBlokSensus extends HttpServlet {
        public static String PATH = "/v109-options";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                List<MKriteriaBlokSensus> options = v109Dao.queryForAll();

                resp.getWriter().println(new Gson().toJson(options));
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
