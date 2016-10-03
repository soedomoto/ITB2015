package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKriteriaBlokSensus;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static com.soedomoto.bundle.se2016.Activator.gson;

/**
 * Created by soedomoto on 8/9/16.
 */
public class CKriteriaBlokSensus {
    private static CKriteriaBlokSensus cKriteriaBlokSensus;
    public static CKriteriaBlokSensus instance() {
        if(cKriteriaBlokSensus == null) cKriteriaBlokSensus = new CKriteriaBlokSensus();
        return cKriteriaBlokSensus;
    }

    private Dao<MKriteriaBlokSensus, String> v109Dao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKriteriaBlokSensus.class);
        v109Dao = DaoManager.createDao(connectionSource, MKriteriaBlokSensus.class);

        populateData();
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder lsKriteria = new ServletHolder(new ListKriteriaBlokSensus());
        lsKriteria.setAsyncSupported(true);
        context.addServlet(lsKriteria, ListKriteriaBlokSensus.PATH);
    }

    private void populateData() {
        try {
            v109Dao.create(new MKriteriaBlokSensus(1, "Pasar tradisional", new Date()));
            v109Dao.create(new MKriteriaBlokSensus(2, "Pusat perbelanjaan", new Date()));
            v109Dao.create(new MKriteriaBlokSensus(3, "Pusat perkantoran", new Date()));
            v109Dao.create(new MKriteriaBlokSensus(4, "Kawasan industri", new Date()));
            v109Dao.create(new MKriteriaBlokSensus(5, "Kawasan pariwisata", new Date()));
            v109Dao.create(new MKriteriaBlokSensus(6, "Kawasan pemukiman", new Date()));
            v109Dao.create(new MKriteriaBlokSensus(7, "Lainnya", new Date()));
        } catch (SQLException e) {}
    }

    public Dao<MKriteriaBlokSensus, String> getV109Dao() {
        return v109Dao;
    }

    public static class ListKriteriaBlokSensus extends HttpServlet {
        public static String PATH = "/v109-options";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            List<MKriteriaBlokSensus> options = CKriteriaBlokSensus.instance().getV109Dao().queryForAll();

                            resp.getWriter().println(gson.toJson(options));
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
