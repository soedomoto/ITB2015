package com.soedomoto.sakernas.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soedomoto on 07/11/16.
 */

@DatabaseTable(tableName = "sak_ruta")
public class Ruta {
    //  BLOK I
    @DatabaseField(id = true, canBeNull = false, width = 2)
    private String b1r1;
    @DatabaseField(id = true, canBeNull = false, width = 2)
    private String b1r2;
    @DatabaseField(id = true, canBeNull = false, width = 3)
    private String b1r3;
    @DatabaseField(id = true, canBeNull = false, width = 3)
    private String b1r4;
    @DatabaseField(canBeNull = false, width = 1)
    private Integer b1r5;
    @DatabaseField(id = true, canBeNull = false, width = 4)
    private String b1r6;
    @DatabaseField(canBeNull = false, width = 5)
    private String b1r7;
    @DatabaseField(canBeNull = false, width = 2)
    private Integer b1r8;
    @DatabaseField(canBeNull = false)
    private String b1r9;
    @DatabaseField(canBeNull = false, width = 1)
    private Integer b1r10;

    //  BLOK II
    @DatabaseField
    private Integer b2r1;
    @DatabaseField
    private Integer b2r2;

    // ART
    private List<ART> arts = new ArrayList();


    public String getID() {
        return b1r1 + b1r2 + b1r3 + b1r4;
    }

    public String getB1r1() {
        return b1r1;
    }

    public void setB1r1(String b1r1) {
        this.b1r1 = b1r1;
    }

    public String getB1r2() {
        return b1r2;
    }

    public void setB1r2(String b1r2) {
        this.b1r2 = b1r2;
    }

    public String getB1r3() {
        return b1r3;
    }

    public void setB1r3(String b1r3) {
        this.b1r3 = b1r3;
    }

    public String getB1r4() {
        return b1r4;
    }

    public void setB1r4(String b1r4) {
        this.b1r4 = b1r4;
    }

    public Integer getB1r5() {
        return b1r5;
    }

    public void setB1r5(Integer b1r5) {
        this.b1r5 = b1r5;
    }

    public String getB1r6() {
        return b1r6;
    }

    public void setB1r6(String b1r6) {
        this.b1r6 = b1r6;
    }

    public String getB1r7() {
        return b1r7;
    }

    public void setB1r7(String b1r7) {
        this.b1r7 = b1r7;
    }

    public Integer getB1r8() {
        return b1r8;
    }

    public void setB1r8(Integer b1r8) {
        this.b1r8 = b1r8;
    }

    public String getB1r9() {
        return b1r9;
    }

    public void setB1r9(String b1r9) {
        this.b1r9 = b1r9;
    }

    public Integer getB1r10() {
        return b1r10;
    }

    public void setB1r10(Integer b1r10) {
        this.b1r10 = b1r10;
    }

    public Integer getB2r1() {
        return b2r1;
    }

    public void setB2r1(Integer b2r1) {
        this.b2r1 = b2r1;
    }

    public Integer getB2r2() {
        return b2r2;
    }

    public void setB2r2(Integer b2r2) {
        this.b2r2 = b2r2;
    }

    public void addArt(ART art) {
        arts.add(art);
    }

    public List<ART> getArts() {
        return arts;
    }
}
