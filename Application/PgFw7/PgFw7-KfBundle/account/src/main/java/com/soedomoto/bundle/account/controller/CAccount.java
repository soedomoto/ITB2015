package com.soedomoto.bundle.account.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.account.Activator;
import com.soedomoto.bundle.account.model.MAccount;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.soedomoto.bundle.account.Activator.*;

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

        ServletHolder shSessionReset = new ServletHolder(new Logout());
        shSessionReset.setAsyncSupported(true);
        ctx.addServlet(shSessionReset, Logout.PATH);
    }

    public Dao<MAccount, String> getAccountDao() {
        return _accountDao;
    }

    //  Static servlets
    public static class Register extends HttpServlet {
        public static String PATH = "/register";

        @Override
        protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String username = req.getParameter("username");
            final String password = req.getParameter("password");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            if(! THIS_IP.equalsIgnoreCase(REAL_HOST_IP)) {
                                //  Initiate Http Client
                                HttpClient client = new HttpClient();
                                try {
                                    client.start();
                                } catch (Exception e) {
                                    _sendException("HttpClient cannot be started", e, actx, resp);
                                }

                                // Parse parameter
                                String params = "";
                                Scanner s = new Scanner(req.getInputStream(), "UTF-8").useDelimiter("\\A");
                                while(s.hasNext()) params += s.next();

                                ContentResponse result = client.POST(Activator.REAL_HOST + Activator.CONTEXT_PATH + PATH)
                                        .content(new StringContentProvider(params))
                                        .send();
                                if(result.getStatus() == 200) {
                                    resp.getWriter().println(result.getContentAsString());
                                    resp.setContentType("application/json");
                                    resp.setStatus(HttpServletResponse.SC_OK);
                                } else {
                                    resp.getWriter().println(result.getContentAsString());
                                    resp.setContentType("text/plain");
                                    resp.setStatus(result.getStatus());
                                }
                            } else {
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
                            }
                        } catch (SQLException e) {
                            _sendException("Error in database connection", e, actx, resp);
                        } catch (NoSuchAlgorithmException e) {
                            _sendException("Error in hashing password", e, actx, resp);
                        } catch (InterruptedException e) {
                            _sendException("Error in HTTP Client", e, actx, resp);
                        } catch (ExecutionException e) {
                            _sendException("Error in HTTP Client", e, actx, resp);
                        } catch (TimeoutException e) {
                            _sendException("Error in HTTP Client", e, actx, resp);
                        }

                        actx.complete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static class Login extends HttpServlet {
        public static String PATH = "/login";

        @Override
        protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String username = req.getParameter("username");
            final String password = req.getParameter("password");
            Boolean local = Boolean.parseBoolean(req.getParameter("local"));
            final boolean isLocal = local != null ? local : false;

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        // Check required field
                        if(username == null) {
                            _sendException("{\"message\":\"Username cannot be null\"}", null, actx, resp);
                        }
                        else if(password == null) {
                            _sendException("{\"message\":\"Password cannot be null\"}", null, actx, resp);
                        }
                        else {
                            try {
                                System.out.println(String.format("Debug : This IP = %s, Real-Host IP = %s", THIS_IP, REAL_HOST_IP));
                                if(! THIS_IP.equalsIgnoreCase(REAL_HOST_IP)) {
                                    MAccount account = new MAccount(username, password);
                                    resp.setStatus(200);

                                    if(! isLocal) {
                                        URL url = new URL(Activator.REAL_HOST + Activator.CONTEXT_PATH + PATH);
                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.setRequestMethod("POST");
                                        conn.setDoOutput(true);

                                        String params = String.format("username=%s&password=%s", username, password);
                                        IOUtils.write(params, conn.getOutputStream());

                                        String strAccount = IOUtils.toString(conn.getInputStream());
                                        account = Activator.gson.fromJson(strAccount, MAccount.class);

                                        resp.setStatus(conn.getResponseCode());
                                    }

                                    // Set global username
                                    SESSION_USER_ID = account.getUsername();

                                    resp.getWriter().write(Activator.gson.toJson(account)); //401
                                    resp.setContentType("application/json");
                                    actx.complete();
                                } else {
                                    // MD5 password
                                    MessageDigest md = MessageDigest.getInstance("MD5");
                                    md.update(password.getBytes());
                                    StringBuffer sb = new StringBuffer();
                                    for (byte b : md.digest()) sb.append(String.format("%02x", b & 0xff));
                                    final String hashedPassword = sb.toString();

                                    MAccount account = instance().getAccountDao().queryForId(username);
                                    if (account != null && account.getPassword().equalsIgnoreCase(String.valueOf(hashedPassword))) {
                                        System.out.println(String.format(String.format("Debug : Account = %s", account.getUsername())));

                                        resp.getWriter().println(Activator.gson.toJson(account));
                                        resp.setStatus(HttpServletResponse.SC_OK);
                                    } else {
                                        System.out.println(String.format(String.format("Debug : Account not found")));

                                        resp.getWriter().println("{\"message\":\"Username and password not match!\"}");
                                        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    }

                                    resp.setContentType("application/json");
                                    actx.complete();
                                }
                            } catch (SQLException e) {
                                _sendException("{\"message\":\"Error in database connection\"}", e, actx, resp);
                            } catch (NoSuchAlgorithmException e) {
                                _sendException("{\"message\":\"No algorithm found\"}", e, actx, resp);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static class Logout extends HttpServlet {
        public static String PATH = "/logout";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    SESSION_USER_ID = null;
                    actx.complete();
                }
            });
        }
    }

    private static void _sendException(String prefix, Exception e, AsyncContext actx, HttpServletResponse resp) throws IOException {
        resp.getWriter().println(prefix + (e == null ? "" : ": " + e.getMessage()));
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        actx.complete();
    }

}
