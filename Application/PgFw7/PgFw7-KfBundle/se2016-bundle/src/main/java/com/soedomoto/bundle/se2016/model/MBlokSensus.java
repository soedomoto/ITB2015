package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by soedomoto on 8/6/16.
 */
@DatabaseTable(tableName = "blok_sensus")
public class MBlokSensus {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(useGetSet = true)
    private String kode;
    @DatabaseField(useGetSet = true)
    private String nama;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MKelurahan kelurahan;

    public MBlokSensus() {}

    public MBlokSensus(String fullKode, String kode, String nama, MKelurahan kelurahan) {
        this.fullKode = fullKode;
        this.kode = kode;
        this.nama = nama;
        this.kelurahan = kelurahan;
    }

    public MBlokSensus(String kode, String nama, MKelurahan kelurahan) {
        this.kode = kode;
        this.nama = nama;
        this.kelurahan = kelurahan;
        this.fullKode = kelurahan.getFullKode() + kode;
    }

    public MBlokSensus(MKelurahan kelurahan) {
        this.kelurahan = kelurahan;
    }

    public String getFullKode() {
        return fullKode;
    }

    public void setFullKode(String fullKode) {
        this.fullKode = fullKode;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public MKelurahan getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(MKelurahan kelurahan) {
        this.kelurahan = kelurahan;
    }
}
