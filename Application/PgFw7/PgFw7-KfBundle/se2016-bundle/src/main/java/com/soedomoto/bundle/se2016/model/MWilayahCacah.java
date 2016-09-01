package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

/**
 * Created by Soedomoto on 8/17/2016.
 */
@DatabaseTable(tableName = "wilayah_cacah")
public class MWilayahCacah {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MBlokSensus blokSensus;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MPencacah pencacah;
    @DatabaseField(columnName = "last_update", useGetSet = true)
    private Date lastUpdate;

    // Additional Attribute
    private List<MNks> nks;
    private List<MSls> sls;

    public MWilayahCacah() {}

    public MWilayahCacah(String fullKode, MBlokSensus blokSensus, MPencacah pencacah, Date lastUpdate) {
        this.fullKode = fullKode;
        this.blokSensus = blokSensus;
        this.pencacah = pencacah;
        this.setLastUpdate(lastUpdate);
    }

    public MWilayahCacah(MBlokSensus blokSensus, MPencacah pencacah, Date lastUpdate) {
        this.blokSensus = blokSensus;
        this.pencacah = pencacah;
        this.setLastUpdate(lastUpdate);
    }

    public MWilayahCacah(String fullKode) {
        this.fullKode = fullKode;
    }

    public MWilayahCacah(MPencacah pencacah) {
        this.pencacah = pencacah;
    }

    public String getFullKode() {
        return fullKode;
    }

    public void setFullKode(String fullKode) {
        this.fullKode = fullKode;
    }

    public MBlokSensus getBlokSensus() {
        return blokSensus;
    }

    public void setBlokSensus(MBlokSensus blokSensus) {
        this.blokSensus = blokSensus;
    }

    public MPencacah getPencacah() {
        return pencacah;
    }

    public void setPencacah(MPencacah pencacah) {
        this.pencacah = pencacah;
    }

    public List<MNks> getNks() {
        return nks;
    }

    public void setNks(List<MNks> nks) {
        this.nks = nks;
    }

    public List<MSls> getSls() {
        return sls;
    }

    public void setSls(List<MSls> sls) {
        this.sls = sls;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
