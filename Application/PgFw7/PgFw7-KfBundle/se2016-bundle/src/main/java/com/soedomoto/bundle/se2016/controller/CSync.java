package com.soedomoto.bundle.se2016.controller;

import com.soedomoto.bundle.se2016.model.*;
import com.soedomoto.bundle.se2016.tools.IpChecker;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.soedomoto.bundle.se2016.Activator.*;
import static com.soedomoto.bundle.se2016.controller.CBlokSensus.v105Dao;
import static com.soedomoto.bundle.se2016.controller.CFormL1.*;
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
    private static String PENCACAH_ID = "198706152009021004";
    private static HttpClient client;

    public static void createDao() throws SQLException {

    }

    public static void registerServlets(ServletContextHandler context) {
        client = new HttpClient();

        try {
            client.start();

            String remoteIPAddr = InetAddress.getByName(new URI(REAL_HOST).getHost()).getHostAddress();
            String currentIPAddr = IpChecker.getIp();

            System.out.println(String.format("Current IP : %s", currentIPAddr));
            System.out.println(String.format("Remote IP : %s", remoteIPAddr));

            if(! currentIPAddr.equalsIgnoreCase(remoteIPAddr)) {
                Thread syncMaster = new Thread(threads, new SyncMaster(client, SYNC_INTERVAL));
                syncMaster.setDaemon(true);
                syncMaster.start();

                Thread syncData = new Thread(threads, new SyncData(client, SYNC_INTERVAL));
                syncData.setDaemon(true);
                syncData.start();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SyncMaster implements Runnable {
        private final HttpClient _client;
        private int _interval;
        private final int _global_interval;

        public SyncMaster(HttpClient client, int interval) {
            _client = client;
            _interval = interval;
            _global_interval = interval;
        }

        public void run() {
            while(true) {
                int changes = sync();

                if(changes == 0) {
                    if(_interval < 1800000) _interval = _interval * 2;
                } else {
                    if(_interval > _global_interval*2) _interval = _interval / 2;
                }

                System.out.println(String.format("* Next sync Master Tables will be held in %s seconds!",
                        _interval/1000));

                try {
                    Thread.sleep(_interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private int sync() {
            int changes = 0;

            try {
                ContentResponse wcR = _client.GET(REAL_HOST + CONTEXT_PATH + CWilayahCacah.WilayahCacah.PATH +
                        "?pencacah=" + PENCACAH_ID);
                MWilayahCacah[] wilcahs = gson.fromJson(wcR.getContentAsString(), MWilayahCacah[].class);
                for(MWilayahCacah wilcah : wilcahs) {
                    ContentResponse v105R = _client.GET(REAL_HOST + CONTEXT_PATH + CBlokSensus.BlokSensusByKode.PATH +
                            "?fullKode=" + wilcah.getBlokSensus().getFullKode());
                    MBlokSensus v105 = gson.fromJson(v105R.getContentAsString(), MBlokSensus.class);

                    ContentResponse v104R = _client.GET(REAL_HOST + CONTEXT_PATH + CKelurahan.KelurahanByKode.PATH +
                            "?fullKode=" + v105.getKelurahan().getFullKode());
                    MKelurahan v104 = gson.fromJson(v104R.getContentAsString(), MKelurahan.class);

                    ContentResponse v103R = _client.GET(REAL_HOST + CONTEXT_PATH + CKecamatan.KecamatanByKode.PATH +
                            "?fullKode=" + v104.getKecamatan().getFullKode());
                    MKecamatan v103 = gson.fromJson(v103R.getContentAsString(), MKecamatan.class);

                    ContentResponse v102R = _client.GET(REAL_HOST + CONTEXT_PATH + CKabupaten.KabupatenByKode.PATH +
                            "?fullKode=" + v103.getKabupaten().getFullKode());
                    MKabupaten v102 = gson.fromJson(v102R.getContentAsString(), MKabupaten.class);

                    ContentResponse v101R = _client.GET(REAL_HOST + CONTEXT_PATH + CPropinsi.PropinsiByKode.PATH +
                            "?fullKode=" + v102.getPropinsi().getFullKode());
                    MPropinsi v101 = gson.fromJson(v101R.getContentAsString(), MPropinsi.class);


                    MPropinsi v101L = v101Dao.queryForId(v101.getFullKode());
                    if(v101L != null) {
                        if(v101.getLastUpdate().after(v101L.getLastUpdate())) {
                            changes += v101Dao.update(v101);
                        }
                    } else {
                        changes += v101Dao.create(v101);
                    }

                    MKabupaten v102L = v102Dao.queryForId(v102.getFullKode());
                    if(v102L != null) {
                        if(v102.getLastUpdate().after(v102L.getLastUpdate())) {
                            changes += v102Dao.update(v102);
                        }
                    } else {
                        changes += v102Dao.create(v102);
                    }

                    MKecamatan v103L = v103Dao.queryForId(v103.getFullKode());
                    if(v103L != null) {
                        if(v103.getLastUpdate().after(v103L.getLastUpdate())) {
                            changes += v103Dao.update(v103);
                        }
                    } else {
                        changes += v103Dao.create(v103);
                    }

                    MKelurahan v104L = v104Dao.queryForId(v104.getFullKode());
                    if(v104L != null) {
                        if(v104.getLastUpdate().after(v104L.getLastUpdate())) {
                            changes += v104Dao.update(v104);
                        }
                    } else {
                        changes += v104Dao.create(v104);
                    }

                    MBlokSensus v105L = v105Dao.queryForId(v105.getFullKode());
                    if(v105L != null) {
                        if(v105.getLastUpdate().after(v105L.getLastUpdate())) {
                            changes += v105Dao.update(v105);
                        }
                    } else {
                        changes += v105Dao.create(v105);
                    }


                    ContentResponse r7 = _client.GET(REAL_HOST + CONTEXT_PATH + CPencacah.Pencacah.PATH +
                            "?kode=" + wilcah.getPencacah().getId());
                    MPencacah pencacah = gson.fromJson(r7.getContentAsString(), MPencacah.class);

                    MPencacah pchL = pencacahDao.queryForId(pencacah.getId());
                    if(pchL != null) {
                        if(pencacah.getLastUpdate().after(pchL.getLastUpdate())) {
                            changes += pencacahDao.update(pencacah);
                        }
                    } else {
                        changes += pencacahDao.create(pencacah);
                    }


                    for(MNks nks : wilcah.getNks()) {
                        ContentResponse r8 = _client.GET(REAL_HOST + CONTEXT_PATH + CNks.NKSByKode.PATH +
                                "?fullKode=" + nks.getFullKode());
                        MNks mnks = gson.fromJson(r8.getContentAsString(), MNks.class);

                        MNks mnksL = v107Dao.queryForId(mnks.getFullKode());
                        if(mnksL != null) {
                            if(mnks.getLastUpdate().after(mnksL.getLastUpdate())) {
                                changes += v107Dao.update(mnks);
                            }
                        } else {
                            changes += v107Dao.create(mnks);
                        }
                    }

                    for(MSls sls : wilcah.getSls()) {
                        ContentResponse r9 = _client.GET(REAL_HOST + CONTEXT_PATH + CSls.SLSByKode.PATH +
                                "?fullKode=" + sls.getFullKode());
                        MSls mSls = gson.fromJson(r9.getContentAsString(), MSls.class);

                        MSls mSlsL = v108Dao.queryForId(mSls.getFullKode());
                        if(mSlsL != null) {
                            if(mSls.getLastUpdate().after(mSlsL.getLastUpdate())) {
                                changes += v108Dao.update(mSls);
                            }
                        } else {
                            changes += v108Dao.create(mSls);
                        }
                    }

                    MWilayahCacah wilcahL = wilayahCacahDao.queryForId(wilcah.getFullKode());
                    if(wilcahL != null) {
                        if(wilcah.getLastUpdate().after(wilcahL.getLastUpdate())) {
                            changes += wilayahCacahDao.update(wilcah);
                        }
                    } else {
                        changes += wilayahCacahDao.create(wilcah);
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

            return changes;
        }
    }

    public static class SyncFormL1Response {
        private List<MFormL1> formL1s;
        private int changes;

        public SyncFormL1Response() {}

        public SyncFormL1Response(List<MFormL1> formL1s, int changes) {
            this.setFormL1s(formL1s);
            this.setChanges(changes);
        }

        public List<MFormL1> getFormL1s() {
            return formL1s;
        }

        public int getChanges() {
            return changes;
        }

        public void setFormL1s(List<MFormL1> formL1s) {
            this.formL1s = formL1s;
        }

        public void setChanges(int changes) {
            this.changes = changes;
        }
    }

    public static class SyncData implements Runnable {
        private final HttpClient _client;
        private int _interval;
        private final int _global_interval;

        public SyncData(HttpClient client, int interval) {
            _client = client;
            _interval = interval;
            _global_interval = interval;
        }


        public void run() {
            while(true) {
                int changes = sync();

                if(changes == 0) {
                    if(_interval < 1800000) _interval = _interval * 2;
                } else {
                    if(_interval > _global_interval*2) _interval = _interval / 2;
                }

                System.out.println(String.format("* Next sync Data Tables will be held in %s seconds!",
                        _interval/1000));

                try {
                    Thread.sleep(_interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private int sync() {
            int changes = 0;

            try {
                ContentResponse wcC = _client.GET(REAL_HOST + CONTEXT_PATH + CWilayahCacah.WilayahCacah.PATH +
                        "?pencacah=" + PENCACAH_ID);
                MWilayahCacah[] wilcahs = gson.fromJson(wcC.getContentAsString(), MWilayahCacah[].class);
                for(MWilayahCacah wilcah : wilcahs) {
                    ContentResponse l1C = _client.GET(REAL_HOST + CONTEXT_PATH + CFormL1.DataL1.PATH +
                            "?bs=" + wilcah.getBlokSensus().getFullKode());
                    MFormL1[] l1s = gson.fromJson(l1C.getContentAsString(), MFormL1[].class);
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
                }

                for(MWilayahCacah wilcah : wilayahCacahDao.queryForMatching(
                                           new MWilayahCacah(new MPencacah(PENCACAH_ID)))) {
                    List<MFormL1> l1s = formL1Dao.queryForMatching(new MFormL1(wilcah.getBlokSensus()));
                    for(MFormL1 l1 : l1s) {
                        List<MFormL1B5> b5s = formL1B5Dao.queryForMatching(new MFormL1B5(l1));
                        for(MFormL1B5 b5 : b5s) {
                            List<MFormL1B5Usaha> usahaas = formL1B5UsahaDao.queryForMatching(new MFormL1B5Usaha(b5));
                            b5.setUsaha(usahaas);
                        }

                        l1.setB5(b5s);
                    }

                    ContentResponse resp = _client.POST(REAL_HOST + CONTEXT_PATH + CFormL1.Sync.PATH)
                            .content(new StringContentProvider(gson.toJson(l1s)), "application/json")
                            .send();

                    SyncFormL1Response l1sR = gson.fromJson(resp.getContentAsString(), SyncFormL1Response.class);
                    changes += l1sR.getChanges();
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

            return changes;
        }
    }
}
