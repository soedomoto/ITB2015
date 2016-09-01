package com.soedomoto.bundle.se2016.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soedomoto.bundle.se2016.model.*;
import com.soedomoto.bundle.se2016.tools.IpChecker;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.soedomoto.bundle.se2016.Activator.CONTEXT_PATH;
import static com.soedomoto.bundle.se2016.Activator.REAL_HOST;
import static com.soedomoto.bundle.se2016.controller.CBlokSensus.v105Dao;
import static com.soedomoto.bundle.se2016.controller.CFormL1.formL1B5Dao;
import static com.soedomoto.bundle.se2016.controller.CFormL1.formL1B5UsahaDao;
import static com.soedomoto.bundle.se2016.controller.CFormL1.formL1Dao;
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
    private static Gson gson;
    private static HttpClient client;

    public static void createDao() throws SQLException {

    }

    public static void registerServlets(ServletContextHandler context) {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
        client = new HttpClient();

        try {
            client.start();

            String remoteIPAddr = InetAddress.getByName(new URI(REAL_HOST).getHost()).getHostAddress();
            String currentIPAddr = IpChecker.getIp();

            System.out.println(String.format("Current IP : %s", currentIPAddr));
            System.out.println(String.format("Remote IP : %s", remoteIPAddr));

            if(! currentIPAddr.equalsIgnoreCase(remoteIPAddr)) {
                new SyncMaster(client, gson).start();
                new SyncData(client, gson).start();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SyncMaster extends Thread {
        private final HttpClient _client;
        private final Gson _gson;

        public SyncMaster(HttpClient client, Gson gson) {
            _gson = gson;
            _client = client;
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

    public static class SyncData extends Thread {
        private final HttpClient _client;
        private final Gson _gson;

        public SyncData(HttpClient client, Gson gson) {
            _gson = gson;
            _client = client;
        }

        @Override
        public void run() {
            try {
                ContentResponse wcC = _client.GET(REAL_HOST + CONTEXT_PATH + CWilayahCacah.WilayahCacah.PATH +
                        "?pencacah=" + "198706152009021004");
                MWilayahCacah[] wilcahs = _gson.fromJson(wcC.getContentAsString(), MWilayahCacah[].class);
                for(MWilayahCacah wilcah : wilcahs) {
                    ContentResponse l1C = _client.GET(REAL_HOST + CONTEXT_PATH + CFormL1.DataL1.PATH +
                            "?bs=" + wilcah.getBlokSensus().getFullKode());
                    MFormL1[] l1s = _gson.fromJson(l1C.getContentAsString(), MFormL1[].class);
                    for(MFormL1 l1 : l1s) {
                        formL1Dao.createOrUpdate(l1);

                        for(MFormL1B5 b5 : l1.getB5()) {
                            formL1B5Dao.createOrUpdate(b5);

                            for(MFormL1B5Usaha usaha : b5.getUsaha()) {
                                formL1B5UsahaDao.createOrUpdate(usaha);
                            }
                        }
                    }
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
