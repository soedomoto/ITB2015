package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MBlokSensus;
import com.soedomoto.bundle.se2016.model.MSubBlokSensus;
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

/**
 * Created by soedomoto on 8/9/16.
 */
public class CSubBlokSensus {
    public static Dao<MSubBlokSensus, String> v106Dao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MSubBlokSensus.class);
        v106Dao = DaoManager.createDao(connectionSource, MSubBlokSensus.class);

        //populateData();
    }

    public static void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new SubBlokSensusByBlokSensus()), SubBlokSensusByBlokSensus.PATH);
    }

    private static void populateData() {
        try {
            MBlokSensus blokSensus = v105Dao.queryForId("1302090010001");

            v106Dao.create(new MSubBlokSensus("001", "001B", blokSensus));
            v106Dao.create(new MSubBlokSensus("002", "002B", blokSensus));
            v106Dao.create(new MSubBlokSensus("003", "003B", blokSensus));
        } catch (SQLException e) {}
    }

    public static class SubBlokSensusByBlokSensus extends HttpServlet {
        public static String PATH = "/subbloksensus";

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
                List<MSubBlokSensus> sbss = v106Dao.queryForMatching(new MSubBlokSensus(blokSensus));

                resp.getWriter().println(new Gson().toJson(sbss));
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
