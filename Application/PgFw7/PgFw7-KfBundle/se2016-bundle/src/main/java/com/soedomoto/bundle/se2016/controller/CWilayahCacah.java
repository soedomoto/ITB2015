package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MNks;
import com.soedomoto.bundle.se2016.model.MPencacah;
import com.soedomoto.bundle.se2016.model.MSls;
import com.soedomoto.bundle.se2016.model.MWilayahCacah;
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
public class CWilayahCacah {
    private static CWilayahCacah cWilayahCacah;
    public static CWilayahCacah instance() {
        if(cWilayahCacah == null) cWilayahCacah = new CWilayahCacah();
        return cWilayahCacah;
    }

    private Dao<MWilayahCacah, String> wilayahCacahDao;

    public void createDao(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MWilayahCacah.class);
        wilayahCacahDao = DaoManager.createDao(connectionSource, MWilayahCacah.class);
    }

    public void registerServlets(ServletContextHandler context) {
        ServletHolder wilcah = new ServletHolder(new WilayahCacah());
        wilcah.setAsyncSupported(true);
        context.addServlet(wilcah, WilayahCacah.PATH);
    }

    public Dao<MWilayahCacah, String> getWilayahCacahDao() {
        return wilayahCacahDao;
    }

    public static class WilayahCacah extends HttpServlet {
        public static String PATH = "/wilcah";

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            final String kodePencacah = req.getParameter("pencacah");
            final String refresh = req.getParameter("refreshForeign");

            final AsyncContext actx = req.startAsync();
            actx.start(new Runnable() {
                public void run() {
                    try {
                        try {
                            List<MWilayahCacah> wilayahCacahs;

                            if(kodePencacah != null) {
                                MPencacah pencacah = CPencacah.instance().getPencacahDao().queryForId(kodePencacah);
                                wilayahCacahs = CWilayahCacah.instance().getWilayahCacahDao().queryForMatching(new MWilayahCacah(pencacah));
                            } else {
                                wilayahCacahs = CWilayahCacah.instance().getWilayahCacahDao().queryForAll();
                            }

                            for(MWilayahCacah wilayahCacah : wilayahCacahs) {
                                List<MNks> nkss = CNks.instance().getV107Dao().queryForMatching(new MNks(wilayahCacah.getBlokSensus()));
                                wilayahCacah.setNks(nkss);
                                List<MSls> slss = CSls.instance().getV108Dao().queryForMatching(new MSls(wilayahCacah.getBlokSensus()));
                                wilayahCacah.setSls(slss);

                                if(Boolean.valueOf(refresh)) {
                                    CBlokSensus.instance().getV105Dao().refresh(wilayahCacah.getBlokSensus());
                                    CKelurahan.instance().getV104Dao().refresh(wilayahCacah.getBlokSensus().getKelurahan());
                                    CKecamatan.instance().getV103Dao().refresh(wilayahCacah.getBlokSensus().getKelurahan().getKecamatan());
                                    CKabupaten.instance().getV102Dao().refresh(wilayahCacah.getBlokSensus().getKelurahan().getKecamatan().getKabupaten());
                                    CPropinsi.instance().getV101Dao().refresh(wilayahCacah.getBlokSensus().getKelurahan().getKecamatan().getKabupaten().getPropinsi());
                                }
                            }

                            resp.getWriter().println(gson.toJson(wilayahCacahs));
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
