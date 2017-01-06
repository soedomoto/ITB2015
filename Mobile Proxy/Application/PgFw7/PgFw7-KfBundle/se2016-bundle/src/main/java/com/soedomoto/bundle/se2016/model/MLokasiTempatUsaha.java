package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by soedomoto on 8/6/16.
 */
@DatabaseTable(tableName = "v510_options")
public class MLokasiTempatUsaha {
    @DatabaseField(id = true, useGetSet = true)
    private int kode;
    @DatabaseField(useGetSet = true)
    private String nama;
    @DatabaseField(columnName = "last_update", useGetSet = true)
    private Date lastUpdate;

    public MLokasiTempatUsaha() {}

    public MLokasiTempatUsaha(int kode, String nama, Date lastUpdate) {
        this.kode = kode;
        this.nama = nama;
        this.setLastUpdate(lastUpdate);
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

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
