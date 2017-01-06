package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.account.model.MAccount;
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
 * Created by soedomoto on 8/18/16.
 */
public class CPencacah {
    private static CPencacah cPencacah;
    public static CPencacah instance() {
        if(cPencacah == null) cPencacah = new CPencacah();
        return cPencacah;
    }

    private Dao<MAccount, String> pencacahDao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MAccount.class);
        pencacahDao = DaoManager.createDao(connectionSource, MAccount.class);
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder pch = new ServletHolder(new Pencacah());
        pch.setAsyncSupported(true);
        context.addServlet(pch, Pencacah.PATH);
    }

    public Dao<MAccount, String> getPencacahDao() {
        return pencacahDao;
    }

    public static class Pencacah extends HttpServlet {
        public static String PATH = "/pencacah";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String kodePencacah = req.getParameter("kode");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            if(kodePencacah != null) {
                                MAccount pencacah = CPencacah.instance().getPencacahDao().queryForId(kodePencacah);
                                resp.getWriter().println(gson.toJson(pencacah));
                                resp.setContentType("application/json");
                                resp.setStatus(HttpServletResponse.SC_OK);
                            } else {
                                List<MAccount> pencacahs = CPencacah.instance().getPencacahDao().queryForAll();
                                resp.getWriter().println(gson.toJson(pencacahs));
                                resp.setContentType("application/json");
                                resp.setStatus(HttpServletResponse.SC_OK);
                            }
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
