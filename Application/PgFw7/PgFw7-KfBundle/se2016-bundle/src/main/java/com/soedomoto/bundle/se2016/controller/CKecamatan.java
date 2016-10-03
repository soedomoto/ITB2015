package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MKabupaten;
import com.soedomoto.bundle.se2016.model.MKecamatan;
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
 * Created by soedomoto on 8/6/16.
 */
public class CKecamatan {
    private static CKecamatan cKecamatan;
    public static CKecamatan instance() {
        if(cKecamatan == null) cKecamatan = new CKecamatan();
        return cKecamatan;
    }

    private Dao<MKecamatan, String> v103Dao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MKecamatan.class);
        v103Dao = DaoManager.createDao(connectionSource, MKecamatan.class);
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder kecByKab = new ServletHolder(new KecamatanByKabupaten());
        kecByKab.setAsyncSupported(true);
        context.addServlet(kecByKab, KecamatanByKabupaten.PATH);

        ServletHolder kecByKode = new ServletHolder(new KecamatanByKode());
        kecByKode.setAsyncSupported(true);
        context.addServlet(kecByKode, KecamatanByKode.PATH);
    }

    public Dao<MKecamatan, String> getV103Dao() {
        return v103Dao;
    }

    public static class KecamatanByKabupaten extends HttpServlet {
        public static String PATH = "/kecamatan";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String kodePropinsi = req.getParameter("propinsi");
            final String kodeKabupaten = req.getParameter("kabupaten");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MKabupaten kabupaten = CKabupaten.instance().getV102Dao().queryForId(kodePropinsi + kodeKabupaten);
                            List<MKecamatan> kecs = CKecamatan.instance().getV103Dao().queryForMatching(new MKecamatan(kabupaten));

                            resp.getWriter().println(gson.toJson(kecs));
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

    public static class KecamatanByKode extends HttpServlet {
        public static String PATH = "/kecamatan/by-kode";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String fullKode = req.getParameter("fullKode");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MKecamatan kec = CKecamatan.instance().getV103Dao().queryForId(fullKode);

                            resp.getWriter().println(gson.toJson(kec));
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
