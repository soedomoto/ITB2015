package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by soedomoto on 8/6/16.
 */
@DatabaseTable(tableName = "kecamatan")
public class MKecamatan {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(useGetSet = true)
    private String kode;
    @DatabaseField(useGetSet = true)
    private String nama;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MKabupaten kabupaten;

    public MKecamatan() {}

    public MKecamatan(String fullKode, String kode, String nama, MKabupaten kabupaten) {
        this.fullKode = fullKode;
        this.kode = kode;
        this.nama = nama;
        this.kabupaten = kabupaten;
    }

    public MKecamatan(String kode, String nama, MKabupaten kabupaten) {
        this.kode = kode;
        this.nama = nama;
        this.kabupaten = kabupaten;
        this.fullKode = kabupaten.getFullKode() + kode;
    }

    public MKecamatan(MKabupaten kabupaten) {
        this.kabupaten = kabupaten;
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

    public MKabupaten getKabupaten() {
        return kabupaten;
    }

    public void setKabupaten(MKabupaten kabupaten) {
        this.kabupaten = kabupaten;
    }
}
