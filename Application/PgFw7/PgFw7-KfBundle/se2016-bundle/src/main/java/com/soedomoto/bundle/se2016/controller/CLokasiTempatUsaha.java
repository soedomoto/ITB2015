package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MLokasiTempatUsaha;
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

import static com.soedomoto.bundle.se2016.Activator.connectionSource;
import static com.soedomoto.bundle.se2016.Activator.gson;

/**
 * Created by soedomoto on 8/9/16.
 */
public class CLokasiTempatUsaha {
    public static Dao<MLokasiTempatUsaha, String> v510Dao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MLokasiTempatUsaha.class);
        v510Dao = DaoManager.createDao(connectionSource, MLokasiTempatUsaha.class);

        populateData();
    }

    public static void registerServlets(ServletContextHandler context) {
        ServletHolder lsLokasi = new ServletHolder(new ListLokasiUsahaRuta());
        lsLokasi.setAsyncSupported(true);
        context.addServlet(lsLokasi, ListLokasiUsahaRuta.PATH);
    }

    private static void populateData() {
        try {
            v510Dao.create(new MLokasiTempatUsaha(1, "Dalam tempat tinggal rumah tangga", new Date()));
            v510Dao.create(new MLokasiTempatUsaha(2, "Luar tempat tinggal rumah tangga dengan lokasi tetap dan perlengkapan bongkar pasang", new Date()));
            v510Dao.create(new MLokasiTempatUsaha(3, "Usaha keliling dan Konstruksi perorangan", new Date()));
            v510Dao.create(new MLokasiTempatUsaha(4, "Usaha pertambangan dan penggalian perorangan atau Persewaan rumah/kamar", new Date()));
        } catch (SQLException e) {}
    }

    public static class ListLokasiUsahaRuta extends HttpServlet {
        public static String PATH = "/v510-options";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            List<MLokasiTempatUsaha> options = v510Dao.queryForAll();

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
