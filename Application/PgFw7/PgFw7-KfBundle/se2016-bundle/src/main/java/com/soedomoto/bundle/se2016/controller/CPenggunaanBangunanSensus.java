package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MPenggunaanBangunanSensus;
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
public class CPenggunaanBangunanSensus {
    private static CPenggunaanBangunanSensus cPenggunaanBangunanSensus;
    public static CPenggunaanBangunanSensus instance() {
        if(cPenggunaanBangunanSensus == null) cPenggunaanBangunanSensus = new CPenggunaanBangunanSensus();
        return cPenggunaanBangunanSensus;
    }

    private Dao<MPenggunaanBangunanSensus, String> v504Dao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MPenggunaanBangunanSensus.class);
        v504Dao = DaoManager.createDao(connectionSource, MPenggunaanBangunanSensus.class);

        populateData();
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder lsBangunan = new ServletHolder(new ListPenggunaanBangunanSensus());
        lsBangunan.setAsyncSupported(true);
        context.addServlet(lsBangunan, ListPenggunaanBangunanSensus.PATH);
    }

    private void populateData() {
        try {
            v504Dao.create(new MPenggunaanBangunanSensus(1, "Tempat usaha", new Date()));
            v504Dao.create(new MPenggunaanBangunanSensus(2, "Campuran", new Date()));
            v504Dao.create(new MPenggunaanBangunanSensus(3, "Tempat tinggal", new Date()));
            v504Dao.create(new MPenggunaanBangunanSensus(4, "Tempat ibadah, Kantor organisasi, Panti sosial", new Date()));
            v504Dao.create(new MPenggunaanBangunanSensus(5, "Tempat usaha pertanian, kantor pemerintahan, kedutaan, bangunan kosong", new Date()));
        } catch (SQLException e) {}
    }

    public Dao<MPenggunaanBangunanSensus, String> getV504Dao() {
        return v504Dao;
    }

    public static class ListPenggunaanBangunanSensus extends HttpServlet {
        public static String PATH = "/v504-options";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            List<MPenggunaanBangunanSensus> options = CPenggunaanBangunanSensus.instance().getV504Dao().queryForAll();

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
