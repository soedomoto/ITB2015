package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

/**
 * Created by soedomoto on 8/14/16.
 */
@DatabaseTable(tableName = "form_l1_blok_5")
public class MFormL1B5 {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MFormL1 formL1;
    @DatabaseField(useGetSet = true)
    private String v501;
    @DatabaseField(useGetSet = true)
    private String v502;
    @DatabaseField(useGetSet = true)
    private String v503;
    @DatabaseField(useGetSet = true)
    private String v504;
    @DatabaseField(useGetSet = true)
    private String v505;
    @DatabaseField(useGetSet = true)
    private String v506;
    @DatabaseField(useGetSet = true)
    private String v507;
    @DatabaseField(columnName = "last_update", useGetSet = true)
    private Date lastUpdate;

    // Additional non-persistence field
    private List<MFormL1B5Usaha> usaha;

    public MFormL1B5() {}

    public MFormL1B5(String fullKode, MFormL1 formL1, String v501, String v502, String v503, String v504, String v505,
                     String v506, String v507, Date lastUpdate) {
        this.fullKode = fullKode;
        this.formL1 = formL1;
        this.v501 = v501;
        this.v502 = v502;
        this.v503 = v503;
        this.v504 = v504;
        this.v505 = v505;
        this.v506 = v506;
        this.v507 = v507;
        this.setLastUpdate(lastUpdate);
    }

    public MFormL1B5(MFormL1 formL1, String v501, String v502, String v503, String v504, String v505,
                     String v506, String v507, Date lastUpdate) {
        this.fullKode = formL1.getFullKode() + v501;
        this.formL1 = formL1;
        this.v501 = v501;
        this.v502 = v502;
        this.v503 = v503;
        this.v504 = v504;
        this.v505 = v505;
        this.v506 = v506;
        this.v507 = v507;
        this.setLastUpdate(lastUpdate);
    }

    public MFormL1B5(String fullKode) {
        this.fullKode = fullKode;
    }

    public MFormL1B5(MFormL1 formL1) {
        this.formL1 = formL1;
    }

    public String getFullKode() {
        return fullKode;
    }

    public void setFullKode(String fullKode) {
        this.fullKode = fullKode;
    }

    public MFormL1 getFormL1() {
        return formL1;
    }

    public void setFormL1(MFormL1 formL1) {
        this.formL1 = formL1;
    }

    public String getV501() {
        return v501;
    }

    public void setV501(String v501) {
        this.v501 = v501;
    }

    public String getV502() {
        return v502;
    }

    public void setV502(String v502) {
        this.v502 = v502;
    }

    public String getV503() {
        return v503;
    }

    public void setV503(String v503) {
        this.v503 = v503;
    }

    public String getV504() {
        return v504;
    }

    public void setV504(String v504) {
        this.v504 = v504;
    }

    public String getV505() {
        return v505;
    }

    public void setV505(String v505) {
        this.v505 = v505;
    }

    public String getV506() {
        return v506;
    }

    public void setV506(String v506) {
        this.v506 = v506;
    }

    public String getV507() {
        return v507;
    }

    public void setV507(String v507) {
        this.v507 = v507;
    }

    public List<MFormL1B5Usaha> getUsaha() {
        return usaha;
    }

    public void setUsaha(List<MFormL1B5Usaha> usaha) {
        this.usaha = usaha;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
