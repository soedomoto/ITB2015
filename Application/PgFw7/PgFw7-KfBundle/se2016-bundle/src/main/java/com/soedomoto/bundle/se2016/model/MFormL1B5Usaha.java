package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by soedomoto on 8/14/16.
 */
@DatabaseTable(tableName = "form_l1_blok_5_usaha")
public class MFormL1B5Usaha {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MFormL1B5 formL1B5;
    @DatabaseField(useGetSet = true)
    private String v508;
    @DatabaseField(useGetSet = true)
    private String v509;
    @DatabaseField(useGetSet = true)
    private String v510;
    @DatabaseField(columnName = "last_update", useGetSet = true)
    private Date lastUpdate;

    public MFormL1B5Usaha() {}

    public MFormL1B5Usaha(String fullKode, MFormL1B5 formL1B5, String v508, String v509, String v510, Date lastUpdate) {
        this.fullKode = fullKode;
        this.formL1B5 = formL1B5;
        this.v508 = v508;
        this.v509 = v509;
        this.v510 = v510;
        this.setLastUpdate(lastUpdate);
    }

    public MFormL1B5Usaha(MFormL1B5 formL1B5, String v508, String v509, String v510, Date lastUpdate) {
        this.fullKode = formL1B5.getFullKode() + v508;
        this.formL1B5 = formL1B5;
        this.v508 = v508;
        this.v509 = v509;
        this.v510 = v510;
        this.setLastUpdate(lastUpdate);
    }

    public MFormL1B5Usaha(String fullKode) {
        this.fullKode = fullKode;
    }

    public MFormL1B5Usaha(MFormL1B5 formL1B5) {
        this.formL1B5 = formL1B5;
    }

    public String getFullKode() {
        return fullKode;
    }

    public void setFullKode(String fullKode) {
        this.fullKode = fullKode;
    }

    public MFormL1B5 getFormL1B5() {
        return formL1B5;
    }

    public void setFormL1B5(MFormL1B5 formL1B5) {
        this.formL1B5 = formL1B5;
    }

    public String getV508() {
        return v508;
    }

    public void setV508(String v508) {
        this.v508 = v508;
    }

    public String getV509() {
        return v509;
    }

    public void setV509(String v509) {
        this.v509 = v509;
    }

    public String getV510() {
        return v510;
    }

    public void setV510(String v510) {
        this.v510 = v510;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
