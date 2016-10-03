package com.soedomoto.bundle.account.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.account.Activator;
import com.soedomoto.bundle.account.model.MAccount;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Created by soedomoto on 10/1/16.
 */
public class CAccount {
    //  Instance
    private static CAccount cAccount;
    public static CAccount instance() {
        if(cAccount == null) cAccount = new CAccount();
        return cAccount;
    }

    //  Properties Methods
    private Dao<MAccount, String> _accountDao;

    public void createDao(ConnectionSource cs) throws SQLException {
        TableUtils.createTableIfNotExists(cs, MAccount.class);
        _accountDao = DaoManager.createDao(cs, MAccount.class);
    }

    public void registerServlets(ServletContextHandler ctx) {
        ServletHolder shRegister = new ServletHolder(new Register());
        shRegister.setAsyncSupported(true);
        ctx.addServlet(shRegister, Register.PATH);

        ServletHolder shLogin = new ServletHolder(new Login());
        shLogin.setAsyncSupported(true);
        ctx.addServlet(shLogin, Login.PATH);
    }

    public Dao<MAccount, String> getAccountDao() {
        return _accountDao;
    }

    //  Static servlets
    public static class Register extends HttpServlet {
        public static String PATH = "/register";

        @Override
        protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String username = req.getParameter("username");
            final String password = req.getParameter("password");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            md.update(password.getBytes());
                            StringBuffer sb = new StringBuffer();
                            for (byte b : md.digest()) sb.append(String.format("%02x", b & 0xff));
                            String hashedPassword = sb.toString();

                            MAccount account = instance().getAccountDao().queryForId(username);
                            if(account != null) {
                                resp.getWriter().println("Username has been used. Choose another username!");
                                resp.setContentType("text/plain");
                                resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                            } else {
                                account = new MAccount(username, String.valueOf(hashedPassword));
                                instance().getAccountDao().create(account);

                                resp.getWriter().println(Activator.gson.toJson(account));
                                resp.setContentType("application/json");
                                resp.setStatus(HttpServletResponse.SC_OK);
                            }
                        } catch (SQLException e) {
                            _sendException("Error in database connection", e, resp);
                        } catch (NoSuchAlgorithmException e) {
                            _sendException("Error in hashing password", e, resp);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    actx.complete();
                }
            });
        }
    }

    public static class Login extends HttpServlet {
        public static String PATH = "/login";

        @Override
        protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String username = req.getParameter("username");
            final String password = req.getParameter("password");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            md.update(password.getBytes());
                            StringBuffer sb = new StringBuffer();
                            for (byte b : md.digest()) sb.append(String.format("%02x", b & 0xff));
                            String hashedPassword = sb.toString();

                            MAccount account = instance().getAccountDao().queryForId(username);
                            if (account != null && account.getPassword().equalsIgnoreCase(String.valueOf(hashedPassword))) {
                                resp.getWriter().println(Activator.gson.toJson(account));
                                resp.setContentType("application/json");
                                resp.setStatus(HttpServletResponse.SC_OK);
                            } else {
                                resp.getWriter().println("Username and password not match!");
                                resp.setContentType("text/plain");
                                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            }
                        } catch (SQLException e) {
                            _sendException("Error in database connection", e, resp);
                        } catch (NoSuchAlgorithmException e) {
                            _sendException("Error in hashing password", e, resp);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    actx.complete();
                }
            });
        }
    }

    private static void _sendException(String prefix, Exception e, HttpServletResponse resp) throws IOException {
        resp.getWriter().println(prefix + ": " + e.getMessage());
        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}
