package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soedomoto.bundle.se2016.model.*;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.soedomoto.bundle.se2016.Activator.CONTEXT_PATH;
import static com.soedomoto.bundle.se2016.Activator.REAL_HOST;
import static com.soedomoto.bundle.se2016.controller.CBlokSensus.v105Dao;
import static com.soedomoto.bundle.se2016.controller.CKabupaten.v102Dao;
import static com.soedomoto.bundle.se2016.controller.CKecamatan.v103Dao;
import static com.soedomoto.bundle.se2016.controller.CKelurahan.v104Dao;
import static com.soedomoto.bundle.se2016.controller.CNks.v107Dao;
import static com.soedomoto.bundle.se2016.controller.CPencacah.pencacahDao;
import static com.soedomoto.bundle.se2016.controller.CPropinsi.v101Dao;
import static com.soedomoto.bundle.se2016.controller.CSls.v108Dao;
import static com.soedomoto.bundle.se2016.controller.CWilayahCacah.wilayahCacahDao;

/**
 * Created by soedomoto on 30/08/16.
 */
public class CSync {
    public static void createDao() throws SQLException {

    }

    public static void registerServlets(ServletContextHandler context) {
        new Sync().start();
    }

    public static class Sync extends Thread {
        private final HttpClient _client;
        private final Gson _gson;

        public Sync() {
            _gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
            _client = new HttpClient();
            try {
                _client.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                ContentResponse r1 = _client.GET(REAL_HOST + CONTEXT_PATH + CWilayahCacah.WilayahCacah.PATH +
                                                       "?pencacah=" + "198706152009021004");
                MWilayahCacah[] wilcahs = _gson.fromJson(r1.getContentAsString(), MWilayahCacah[].class);
                for(MWilayahCacah wilcah : wilcahs) {
                    ContentResponse r6 = _client.GET(REAL_HOST + CONTEXT_PATH + CBlokSensus.BlokSensusByKode.PATH +
                            "?fullKode=" + wilcah.getBlokSensus().getFullKode());
                    MBlokSensus blokSensus = _gson.fromJson(r6.getContentAsString(), MBlokSensus.class);

                    ContentResponse r5 = _client.GET(REAL_HOST + CONTEXT_PATH + CKelurahan.KelurahanByKode.PATH +
                            "?fullKode=" + blokSensus.getKelurahan().getFullKode());
                    MKelurahan kelurahan = _gson.fromJson(r5.getContentAsString(), MKelurahan.class);

                    ContentResponse r2 = _client.GET(REAL_HOST + CONTEXT_PATH + CKecamatan.KecamatanByKode.PATH +
                            "?fullKode=" + kelurahan.getKecamatan().getFullKode());
                    MKecamatan kecamatan = _gson.fromJson(r2.getContentAsString(), MKecamatan.class);

                    ContentResponse r3 = _client.GET(REAL_HOST + CONTEXT_PATH + CKabupaten.KabupatenByKode.PATH +
                            "?fullKode=" + kecamatan.getKabupaten().getFullKode());
                    MKabupaten kabupaten = _gson.fromJson(r3.getContentAsString(), MKabupaten.class);

                    ContentResponse r4 = _client.GET(REAL_HOST + CONTEXT_PATH + CPropinsi.PropinsiByKode.PATH +
                            "?fullKode=" + kabupaten.getPropinsi().getFullKode());
                    MPropinsi propinsi = _gson.fromJson(r4.getContentAsString(), MPropinsi.class);

                    v101Dao.createOrUpdate(propinsi);
                    v102Dao.createOrUpdate(kabupaten);
                    v103Dao.createOrUpdate(kecamatan);
                    v104Dao.createOrUpdate(kelurahan);
                    v105Dao.createOrUpdate(blokSensus);

                    ContentResponse r7 = _client.GET(REAL_HOST + CONTEXT_PATH + CPencacah.Pencacah.PATH +
                            "?kode=" + wilcah.getPencacah().getId());
                    MPencacah pencacah = _gson.fromJson(r7.getContentAsString(), MPencacah.class);
                    pencacahDao.createOrUpdate(pencacah);

                    for(MNks nks : wilcah.getNks()) {
                        ContentResponse r8 = _client.GET(REAL_HOST + CONTEXT_PATH + CNks.NKSByKode.PATH +
                                "?fullKode=" + nks.getFullKode());
                        MNks mnks = _gson.fromJson(r8.getContentAsString(), MNks.class);
                        v107Dao.createOrUpdate(mnks);
                    }

                    for(MSls sls : wilcah.getSls()) {
                        ContentResponse r9 = _client.GET(REAL_HOST + CONTEXT_PATH + CSls.SLSByKode.PATH +
                                "?fullKode=" + sls.getFullKode());
                        MSls mSls = _gson.fromJson(r9.getContentAsString(), MSls.class);
                        v108Dao.createOrUpdate(mSls);
                    }

                    wilayahCacahDao.createOrUpdate(wilcah);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
