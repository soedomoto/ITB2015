package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by soedomoto on 8/6/16.
 */
@DatabaseTable(tableName = "v504_options")
public class MPenggunaanBangunanSensus {
    @DatabaseField(id = true, useGetSet = true)
    private int kode;
    @DatabaseField(useGetSet = true)
    private String nama;

    public MPenggunaanBangunanSensus() {}

    public MPenggunaanBangunanSensus(int kode, String nama) {
        this.kode = kode;
        this.nama = nama;
    }

    public int getKode() {
        return kode;
    }

    public void setKode(int kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
