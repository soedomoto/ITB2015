package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MBlokSensus;
import com.soedomoto.bundle.se2016.model.MSubBlokSensus;
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

import static com.soedomoto.bundle.se2016.Activator.gson;

/**
 * Created by soedomoto on 8/9/16.
 */
public class CSubBlokSensus {
    private static CSubBlokSensus cSubBlokSensus;
    public static CSubBlokSensus instance() {
        if(cSubBlokSensus == null) cSubBlokSensus = new CSubBlokSensus();
        return cSubBlokSensus;
    }

    private Dao<MSubBlokSensus, String> v106Dao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MSubBlokSensus.class);
        v106Dao = DaoManager.createDao(connectionSource, MSubBlokSensus.class);
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder sbByBs = new ServletHolder(new SubBlokSensusByBlokSensus());
        sbByBs.setAsyncSupported(true);
        context.addServlet(sbByBs, SubBlokSensusByBlokSensus.PATH);
    }

    public Dao<MSubBlokSensus, String> getV106Dao() {
        return v106Dao;
    }

    public static class SubBlokSensusByBlokSensus extends HttpServlet {
        public static String PATH = "/subbloksensus";

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
                            MBlokSensus blokSensus = CBlokSensus.instance().getV105Dao().queryForId(kodePropinsi + kodeKabupaten + kodeKecamatan +
                                    kodeKelurahan + kodeBlokSensus);
                            List<MSubBlokSensus> sbss = CSubBlokSensus.instance().getV106Dao().queryForMatching(new MSubBlokSensus(blokSensus));

                            resp.getWriter().println(gson.toJson(sbss));
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
