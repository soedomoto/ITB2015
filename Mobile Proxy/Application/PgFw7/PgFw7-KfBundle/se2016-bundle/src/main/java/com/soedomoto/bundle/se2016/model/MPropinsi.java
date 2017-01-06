package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by soedomoto on 8/5/16.
 */
@DatabaseTable(tableName = "propinsi")
public class MPropinsi {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(useGetSet = true)
    private String kode;
    @DatabaseField(useGetSet = true)
    private String nama;
    @DatabaseField(columnName = "last_update", useGetSet = true)
    private Date lastUpdate;

    public MPropinsi() {}

    public MPropinsi(String kode, String nama, Date lastUpdate) {
        this.setKode(kode);
        this.setNama(nama);
        this.setFullKode(kode);
        this.setLastUpdate(lastUpdate);
    }

    public MPropinsi(String fullKode, String kode, String nama, Date lastUpdate) {
        this.setKode(kode);
        this.setNama(nama);
        this.setFullKode(fullKode);
        this.setLastUpdate(lastUpdate);
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

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
