package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by soedomoto on 8/6/16.
 */
@DatabaseTable(tableName = "kelurahan")
public class MKelurahan {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(useGetSet = true)
    private String kode;
    @DatabaseField(useGetSet = true)
    private String nama;
    @DatabaseField(foreign = true, /*foreignAutoRefresh = true, */useGetSet = true)
    private MKecamatan kecamatan;
    @DatabaseField(columnName = "last_update", useGetSet = true)
    private Date lastUpdate;

    public MKelurahan() {}

    public MKelurahan(String fullKode, String kode, String nama, Date lastUpdate, MKecamatan kecamatan) {
        this.fullKode = fullKode;
        this.kode = kode;
        this.nama = nama;
        this.kecamatan = kecamatan;
        this.setLastUpdate(lastUpdate);
    }

    public MKelurahan(String kode, String nama, Date lastUpdate, MKecamatan kecamatan) {
        this.kode = kode;
        this.nama = nama;
        this.kecamatan = kecamatan;
        this.fullKode = kecamatan.getFullKode() + kode;
        this.setLastUpdate(lastUpdate);
    }

    public MKelurahan(MKecamatan kecamatan) {
        this.kecamatan = kecamatan;
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

    public MKecamatan getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(MKecamatan kecamatan) {
        this.kecamatan = kecamatan;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
