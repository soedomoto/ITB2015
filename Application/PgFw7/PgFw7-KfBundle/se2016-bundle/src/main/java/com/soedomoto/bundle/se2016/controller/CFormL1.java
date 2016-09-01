package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.Activator;
import com.soedomoto.bundle.se2016.model.*;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static com.soedomoto.bundle.se2016.Activator.connectionSource;
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

    public static class Submit extends ProxyServlet {
        public static String PATH = "/submit";

        @Override
        protected void onResponseFailure(HttpServletRequest request, final HttpServletResponse response, Response proxyResponse, Throwable failure) {
            System.err.println(String.format("Submit to remote URI %s failed. Message: %s", request.getRequestURI(), failure.getMessage()));

            if(request.getMethod().equalsIgnoreCase("POST")) {
                try {
                    String params = "";
                    Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
                    while(s.hasNext()) params += s.next();

                    FormL1 jsonFormL1 = new Gson().fromJson(params, FormL1.class);

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
                                jsonFormL1.v305, jsonFormL1.v306, jsonFormL1.v307, jsonFormL1.v308);
                        formL1Dao.createOrUpdate(formL1);

                        for(FormL1B5 b5 : jsonFormL1.b5) {
                            MFormL1B5 formL1B5 = new MFormL1B5(formL1, b5.v501, b5.v502, b5.v503, b5.v504, b5.v505,
                                    b5.v506, b5.v507);
                            formL1B5Dao.createOrUpdate(formL1B5);

                            for(FormL1B5Usaha b5Usaha : b5.usaha) {
                                MFormL1B5Usaha formL1B5Usaha = new MFormL1B5Usaha(formL1B5, b5Usaha.v508, b5Usaha.v509,
                                        b5Usaha.v510);
                                formL1B5UsahaDao.createOrUpdate(formL1B5Usaha);
                            }
                        }

                        if (!response.isCommitted()) {
                            response.getWriter().println(new Gson().toJson(jsonFormL1));
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_OK);
                        }
                    } catch (SQLException e) {
                        response.getWriter().println("Error in database connection: " + e.getMessage());
                        response.setContentType("text/plain");
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    } catch (ParseException e) {
                        response.getWriter().println("Error in date format: " + e.getMessage());
                        response.setContentType("text/plain");
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }

                    AsyncContext asyncContext = (AsyncContext)request.getAttribute(ASYNC_CONTEXT);
                    asyncContext.complete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected URI rewriteURI(HttpServletRequest request) {
            return URI.create(Activator.REAL_HOST + Activator.CONTEXT_PATH + PATH);
        }
    }
}
