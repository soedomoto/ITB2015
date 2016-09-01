package com.soedomoto.bundle.se2016.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.bundle.se2016.model.MNks;
import com.soedomoto.bundle.se2016.model.MPencacah;
import com.soedomoto.bundle.se2016.model.MSls;
import com.soedomoto.bundle.se2016.model.MWilayahCacah;
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
import static com.soedomoto.bundle.se2016.controller.CBlokSensus.v105Dao;
import static com.soedomoto.bundle.se2016.controller.CKabupaten.v102Dao;
import static com.soedomoto.bundle.se2016.controller.CKecamatan.v103Dao;
import static com.soedomoto.bundle.se2016.controller.CKelurahan.v104Dao;
import static com.soedomoto.bundle.se2016.controller.CNks.v107Dao;
import static com.soedomoto.bundle.se2016.controller.CPencacah.pencacahDao;
import static com.soedomoto.bundle.se2016.controller.CPropinsi.v101Dao;
import static com.soedomoto.bundle.se2016.controller.CSls.v108Dao;

/**
 * Created by soedomoto on 8/18/16.
 */
public class CWilayahCacah {
    public static Dao<MWilayahCacah, String> wilayahCacahDao;

    public static void createDao() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MWilayahCacah.class);
        wilayahCacahDao = DaoManager.createDao(connectionSource, MWilayahCacah.class);
    }

    public static void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new WilayahCacah()), WilayahCacah.PATH);
    }

    public static class WilayahCacah extends HttpServlet {
        public static String PATH = "/wilcah";

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String kodePencacah = req.getParameter("pencacah");
            String refresh = req.getParameter("refreshForeign");

            try {
                List<MWilayahCacah> wilayahCacahs;

                if(kodePencacah != null) {
                    MPencacah pencacah = pencacahDao.queryForId(kodePencacah);
                    wilayahCacahs = wilayahCacahDao.queryForMatching(new MWilayahCacah(pencacah));
                } else {
                    wilayahCacahs = wilayahCacahDao.queryForAll();
                }

                for(MWilayahCacah wilayahCacah : wilayahCacahs) {
                    List<MNks> nkss = v107Dao.queryForMatching(new MNks(wilayahCacah.getBlokSensus()));
                    wilayahCacah.setNks(nkss);
                    List<MSls> slss = v108Dao.queryForMatching(new MSls(wilayahCacah.getBlokSensus()));
                    wilayahCacah.setSls(slss);

                    if(Boolean.valueOf(refresh)) {
                        v105Dao.refresh(wilayahCacah.getBlokSensus());
                        v104Dao.refresh(wilayahCacah.getBlokSensus().getKelurahan());
                        v103Dao.refresh(wilayahCacah.getBlokSensus().getKelurahan().getKecamatan());
                        v102Dao.refresh(wilayahCacah.getBlokSensus().getKelurahan().getKecamatan().getKabupaten());
                        v101Dao.refresh(wilayahCacah.getBlokSensus().getKelurahan().getKecamatan().getKabupaten().getPropinsi());
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
        }
    }
}
