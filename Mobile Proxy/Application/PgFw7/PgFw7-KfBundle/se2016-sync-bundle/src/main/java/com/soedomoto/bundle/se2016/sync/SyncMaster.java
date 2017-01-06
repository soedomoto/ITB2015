package com.soedomoto.bundle.se2016.sync;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.soedomoto.bundle.se2016.controller.*;
import com.soedomoto.bundle.se2016.model.*;
import com.soedomoto.bundle.se2016.service.AccountHandlerService;
import com.soedomoto.bundle.se2016.service.DaoHandlerService;
import com.soedomoto.bundle.se2016.service.PropertyHandlerService;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by soedomoto on 02/09/16.
 */
public class SyncMaster implements Runnable {
    private final HttpClient _client;
    private final PropertyHandlerService _properties;
    private final DaoHandlerService _dao;
    private final Gson _gson;
    private final AccountHandlerService _account;
    private int _interval;
    private final int _global_interval;

    public SyncMaster(Integer interval, PropertyHandlerService properties, DaoHandlerService dao, AccountHandlerService account, HttpClient client, Gson gson) {
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

            System.out.println(String.format("* %s Records changed. Next sync Master Tables will be held in %s seconds!",
                    changes, _interval /1000));

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
            ContentResponse wcR = _client.GET(_properties.getRealHost() + _properties.getContextPath() +
                    CWilayahCacah.WilayahCacah.PATH + "?pencacah=" + _account.getPencacahID());

            try {
                MWilayahCacah[] wilcahs = _gson.fromJson(wcR.getContentAsString(), MWilayahCacah[].class);

                for (MWilayahCacah wilcah : wilcahs) {
                    MWilayahCacah wilcahL = _dao.wilayahCacahDao().queryForId(wilcah.getFullKode());
                    if (wilcahL != null) {
                        if (wilcah.getLastUpdate().after(wilcahL.getLastUpdate())) {
                            changes += _dao.wilayahCacahDao().update(wilcah);
                        }
                    } else {
                        changes += _dao.wilayahCacahDao().create(wilcah);
                    }

                    ContentResponse v105R = _client.GET(_properties.getRealHost() + _properties.getContextPath() + CBlokSensus.BlokSensusByKode.PATH +
                            "?fullKode=" + wilcah.getBlokSensus().getFullKode());
                    MBlokSensus v105 = _gson.fromJson(v105R.getContentAsString(), MBlokSensus.class);

                    ContentResponse v104R = _client.GET(_properties.getRealHost() + _properties.getContextPath() + CKelurahan.KelurahanByKode.PATH +
                            "?fullKode=" + v105.getKelurahan().getFullKode());
                    MKelurahan v104 = _gson.fromJson(v104R.getContentAsString(), MKelurahan.class);

                    ContentResponse v103R = _client.GET(_properties.getRealHost() + _properties.getContextPath() + CKecamatan.KecamatanByKode.PATH +
                            "?fullKode=" + v104.getKecamatan().getFullKode());
                    MKecamatan v103 = _gson.fromJson(v103R.getContentAsString(), MKecamatan.class);

                    ContentResponse v102R = _client.GET(_properties.getRealHost() + _properties.getContextPath() + CKabupaten.KabupatenByKode.PATH +
                            "?fullKode=" + v103.getKabupaten().getFullKode());
                    MKabupaten v102 = _gson.fromJson(v102R.getContentAsString(), MKabupaten.class);

                    ContentResponse v101R = _client.GET(_properties.getRealHost() + _properties.getContextPath() + CPropinsi.PropinsiByKode.PATH +
                            "?fullKode=" + v102.getPropinsi().getFullKode());
                    MPropinsi v101 = _gson.fromJson(v101R.getContentAsString(), MPropinsi.class);


                    MPropinsi v101L = _dao.v101Dao().queryForId(v101.getFullKode());
                    if (v101L != null) {
                        if (v101.getLastUpdate().after(v101L.getLastUpdate())) {
                            changes += _dao.v101Dao().update(v101);
                        }
                    } else {
                        changes += _dao.v101Dao().create(v101);
                    }

                    MKabupaten v102L = _dao.v102Dao().queryForId(v102.getFullKode());
                    if (v102L != null) {
                        if (v102.getLastUpdate().after(v102L.getLastUpdate())) {
                            changes += _dao.v102Dao().update(v102);
                        }
                    } else {
                        changes += _dao.v102Dao().create(v102);
                    }

                    MKecamatan v103L = _dao.v103Dao().queryForId(v103.getFullKode());
                    if (v103L != null) {
                        if (v103.getLastUpdate().after(v103L.getLastUpdate())) {
                            changes += _dao.v103Dao().update(v103);
                        }
                    } else {
                        changes += _dao.v103Dao().create(v103);
                    }

                    MKelurahan v104L = _dao.v104Dao().queryForId(v104.getFullKode());
                    if (v104L != null) {
                        if (v104.getLastUpdate().after(v104L.getLastUpdate())) {
                            changes += _dao.v104Dao().update(v104);
                        }
                    } else {
                        changes += _dao.v104Dao().create(v104);
                    }

                    MBlokSensus v105L = _dao.v105Dao().queryForId(v105.getFullKode());
                    if (v105L != null) {
                        if (v105.getLastUpdate().after(v105L.getLastUpdate())) {
                            changes += _dao.v105Dao().update(v105);
                        }
                    } else {
                        changes += _dao.v105Dao().create(v105);
                    }

                    for (MNks nks : wilcah.getNks()) {
                        ContentResponse r8 = _client.GET(_properties.getRealHost() + _properties.getContextPath() + CNks.NKSByKode.PATH +
                                "?fullKode=" + nks.getFullKode());
                        MNks mnks = _gson.fromJson(r8.getContentAsString(), MNks.class);

                        MNks mnksL = _dao.v107Dao().queryForId(mnks.getFullKode());
                        if (mnksL != null) {
                            if (mnks.getLastUpdate().after(mnksL.getLastUpdate())) {
                                changes += _dao.v107Dao().update(mnks);
                            }
                        } else {
                            changes += _dao.v107Dao().create(mnks);
                        }
                    }

                    for (MSls sls : wilcah.getSls()) {
                        ContentResponse r9 = _client.GET(_properties.getRealHost() + _properties.getContextPath() + CSls.SLSByKode.PATH +
                                "?fullKode=" + sls.getFullKode());
                        MSls mSls = _gson.fromJson(r9.getContentAsString(), MSls.class);

                        MSls mSlsL = _dao.v108Dao().queryForId(mSls.getFullKode());
                        if (mSlsL != null) {
                            if (mSls.getLastUpdate().after(mSlsL.getLastUpdate())) {
                                changes += _dao.v108Dao().update(mSls);
                            }
                        } else {
                            changes += _dao.v108Dao().create(mSls);
                        }
                    }
                }
            } catch (JsonSyntaxException e) {
                System.err.println(String.format("Error JSON : %s", wcR.getContentAsString()));
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
