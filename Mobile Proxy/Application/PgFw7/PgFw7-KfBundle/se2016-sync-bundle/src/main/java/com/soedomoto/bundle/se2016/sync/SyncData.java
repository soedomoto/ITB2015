package com.soedomoto.bundle.se2016.sync;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.soedomoto.bundle.se2016.controller.CFormL1;
import com.soedomoto.bundle.se2016.controller.CSync;
import com.soedomoto.bundle.se2016.controller.CWilayahCacah;
import com.soedomoto.bundle.se2016.model.MFormL1;
import com.soedomoto.bundle.se2016.model.MFormL1B5;
import com.soedomoto.bundle.se2016.model.MFormL1B5Usaha;
import com.soedomoto.bundle.se2016.model.MWilayahCacah;
import com.soedomoto.bundle.se2016.service.AccountHandlerService;
import com.soedomoto.bundle.se2016.service.DaoHandlerService;
import com.soedomoto.bundle.se2016.service.PropertyHandlerService;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by soedomoto on 02/09/16.
 */
public class SyncData implements Runnable {
    private final HttpClient _client;
    private final PropertyHandlerService _properties;
    private final DaoHandlerService _dao;
    private final Gson _gson;
    private final AccountHandlerService _account;
    private int _interval;
    private final int _global_interval;

    public SyncData(Integer interval, PropertyHandlerService properties, DaoHandlerService dao, AccountHandlerService account, HttpClient client, Gson gson) {
        _client = client;
        _gson = gson;
        _account = account;
        _interval = interval;
        _global_interval = interval;
        _properties = properties;
        _dao = dao;
    }

    public void run() {
        while(true) {
            int changes = sync();

            if(changes == 0) {
                if(_interval < 1800000) _interval = _interval * 2;
            } else {
                if(_interval > _global_interval*2) _interval = _interval / 2;
            }

            System.out.println(String.format("* %s Records changed. Next sync Data Tables will be held in %s seconds!",
                    changes, _interval/1000));

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
            ContentResponse wcC = _client.GET(_properties.getRealHost() + _properties.getContextPath() + CWilayahCacah.WilayahCacah.PATH +
                    "?pencacah=" + _account.getPencacahID());

            try {
                MWilayahCacah[] wilcahs = _gson.fromJson(wcC.getContentAsString(), MWilayahCacah[].class);
                for (MWilayahCacah wilcah : wilcahs) {
                    ContentResponse l1C = _client.GET(_properties.getRealHost() + _properties.getContextPath() + CFormL1.DataL1.PATH +
                            "?bs=" + wilcah.getBlokSensus().getFullKode());
                    MFormL1[] l1s = _gson.fromJson(l1C.getContentAsString(), MFormL1[].class);
                    for (MFormL1 l1 : l1s) {
                        MFormL1 l1L = _dao.formL1Dao().queryForId(l1.getFullKode());
                        if (l1L != null) {
                            if (l1.getLastUpdate().after(l1L.getLastUpdate())) {
                                changes += _dao.formL1Dao().update(l1);
                            }
                        } else {
                            changes += _dao.formL1Dao().create(l1);
                        }

                        for (MFormL1B5 b5 : l1.getB5()) {
                            MFormL1B5 b5L = _dao.formL1B5Dao().queryForId(b5.getFullKode());
                            if (b5L != null) {
                                if (b5.getLastUpdate().after(b5L.getLastUpdate())) {
                                    changes += _dao.formL1B5Dao().update(b5);
                                }
                            } else {
                                changes += _dao.formL1B5Dao().create(b5);
                            }

                            for (MFormL1B5Usaha usaha : b5.getUsaha()) {
                                MFormL1B5Usaha usahaL = _dao.formL1B5UsahaDao().queryForId(usaha.getFullKode());
                                if (usahaL != null) {
                                    if (usaha.getLastUpdate().after(usahaL.getLastUpdate())) {
                                        changes += _dao.formL1B5UsahaDao().update(usaha);
                                    }
                                } else {
                                    changes += _dao.formL1B5UsahaDao().create(usaha);
                                }
                            }
                        }
                    }
                }

                for (MWilayahCacah wilcah : _dao.wilayahCacahDao().queryForMatching(new MWilayahCacah() {{
                    setPencacah(_account.getPencacahID());
                }})) {
                    List<MFormL1> l1s = _dao.formL1Dao().queryForMatching(new MFormL1(wilcah.getBlokSensus()));
                    for (MFormL1 l1 : l1s) {
                        List<MFormL1B5> b5s = _dao.formL1B5Dao().queryForMatching(new MFormL1B5(l1));
                        for (MFormL1B5 b5 : b5s) {
                            List<MFormL1B5Usaha> usahaas = _dao.formL1B5UsahaDao().queryForMatching(new MFormL1B5Usaha(b5));
                            b5.setUsaha(usahaas);
                        }

                        l1.setB5(b5s);
                    }

                    ContentResponse resp = _client.POST(_properties.getRealHost() + _properties.getContextPath() + CFormL1.Sync.PATH)
                            .content(new StringContentProvider(_gson.toJson(l1s)), "application/json")
                            .send();

                    CSync.SyncFormL1Response l1sR = _gson.fromJson(resp.getContentAsString(), CSync.SyncFormL1Response.class);
                    changes += l1sR.getChanges();
                }
            } catch (JsonSyntaxException e) {
                System.err.println(String.format("Error JSON : %s", wcC.getContentAsString()));
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
