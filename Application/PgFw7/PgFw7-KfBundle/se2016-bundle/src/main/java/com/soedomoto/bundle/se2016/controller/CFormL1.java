package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.Activator;
import com.soedomoto.bundle.se2016.model.*;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.soedomoto.bundle.se2016.Activator.*;
import static com.soedomoto.bundle.se2016.controller.CBlokSensus.v105Dao;
import static com.soedomoto.bundle.se2016.controller.CKabupaten.v102Dao;
import static com.soedomoto.bundle.se2016.controller.CKecamatan.v103Dao;
import static com.soedomoto.bundle.se2016.controller.CKelurahan.v104Dao;
import static com.soedomoto.bundle.se2016.controller.CKriteriaBlokSensus.v109Dao;
import static com.soedomoto.bundle.se2016.controller.CNks.v107Dao;
import static com.soedomoto.bundle.se2016.controller.CPencacah.pencacahDao;
import static com.soedomoto.bundle.se2016.controller.CPropinsi.v101Dao;
import static com.soedomoto.bundle.se2016.controller.CSls.v108Dao;
import static com.soedomoto.bundle.se2016.controller.CSubBlokSensus.v106Dao;

/**
 * Created by soedomoto on 8/13/16.
 */
public class CFormL1 {
    public static Dao<MFormL1, String> formL1Dao;
    public static Dao<MFormL1B5, String> formL1B5Dao;
    public static Dao<MFormL1B5Usaha, String> formL1B5UsahaDao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MFormL1.class);
        formL1Dao = DaoManager.createDao(connectionSource, MFormL1.class);
        TableUtils.createTableIfNotExists(connectionSource, MFormL1B5.class);
        formL1B5Dao = DaoManager.createDao(connectionSource, MFormL1B5.class);
        TableUtils.createTableIfNotExists(connectionSource, MFormL1B5Usaha.class);
        formL1B5UsahaDao = DaoManager.createDao(connectionSource, MFormL1B5Usaha.class);
    }

    public static void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new DataL1()), DataL1.PATH);
        context.addServlet(new ServletHolder(new Submit()), Submit.PATH);
        context.addServlet(new ServletHolder(new Sync()), Sync.PATH);
    }

    private static class FormL1 {
        public String v101, v102, v103, v104, v105, v106, v107, v108, v109;
        public String v201, v202, v203, v204;
        public String v301, v302, v303, v304, v305, v306, v307, v308;
        public List<FormL1B5> b5;
    }

    private static class FormL1B5 {
        public String v501, v502, v503, v504, v505, v506, v507;
        public List<FormL1B5Usaha> usaha;
    }

    private static class FormL1B5Usaha {
        public String v508, v509, v510;
    }

    public static class DataL1 extends HttpServlet {
        public static String PATH = "/l1";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePencacah = req.getParameter("pencacah");
            String kodeBlokSensus = req.getParameter("bs");

            try {
                List<MFormL1> l1s;

                if(kodePencacah != null) {
                    MPencacah pencacah = pencacahDao.queryForId(kodePencacah);
                    l1s = formL1Dao.queryForMatching(new MFormL1(pencacah));
                }
                else if(kodeBlokSensus != null) {
                    MBlokSensus bs = v105Dao.queryForId(kodeBlokSensus);
                    l1s = formL1Dao.queryForMatching(new MFormL1(bs));
                }
                else {
                    l1s = formL1Dao.queryForAll();
                }

                for(MFormL1 l1 : l1s) {
                    List<MFormL1B5> b5s = formL1B5Dao.queryForMatching(new MFormL1B5(l1));
                    for(MFormL1B5 b5 : b5s) {
                        List<MFormL1B5Usaha> usahaas = formL1B5UsahaDao.queryForMatching(new MFormL1B5Usaha(b5));
                        b5.setUsaha(usahaas);
                    }

                    l1.setB5(b5s);
                }

                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
                resp.getWriter().println(gson.toJson(l1s));
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (SQLException e) {
                resp.getWriter().println("Error in database connection: " + e.getMessage());
                resp.setContentType("text/plain");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    public static class Sync extends HttpServlet {
        public static String PATH = "/sync";

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                int changes = 0;

                String params = "";
                Scanner s = new Scanner(req.getInputStream(), "UTF-8").useDelimiter("\\A");
                while(s.hasNext()) params += s.next();

                MFormL1[] l1s = gson.fromJson(params, MFormL1[].class);

                for(MFormL1 l1 : l1s) {
                    MFormL1 l1L = formL1Dao.queryForId(l1.getFullKode());
                    if(l1L != null) {
                        if(l1.getLastUpdate().after(l1L.getLastUpdate())) {
                            changes += formL1Dao.update(l1);
                        }
                    } else {
                        changes += formL1Dao.create(l1);
                    }

                    for(MFormL1B5 b5 : l1.getB5()) {
                        MFormL1B5 b5L = formL1B5Dao.queryForId(b5.getFullKode());
                        if(b5L != null) {
                            if(b5.getLastUpdate().after(b5L.getLastUpdate())) {
                                changes += formL1B5Dao.update(b5);
                            }
                        } else {
                            changes += formL1B5Dao.create(b5);
                        }

                        for(MFormL1B5Usaha usaha : b5.getUsaha()) {
                            MFormL1B5Usaha usahaL = formL1B5UsahaDao.queryForId(usaha.getFullKode());
                            if(usahaL != null) {
                                if(usaha.getLastUpdate().after(usahaL.getLastUpdate())) {
                                    changes += formL1B5UsahaDao.update(usaha);
                                }
                            } else {
                                changes += formL1B5UsahaDao.create(usaha);
                            }
                        }
                    }
                }

                CSync.SyncFormL1Response l1Resp = new CSync.SyncFormL1Response(Arrays.asList(l1s), changes);

                resp.getWriter().println(gson.toJson(l1Resp));
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                resp.getWriter().println("Error in database connection: " + e.getMessage());
                resp.setContentType("text/plain");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    public static class Submit extends HttpServlet {
        public static String PATH = "/submit";

        @Override
        protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            try {
                // Parse parameter
                String params = "";
                Scanner s = new Scanner(req.getInputStream(), "UTF-8").useDelimiter("\\A");
                while(s.hasNext()) params += s.next();

                //  Initiate Http Client
                HttpClient client = new HttpClient();
                try {
                    client.start();
                } catch (Exception e) {
                    _sendException("HttpClient cannot be started", e, resp);
                }

                System.out.println(String.format("This IP : %s, Real Host IP : %s", THIS_IP, REAL_HOST_IP));

                // Send to remote if THIS_IP <> REAL_HOST_IP
                if(! THIS_IP.equalsIgnoreCase(REAL_HOST_IP)) {
                    final String finalParams = params;

                    try {
                        System.out.println(String.format("Sending data, params : %s", finalParams));

                        client.POST(Activator.REAL_HOST + Activator.CONTEXT_PATH + PATH)
                            .content(new StringContentProvider(params))
                            .onRequestFailure(new Request.FailureListener() {
                                public void onFailure(Request request, Throwable failure) {
                                    System.out.println(String.format("Sending data failed, cause: %s", failure.getCause()));
                                }
                            })
                            .onResponseFailure(new Response.FailureListener() {
                                public void onFailure(Response response, Throwable failure) {
                                    System.out.println(String.format("Sending data failed, cause: %s", failure.getCause()));

                                    FormL1 jsonFormL1 = gson.fromJson(finalParams, FormL1.class);

                                    try {
                                        _saveRecord(jsonFormL1, resp);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .onResponseSuccess(new BufferingResponseListener() {
                                @Override
                                public void onComplete(Result result) {
                                    System.out.println(String.format("Sending data success, message: %s", getContentAsString()));

                                    FormL1 jsonFormL1 = gson.fromJson(getContentAsString(), FormL1.class);

                                    try {
                                        _saveRecord(jsonFormL1, resp);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .send();
                    } catch (InterruptedException e) {
                        _sendException("HttpClient interrupted", e, resp);
                    } catch (TimeoutException e) {
                        _sendException("HttpClient timeout", e, resp);
                    } catch (ExecutionException e) {
                        _sendException("HttpClient execution failed", e, resp);
                    }
                } else {
                    FormL1 jsonFormL1 = gson.fromJson(params, FormL1.class);
                    _saveRecord(jsonFormL1, resp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void _saveRecord(FormL1 jsonFormL1, HttpServletResponse resp) throws IOException {
            try {
                MPropinsi v101              = v101Dao.queryForId(jsonFormL1.v101);
                MKabupaten v102             = v102Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102);
                MKecamatan v103             = v103Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
                        jsonFormL1.v103);
                MKelurahan v104             = v104Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
                        jsonFormL1.v103 + jsonFormL1.v104);
                MBlokSensus v105            = v105Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
                        jsonFormL1.v103 + jsonFormL1.v104 + jsonFormL1.v105);
                MSubBlokSensus v106         = v106Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
                        jsonFormL1.v103 + jsonFormL1.v104 + jsonFormL1.v105 +
                        jsonFormL1.v106);
                MNks v107                   = v107Dao.queryForId(jsonFormL1.v107);
                MSls v108                   = v108Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
                        jsonFormL1.v103 + jsonFormL1.v104 + jsonFormL1.v108);
                MKriteriaBlokSensus v109    = v109Dao.queryForId(jsonFormL1.v109);

                MPencacah pencacah = pencacahDao.queryForId(jsonFormL1.v202);
                Date v204 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(jsonFormL1.v204);

                MFormL1 formL1 = new MFormL1(v101, v102, v103, v104, v105, v106, v107, v108, v109,
                        pencacah, v204, jsonFormL1.v301, jsonFormL1.v302, jsonFormL1.v303, jsonFormL1.v304,
                        jsonFormL1.v305, jsonFormL1.v306, jsonFormL1.v307, jsonFormL1.v308, new Date());
                formL1Dao.createOrUpdate(formL1);

                for(FormL1B5 b5 : jsonFormL1.b5) {
                    MFormL1B5 formL1B5 = new MFormL1B5(formL1, b5.v501, b5.v502, b5.v503, b5.v504, b5.v505,
                            b5.v506, b5.v507, new Date());
                    formL1B5Dao.createOrUpdate(formL1B5);

                    for(FormL1B5Usaha b5Usaha : b5.usaha) {
                        MFormL1B5Usaha formL1B5Usaha = new MFormL1B5Usaha(formL1B5, b5Usaha.v508, b5Usaha.v509,
                                b5Usaha.v510, new Date());
                        formL1B5UsahaDao.createOrUpdate(formL1B5Usaha);
                    }
                }

                resp.getWriter().println(gson.toJson(jsonFormL1));
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (SQLException e) {
                _sendException("Error in database connection", e, resp);
            } catch (ParseException e) {
                _sendException("Error in date format", e, resp);
            }
        }

        private void _sendException(String prefix, Exception e, HttpServletResponse resp) throws IOException {
            resp.getWriter().println(prefix + ": " + e.getMessage());
            resp.setContentType("text/plain");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

//    public static class Submit2 extends ProxyServlet {
//        public static String PATH = "/submit";
//
//        @Override
//        protected void onResponseFailure(HttpServletRequest request, final HttpServletResponse response, Response proxyResponse, Throwable failure) {
//            System.err.println(String.format("Submit to remote URI %s failed. Message: %s", request.getRequestURI(), failure.getMessage()));
//
//            if(request.getMethod().equalsIgnoreCase("POST")) {
//                try {
//                    String params = "";
//                    Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
//                    while(s.hasNext()) params += s.next();
//
//                    FormL1 jsonFormL1 = gson.fromJson(params, FormL1.class);
//
//                    try {
//                        MPropinsi v101              = v101Dao.queryForId(jsonFormL1.v101);
//                        MKabupaten v102             = v102Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102);
//                        MKecamatan v103             = v103Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
//                                                    jsonFormL1.v103);
//                        MKelurahan v104             = v104Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
//                                                    jsonFormL1.v103 + jsonFormL1.v104);
//                        MBlokSensus v105            = v105Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
//                                                    jsonFormL1.v103 + jsonFormL1.v104 + jsonFormL1.v105);
//                        MSubBlokSensus v106         = v106Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
//                                                    jsonFormL1.v103 + jsonFormL1.v104 + jsonFormL1.v105 +
//                                                    jsonFormL1.v106);
//                        MNks v107                   = v107Dao.queryForId(jsonFormL1.v107);
//                        MSls v108                   = v108Dao.queryForId(jsonFormL1.v101 + jsonFormL1.v102 +
//                                                    jsonFormL1.v103 + jsonFormL1.v104 + jsonFormL1.v108);
//                        MKriteriaBlokSensus v109    = v109Dao.queryForId(jsonFormL1.v109);
//
//                        MPencacah pencacah = pencacahDao.queryForId(jsonFormL1.v202);
//                        Date v204 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(jsonFormL1.v204);
//
//                        MFormL1 formL1 = new MFormL1(v101, v102, v103, v104, v105, v106, v107, v108, v109,
//                                pencacah, v204, jsonFormL1.v301, jsonFormL1.v302, jsonFormL1.v303, jsonFormL1.v304,
//                                jsonFormL1.v305, jsonFormL1.v306, jsonFormL1.v307, jsonFormL1.v308, new Date());
//                        formL1Dao.createOrUpdate(formL1);
//
//                        for(FormL1B5 b5 : jsonFormL1.b5) {
//                            MFormL1B5 formL1B5 = new MFormL1B5(formL1, b5.v501, b5.v502, b5.v503, b5.v504, b5.v505,
//                                    b5.v506, b5.v507, new Date());
//                            formL1B5Dao.createOrUpdate(formL1B5);
//
//                            for(FormL1B5Usaha b5Usaha : b5.usaha) {
//                                MFormL1B5Usaha formL1B5Usaha = new MFormL1B5Usaha(formL1B5, b5Usaha.v508, b5Usaha.v509,
//                                        b5Usaha.v510, new Date());
//                                formL1B5UsahaDao.createOrUpdate(formL1B5Usaha);
//                            }
//                        }
//
//                        if (!response.isCommitted()) {
//                            response.getWriter().println(gson.toJson(jsonFormL1));
//                            response.setContentType("application/json");
//                            response.setStatus(HttpServletResponse.SC_OK);
//                        }
//                    } catch (SQLException e) {
//                        response.getWriter().println("Error in database connection: " + e.getMessage());
//                        response.setContentType("text/plain");
//                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//                    } catch (ParseException e) {
//                        response.getWriter().println("Error in date format: " + e.getMessage());
//                        response.setContentType("text/plain");
//                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//                    }
//
//                    AsyncContext asyncContext = (AsyncContext)request.getAttribute(ASYNC_CONTEXT);
//                    asyncContext.complete();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        protected URI rewriteURI(HttpServletRequest request) {
//            return URI.create(Activator.REAL_HOST + Activator.CONTEXT_PATH + PATH);
//        }
//    }
}
