package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

/**
 * Created by soedomoto on 8/14/16.
 */
@DatabaseTable(tableName = "form_l1")
public class MFormL1 {
    @DatabaseField(id = true, columnName = "id", useGetSet = true)
    private String fullKode;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MPropinsi v101;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MKabupaten v102;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MKecamatan v103;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MKelurahan v104;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MBlokSensus v105;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MSubBlokSensus v106;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MNks v107;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MSls v108;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MKriteriaBlokSensus v109;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, useGetSet = true)
    private MPencacah pencacah;
    @DatabaseField(useGetSet = true)
    private Date v204;
    @DatabaseField(useGetSet = true)
    private String v301;
    @DatabaseField(useGetSet = true)
    private String v302;
    @DatabaseField(useGetSet = true)
    private String v303;
    @DatabaseField(useGetSet = true)
    private String v304;
    @DatabaseField(useGetSet = true)
    private String v305;
    @DatabaseField(useGetSet = true)
    private String v306;
    @DatabaseField(useGetSet = true)
    private String v307;
    @DatabaseField(useGetSet = true)
    private String v308;

    // Additional non-persistence field
    private List<MFormL1B5> b5;

    public MFormL1() {}

    public MFormL1(String fullKode, MPropinsi v101, MKabupaten v102, MKecamatan v103, MKelurahan v104, MBlokSensus v105,
                   MSubBlokSensus v106, MNks v107, MSls v108, MKriteriaBlokSensus v109, MPencacah pencacah, Date v204,
                   String v301, String v302, String v303, String v304, String v305, String v306, String v307,
                   String v308) {
        this.fullKode = fullKode;
        this.v101 = v101;
        this.v102 = v102;
        this.v103 = v103;
        this.v104 = v104;
        this.v105 = v105;
        this.v106 = v106;
        this.v107 = v107;
        this.v108 = v108;
        this.v109 = v109;
        this.pencacah = pencacah;
        this.v204 = v204;
        this.v301 = v301;
        this.v302 = v302;
        this.v303 = v303;
        this.v304 = v304;
        this.v305 = v305;
        this.v306 = v306;
        this.v307 = v307;
        this.v308 = v308;
    }

    public MFormL1(MPropinsi v101, MKabupaten v102, MKecamatan v103, MKelurahan v104, MBlokSensus v105,
                   MSubBlokSensus v106, MNks v107, MSls v108, MKriteriaBlokSensus v109, MPencacah pencacah, Date v204,
                   String v301, String v302, String v303, String v304, String v305, String v306, String v307,
                   String v308) {
        this.fullKode = v105.getFullKode();
        this.v101 = v101;
        this.v102 = v102;
        this.v103 = v103;
        this.v104 = v104;
        this.v105 = v105;
        this.v106 = v106;
        this.v107 = v107;
        this.v108 = v108;
        this.v109 = v109;
        this.pencacah = pencacah;
        this.v204 = v204;
        this.v301 = v301;
        this.v302 = v302;
        this.v303 = v303;
        this.v304 = v304;
        this.v305 = v305;
        this.v306 = v306;
        this.v307 = v307;
        this.v308 = v308;
    }

    public MFormL1(String fullKode) {
        this.fullKode = fullKode;
    }

    public MFormL1(MPencacah pencacah) {
        this.pencacah = pencacah;
    }

    public MFormL1(MBlokSensus blokSensus) {
        this.v105 = blokSensus;
    }

    public String getFullKode() {
        return fullKode;
    }

    public void setFullKode(String fullKode) {
        this.fullKode = fullKode;
    }

    public MPropinsi getV101() {
        return v101;
    }

    public void setV101(MPropinsi v101) {
        this.v101 = v101;
    }

    public MKabupaten getV102() {
        return v102;
    }

    public void setV102(MKabupaten v102) {
        this.v102 = v102;
    }

    public MKecamatan getV103() {
        return v103;
    }

    public void setV103(MKecamatan v103) {
        this.v103 = v103;
    }

    public MKelurahan getV104() {
        return v104;
    }

    public void setV104(MKelurahan v104) {
        this.v104 = v104;
    }

    public MBlokSensus getV105() {
        return v105;
    }

    public void setV105(MBlokSensus v105) {
        this.v105 = v105;
    }

    public MSubBlokSensus getV106() {
        return v106;
    }

    public void setV106(MSubBlokSensus v106) {
        this.v106 = v106;
    }

    public MNks getV107() {
        return v107;
    }

    public void setV107(MNks v107) {
        this.v107 = v107;
    }

    public MSls getV108() {
        return v108;
    }

    public void setV108(MSls v108) {
        this.v108 = v108;
    }

    public MKriteriaBlokSensus getV109() {
        return v109;
    }

    public void setV109(MKriteriaBlokSensus v109) {
        this.v109 = v109;
    }

    public MPencacah getPencacah() {
        return pencacah;
    }

    public void setPencacah(MPencacah pencacah) {
        this.pencacah = pencacah;
    }

    public Date getV204() {
        return v204;
    }

    public void setV204(Date v204) {
        this.v204 = v204;
    }

    public String getV301() {
        return v301;
    }

    public void setV301(String v301) {
        this.v301 = v301;
    }

    public String getV302() {
        return v302;
    }

    public void setV302(String v302) {
        this.v302 = v302;
    }

    public String getV303() {
        return v303;
    }

    public void setV303(String v303) {
        this.v303 = v303;
    }

    public String getV304() {
        return v304;
    }

    public void setV304(String v304) {
        this.v304 = v304;
    }

    public String getV305() {
        return v305;
    }

    public void setV305(String v305) {
        this.v305 = v305;
    }

    public String getV306() {
        return v306;
    }

    public void setV306(String v306) {
        this.v306 = v306;
    }

    public String getV307() {
        return v307;
    }

    public void setV307(String v307) {
        this.v307 = v307;
    }

    public String getV308() {
        return v308;
    }

    public void setV308(String v308) {
        this.v308 = v308;
    }

    public List<MFormL1B5> getB5() {
        return b5;
    }

    public void setB5(List<MFormL1B5> b5) {
        this.b5 = b5;
    }
}
