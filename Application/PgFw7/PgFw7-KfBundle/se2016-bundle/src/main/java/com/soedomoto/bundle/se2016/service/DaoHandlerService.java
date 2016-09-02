package com.soedomoto.bundle.se2016.service;

import com.j256.ormlite.dao.Dao;
import com.soedomoto.bundle.se2016.controller.*;
import com.soedomoto.bundle.se2016.model.*;

/**
 * Created by soedomoto on 02/09/16.
 */
public interface DaoHandlerService {
    public Dao<MPropinsi, String> v101Dao();
    public Dao<MKabupaten, String> v102Dao();
    public Dao<MKecamatan, String> v103Dao();
    public Dao<MKelurahan, String> v104Dao();
    public Dao<MBlokSensus, String> v105Dao();
    public Dao<MSubBlokSensus, String> v106Dao();
    public Dao<MNks, String> v107Dao();
    public Dao<MSls, String> v108Dao();
    public Dao<MKriteriaBlokSensus, String> v109Dao();
    public Dao<MPenggunaanBangunanSensus, String> v504Dao();
    public Dao<MLokasiTempatUsaha, String> v510Dao();
    public Dao<MPencacah, String> pencacahDao();
    public Dao<MWilayahCacah, String> wilayahCacahDao();
    public Dao<MFormL1, String> formL1Dao();
    public Dao<MFormL1B5, String> formL1B5Dao();
    public Dao<MFormL1B5Usaha, String> formL1B5UsahaDao();
}
