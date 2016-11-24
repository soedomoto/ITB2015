package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by soedomoto on 8/6/16.
 */
@DatabaseTable(tableName = "satuan_lingkungan_setempat")
public class MSls {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(useGetSet = true)
    private String kode;
    @DatabaseField(useGetSet = true)
    private String nama;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MBlokSensus blokSensus;
    @DatabaseField(columnName = "last_update", useGetSet = true)
    private Date lastUpdate;

    public MSls() {}

    public MSls(String fullKode, String kode, String nama, Date lastUpdate, MBlokSensus blokSensus) {
        this.fullKode = fullKode;
        this.kode = kode;
        this.nama = nama;
        this.blokSensus = blokSensus;
        this.setLastUpdate(lastUpdate);
    }

    public MSls(String kode, String nama, Date lastUpdate, MBlokSensus blokSensus) {
        this.kode = kode;
        this.nama = nama;
        this.blokSensus = blokSensus;
        this.fullKode = blokSensus.getKelurahan().getFullKode() + kode;
        this.setLastUpdate(lastUpdate);
    }

    public MSls(MBlokSensus blokSensus) {
        this.blokSensus = blokSensus;
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

    public MBlokSensus getBlokSensus() {
        return blokSensus;
    }

    public void setBlokSensus(MBlokSensus blokSensus) {
        this.blokSensus = blokSensus;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
