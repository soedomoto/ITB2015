package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MPencacah;
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
import static com.soedomoto.bundle.se2016.Activator.gson;

/**
 * Created by soedomoto on 8/18/16.
 */
public class CPencacah {
    public static Dao<MPencacah, String> pencacahDao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MPencacah.class);
        pencacahDao = DaoManager.createDao(connectionSource, MPencacah.class);
    }

    public static void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new Pencacah()), Pencacah.PATH);
    }

    public static class Pencacah extends HttpServlet {
        public static String PATH = "/pencacah";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePencacah = req.getParameter("kode");

            try {
                if(kodePencacah != null) {
                    MPencacah pencacah = pencacahDao.queryForId(kodePencacah);
                    resp.getWriter().println(gson.toJson(pencacah));
                    resp.setContentType("application/json");
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    List<MPencacah> pencacahs = pencacahDao.queryForAll();
                    resp.getWriter().println(gson.toJson(pencacahs));
                    resp.setContentType("application/json");
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            } catch (SQLException e) {
                resp.getWriter().println("Error in database connection: " + e.getMessage());
                resp.setContentType("text/plain");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
