package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by soedomoto on 8/5/16.
 */
@DatabaseTable(tableName = "kabupaten")
public class MKabupaten {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(useGetSet = true)
    private String kode;
    @DatabaseField(useGetSet = true)
    private String nama;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MPropinsi propinsi;

    public MKabupaten() {}

    public MKabupaten(String kode, String nama, MPropinsi propinsi) {
        this.kode = kode;
        this.nama = nama;
        this.propinsi = propinsi;
        this.fullKode = propinsi.getFullKode() + kode;
    }

    public MKabupaten(String fullKode, String kode, String nama, MPropinsi propinsi) {
        this.fullKode = fullKode;
        this.kode = kode;
        this.nama = nama;
        this.propinsi = propinsi;
    }

    public MKabupaten(MPropinsi propinsi) {
        this.propinsi = propinsi;
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

    public MPropinsi getPropinsi() {
        return propinsi;
    }

    public void setPropinsi(MPropinsi propinsi) {
        this.propinsi = propinsi;
    }
}
