package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MPropinsi;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.soedomoto.bundle.se2016.Activator.connectionSource;
import static com.soedomoto.bundle.se2016.Activator.dataDir;

/**
 * Created by soedomoto on 15/07/16.
 */
public class CPropinsi {

    public static Dao<MPropinsi, String> propinsiDao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MPropinsi.class);
        propinsiDao = DaoManager.createDao(connectionSource, MPropinsi.class);

        //populateData();
    }

    private static void populateData() {
        try {
            propinsiDao.callBatchTasks(new Callable<Object>() {
                public Object call() throws Exception {
                    BufferedReader br = new BufferedReader(
                            new FileReader(dataDir + File.separator + "dbf" + File.separator + "propinsi.dbf"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] cells = line.split(",");
                        propinsiDao.create(new MPropinsi(cells[0], cells[1]));
                    }

                    return null;
                }
            });
        }
        catch (Exception e) {}
    }

    public static class ListPropinsi extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            List<MPropinsi> provs = new ArrayList<MPropinsi>();

            try {
                provs = propinsiDao.queryForAll();

                resp.getWriter().println(new Gson().toJson(provs));
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
