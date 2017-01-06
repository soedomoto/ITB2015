package com.soedomoto.sakernas.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by soedomoto on 07/11/16.
 */

@DatabaseTable(tableName = "sak_art")
public class ART implements Serializable {
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Ruta ruta;

    //  BLOK IV
    @DatabaseField
    private Integer b4r1;
    @DatabaseField
    private String b4r2;
    @DatabaseField
    private Integer b4r3;
    @DatabaseField
    private Integer b4r4;
    @DatabaseField
    private Date b4r5;
    @DatabaseField
    private Integer b4r6;
    @DatabaseField
    private Integer b4r7;
    @DatabaseField
    private Integer b4r8;

    //  BLOK V.A
    @DatabaseField
    private Integer b5r1a;
    @DatabaseField
    private Integer b5r1b;
    @DatabaseField
    private Integer b5r1c;
    @DatabaseField
    private Integer b5r2;
    @DatabaseField
    private String b5r2negara;
    @DatabaseField
    private String b5r3prov;
    @DatabaseField
    private String b5r3kab;
    @DatabaseField
    private Integer b5r4a;
    @DatabaseField
    private Integer b5r4b;
    @DatabaseField
    private Integer b5r4c;
    @DatabaseField
    private Integer b5r4d;
    @DatabaseField
    private Integer b5r4e;
    @DatabaseField
    private Integer b5r4f;

    //  BLOK V.B
    @DatabaseField
    private Integer b5r5a1;
    @DatabaseField
    private Integer b5r5a2;
    @DatabaseField
    private Integer b5r5a3;
    @DatabaseField
    private Integer b5r5a4;
    @DatabaseField
    private Integer b5r5b;
    @DatabaseField
    private Integer b5r6;
    @DatabaseField
    private Integer b5r7;
    @DatabaseField
    private String b5r7lainnya;
    @DatabaseField
    private Integer b5r8;
    @DatabaseField
    private Integer b5r9;
    @DatabaseField
    private Integer b5r10;

    //  BLOK V.C
    @DatabaseField
    private Integer b5r11;
    @DatabaseField
    private Integer b5r12;
    @DatabaseField
    private Integer b5r13;
    @DatabaseField
    private Integer b5r14;
    @DatabaseField
    private String b5r14lainnya;
    @DatabaseField
    private Integer b5r15a;
    @DatabaseField
    private Integer b5r15b;
    @DatabaseField
    private Integer b5r15c;
    @DatabaseField
    private Integer b5r15d;
    @DatabaseField
    private Integer b5r15e;
    @DatabaseField
    private Integer b5r15f;
    @DatabaseField
    private Integer b5r15g;
    @DatabaseField
    private Integer b5r15h;
    @DatabaseField
    private Integer b5r15i;
    @DatabaseField
    private String b5r15ilainnya;
    @DatabaseField
    private Integer b5r16a;
    @DatabaseField
    private String b5r16alainnya;
    @DatabaseField
    private Integer b5r16b;
    @DatabaseField
    private Integer b5r17a;
    @DatabaseField
    private Integer b5r17b;
    @DatabaseField
    private Integer b5r18;

    //  BLOK V.D
    @DatabaseField
    private Integer b5r19;
    @DatabaseField
    private Integer b5r20;
    @DatabaseField
    private Integer b5r21a;
    @DatabaseField
    private Integer b5r21atahun;
    @DatabaseField
    private Integer b5r21abulan;
    @DatabaseField
    private Integer b5r21b;
    @DatabaseField
    private Integer b5r22asen;
    @DatabaseField
    private Integer b5r22asel;
    @DatabaseField
    private Integer b5r22arab;
    @DatabaseField
    private Integer b5r22akam;
    @DatabaseField
    private Integer b5r22ajum;
    @DatabaseField
    private Integer b5r22asab;
    @DatabaseField
    private Integer b5r22amin;
    @DatabaseField
    private Integer b5r22ajumlah;
    @DatabaseField
    private Integer b5r22b;
    @DatabaseField
    private Integer b5r23;
    @DatabaseField
    private Integer b5r24;
    @DatabaseField
    private Integer b5r25;
    @DatabaseField
    private Integer b5r26uang;
    @DatabaseField
    private Integer b5r26barang;
    @DatabaseField
    private Integer b5r27;
    @DatabaseField
    private Integer b5r28a;
    @DatabaseField
    private Integer b5r28b;
    @DatabaseField
    private Integer b5r28c;
    @DatabaseField
    private Integer b5r28d;
    @DatabaseField
    private Integer b5r28e;
    @DatabaseField
    private Integer b5r28f;
    @DatabaseField
    private Integer b5r28g;
    @DatabaseField
    private Integer b5r29;
    @DatabaseField
    private Integer b5r30;
    @DatabaseField
    private Integer b5r31;
    @DatabaseField
    private String b5r31lainnya;
    @DatabaseField
    private Integer b5r32;
    @DatabaseField
    private String b5r32lainnya;
    @DatabaseField
    private String b5r33aprov;
    @DatabaseField
    private String b5r33akab;
    @DatabaseField
    private Integer b5r33b;
    @DatabaseField
    private Integer b5r33c;
    @DatabaseField
    private Integer b5r33d;
    @DatabaseField
    private Integer b5r33e;

    //  BLOK V.E
    @DatabaseField
    private Integer b5r34;
    @DatabaseField
    private Integer b5r35;
    @DatabaseField
    private Integer b5r36;

    //  BLOK V.F
    @DatabaseField
    private Integer b5r37asen;
    @DatabaseField
    private Integer b5r37asel;
    @DatabaseField
    private Integer b5r37arab;
    @DatabaseField
    private Integer b5r37akam;
    @DatabaseField
    private Integer b5r37ajum;
    @DatabaseField
    private Integer b5r37asab;
    @DatabaseField
    private Integer b5r37amin;
    @DatabaseField
    private Integer b5r37ajumlah;
    @DatabaseField
    private Integer b5r37b;
    @DatabaseField
    private Integer b5r38a;
    @DatabaseField
    private Integer b5r38b;
    @DatabaseField
    private Integer b5r39;

    //  BLOK V.G
    @DatabaseField
    private Integer b5r40;
    @DatabaseField
    private Integer b5r41;
    @DatabaseField
    private Integer b5r42;
    @DatabaseField
    private String b5r42lainnya;
    @DatabaseField
    private Integer b5r43;
    @DatabaseField
    private Integer b5r44;
    @DatabaseField
    private Integer b5r45;
    @DatabaseField
    private String b5r45negara;

    //  BLOK V.H
    @DatabaseField
    private Integer b5r46;
    @DatabaseField
    private Integer b5r47a;
    @DatabaseField
    private Integer b5r47b;
    @DatabaseField
    private Integer b5r47c;
    @DatabaseField
    private Integer b5r47d;
    @DatabaseField
    private Integer b5r48a;
    @DatabaseField
    private Integer b5r48b;
    @DatabaseField
    private Integer b5r49;
    @DatabaseField
    private Integer b5r50;
    @DatabaseField
    private Integer b5r51;
    @DatabaseField
    private Integer b5r52;

    //  BLOK VI
    private String catatan;


    public String getID() {
        return ruta.getID() + this.b4r1;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Integer getB4r1() {
        return b4r1;
    }

    public void setB4r1(Integer b4r1) {
        this.b4r1 = b4r1;
    }

    public String getB4r2() {
        return b4r2;
    }

    public void setB4r2(String b4r2) {
        this.b4r2 = b4r2;
    }

    public Integer getB4r3() {
        return b4r3;
    }

    public void setB4r3(Integer b4r3) {
        this.b4r3 = b4r3;
    }

    public Integer getB4r4() {
        return b4r4;
    }

    public void setB4r4(Integer b4r4) {
        this.b4r4 = b4r4;
    }

    public Date getB4r5() {
        return b4r5;
    }

    public void setB4r5(Date b4r5) {
        this.b4r5 = b4r5;
    }

    public Integer getB4r6() {
        return b4r6;
    }

    public void setB4r6(Integer b4r6) {
        this.b4r6 = b4r6;
    }

    public Integer getB4r7() {
        return b4r7;
    }

    public void setB4r7(Integer b4r7) {
        this.b4r7 = b4r7;
    }

    public Integer getB4r8() {
        return b4r8;
    }

    public void setB4r8(Integer b4r8) {
        this.b4r8 = b4r8;
    }

    public Integer getB5r1a() {
        return b5r1a;
    }

    public void setB5r1a(Integer b5r1a) {
        this.b5r1a = b5r1a;
    }

    public Integer getB5r1b() {
        return b5r1b;
    }

    public void setB5r1b(Integer b5r1b) {
        this.b5r1b = b5r1b;
    }

    public Integer getB5r1c() {
        return b5r1c;
    }

    public void setB5r1c(Integer b5r1c) {
        this.b5r1c = b5r1c;
    }

    public Integer getB5r2() {
        return b5r2;
    }

    public void setB5r2(Integer b5r2) {
        this.b5r2 = b5r2;
    }

    public String getB5r2negara() {
        return b5r2negara;
    }

    public void setB5r2negara(String b5r2negara) {
        this.b5r2negara = b5r2negara;
    }

    public String getB5r3prov() {
        return b5r3prov;
    }

    public void setB5r3prov(String b5r3prov) {
        this.b5r3prov = b5r3prov;
    }

    public String getB5r3kab() {
        return b5r3kab;
    }

    public void setB5r3kab(String b5r3kab) {
        this.b5r3kab = b5r3kab;
    }

    public Integer getB5r4a() {
        return b5r4a;
    }

    public void setB5r4a(Integer b5r4a) {
        this.b5r4a = b5r4a;
    }

    public Integer getB5r4b() {
        return b5r4b;
    }

    public void setB5r4b(Integer b5r4b) {
        this.b5r4b = b5r4b;
    }

    public Integer getB5r4c() {
        return b5r4c;
    }

    public void setB5r4c(Integer b5r4c) {
        this.b5r4c = b5r4c;
    }

    public Integer getB5r4d() {
        return b5r4d;
    }

    public void setB5r4d(Integer b5r4d) {
        this.b5r4d = b5r4d;
    }

    public Integer getB5r4e() {
        return b5r4e;
    }

    public void setB5r4e(Integer b5r4e) {
        this.b5r4e = b5r4e;
    }

    public Integer getB5r4f() {
        return b5r4f;
    }

    public void setB5r4f(Integer b5r4f) {
        this.b5r4f = b5r4f;
    }

    public Integer getB5r5a1() {
        return b5r5a1;
    }

    public void setB5r5a1(Integer b5r5a1) {
        this.b5r5a1 = b5r5a1;
    }

    public Integer getB5r5a2() {
        return b5r5a2;
    }

    public void setB5r5a2(Integer b5r5a2) {
        this.b5r5a2 = b5r5a2;
    }

    public Integer getB5r5a3() {
        return b5r5a3;
    }

    public void setB5r5a3(Integer b5r5a3) {
        this.b5r5a3 = b5r5a3;
    }

    public Integer getB5r5a4() {
        return b5r5a4;
    }

    public void setB5r5a4(Integer b5r5a4) {
        this.b5r5a4 = b5r5a4;
    }

    public Integer getB5r5b() {
        return b5r5b;
    }

    public void setB5r5b(Integer b5r5b) {
        this.b5r5b = b5r5b;
    }

    public Integer getB5r6() {
        return b5r6;
    }

    public void setB5r6(Integer b5r6) {
        this.b5r6 = b5r6;
    }

    public Integer getB5r7() {
        return b5r7;
    }

    public void setB5r7(Integer b5r7) {
        this.b5r7 = b5r7;
    }

    public String getB5r7lainnya() {
        return b5r7lainnya;
    }

    public void setB5r7lainnya(String b5r7lainnya) {
        this.b5r7lainnya = b5r7lainnya;
    }

    public Integer getB5r8() {
        return b5r8;
    }

    public void setB5r8(Integer b5r8) {
        this.b5r8 = b5r8;
    }

    public Integer getB5r9() {
        return b5r9;
    }

    public void setB5r9(Integer b5r9) {
        this.b5r9 = b5r9;
    }

    public Integer getB5r10() {
        return b5r10;
    }

    public void setB5r10(Integer b5r10) {
        this.b5r10 = b5r10;
    }

    public Integer getB5r11() {
        return b5r11;
    }

    public void setB5r11(Integer b5r11) {
        this.b5r11 = b5r11;
    }

    public Integer getB5r12() {
        return b5r12;
    }

    public void setB5r12(Integer b5r12) {
        this.b5r12 = b5r12;
    }

    public Integer getB5r13() {
        return b5r13;
    }

    public void setB5r13(Integer b5r13) {
        this.b5r13 = b5r13;
    }

    public Integer getB5r14() {
        return b5r14;
    }

    public void setB5r14(Integer b5r14) {
        this.b5r14 = b5r14;
    }

    public String getB5r14lainnya() {
        return b5r14lainnya;
    }

    public void setB5r14lainnya(String b5r14lainnya) {
        this.b5r14lainnya = b5r14lainnya;
    }

    public Integer getB5r15a() {
        return b5r15a;
    }

    public void setB5r15a(Integer b5r15a) {
        this.b5r15a = b5r15a;
    }

    public Integer getB5r15b() {
        return b5r15b;
    }

    public void setB5r15b(Integer b5r15b) {
        this.b5r15b = b5r15b;
    }

    public Integer getB5r15c() {
        return b5r15c;
    }

    public void setB5r15c(Integer b5r15c) {
        this.b5r15c = b5r15c;
    }

    public Integer getB5r15d() {
        return b5r15d;
    }

    public void setB5r15d(Integer b5r15d) {
        this.b5r15d = b5r15d;
    }

    public Integer getB5r15e() {
        return b5r15e;
    }

    public void setB5r15e(Integer b5r15e) {
        this.b5r15e = b5r15e;
    }

    public Integer getB5r15f() {
        return b5r15f;
    }

    public void setB5r15f(Integer b5r15f) {
        this.b5r15f = b5r15f;
    }

    public Integer getB5r15g() {
        return b5r15g;
    }

    public void setB5r15g(Integer b5r15g) {
        this.b5r15g = b5r15g;
    }

    public Integer getB5r15h() {
        return b5r15h;
    }

    public void setB5r15h(Integer b5r15h) {
        this.b5r15h = b5r15h;
    }

    public Integer getB5r15i() {
        return b5r15i;
    }

    public void setB5r15i(Integer b5r15i) {
        this.b5r15i = b5r15i;
    }

    public String getB5r15ilainnya() {
        return b5r15ilainnya;
    }

    public void setB5r15ilainnya(String b5r15ilainnya) {
        this.b5r15ilainnya = b5r15ilainnya;
    }

    public Integer getB5r16a() {
        return b5r16a;
    }

    public void setB5r16a(Integer b5r16a) {
        this.b5r16a = b5r16a;
    }

    public String getB5r16alainnya() {
        return b5r16alainnya;
    }

    public void setB5r16alainnya(String b5r16alainnya) {
        this.b5r16alainnya = b5r16alainnya;
    }

    public Integer getB5r16b() {
        return b5r16b;
    }

    public void setB5r16b(Integer b5r16b) {
        this.b5r16b = b5r16b;
    }

    public Integer getB5r17a() {
        return b5r17a;
    }

    public void setB5r17a(Integer b5r17a) {
        this.b5r17a = b5r17a;
    }

    public Integer getB5r17b() {
        return b5r17b;
    }

    public void setB5r17b(Integer b5r17b) {
        this.b5r17b = b5r17b;
    }

    public Integer getB5r18() {
        return b5r18;
    }

    public void setB5r18(Integer b5r18) {
        this.b5r18 = b5r18;
    }

    public Integer getB5r19() {
        return b5r19;
    }

    public void setB5r19(Integer b5r19) {
        this.b5r19 = b5r19;
    }

    public Integer getB5r20() {
        return b5r20;
    }

    public void setB5r20(Integer b5r20) {
        this.b5r20 = b5r20;
    }

    public Integer getB5r21a() {
        return b5r21a;
    }

    public void setB5r21a(Integer b5r21a) {
        this.b5r21a = b5r21a;
    }

    public Integer getB5r21atahun() {
        return b5r21atahun;
    }

    public void setB5r21atahun(Integer b5r21atahun) {
        this.b5r21atahun = b5r21atahun;
    }

    public Integer getB5r21abulan() {
        return b5r21abulan;
    }

    public void setB5r21abulan(Integer b5r21abulan) {
        this.b5r21abulan = b5r21abulan;
    }

    public Integer getB5r21b() {
        return b5r21b;
    }

    public void setB5r21b(Integer b5r21b) {
        this.b5r21b = b5r21b;
    }

    public Integer getB5r22asen() {
        return b5r22asen;
    }

    public void setB5r22asen(Integer b5r22asen) {
        this.b5r22asen = b5r22asen;
    }

    public Integer getB5r22asel() {
        return b5r22asel;
    }

    public void setB5r22asel(Integer b5r22asel) {
        this.b5r22asel = b5r22asel;
    }

    public Integer getB5r22arab() {
        return b5r22arab;
    }

    public void setB5r22arab(Integer b5r22arab) {
        this.b5r22arab = b5r22arab;
    }

    public Integer getB5r22akam() {
        return b5r22akam;
    }

    public void setB5r22akam(Integer b5r22akam) {
        this.b5r22akam = b5r22akam;
    }

    public Integer getB5r22ajum() {
        return b5r22ajum;
    }

    public void setB5r22ajum(Integer b5r22ajum) {
        this.b5r22ajum = b5r22ajum;
    }

    public Integer getB5r22asab() {
        return b5r22asab;
    }

    public void setB5r22asab(Integer b5r22asab) {
        this.b5r22asab = b5r22asab;
    }

    public Integer getB5r22amin() {
        return b5r22amin;
    }

    public void setB5r22amin(Integer b5r22amin) {
        this.b5r22amin = b5r22amin;
    }

    public Integer getB5r22ajumlah() {
        return b5r22ajumlah;
    }

    public void setB5r22ajumlah(Integer b5r22ajumlah) {
        this.b5r22ajumlah = b5r22ajumlah;
    }

    public Integer getB5r22b() {
        return b5r22b;
    }

    public void setB5r22b(Integer b5r22b) {
        this.b5r22b = b5r22b;
    }

    public Integer getB5r23() {
        return b5r23;
    }

    public void setB5r23(Integer b5r23) {
        this.b5r23 = b5r23;
    }

    public Integer getB5r24() {
        return b5r24;
    }

    public void setB5r24(Integer b5r24) {
        this.b5r24 = b5r24;
    }

    public Integer getB5r25() {
        return b5r25;
    }

    public void setB5r25(Integer b5r25) {
        this.b5r25 = b5r25;
    }

    public Integer getB5r26uang() {
        return b5r26uang;
    }

    public void setB5r26uang(Integer b5r26uang) {
        this.b5r26uang = b5r26uang;
    }

    public Integer getB5r26barang() {
        return b5r26barang;
    }

    public void setB5r26barang(Integer b5r26barang) {
        this.b5r26barang = b5r26barang;
    }

    public Integer getB5r27() {
        return b5r27;
    }

    public void setB5r27(Integer b5r27) {
        this.b5r27 = b5r27;
    }

    public Integer getB5r28a() {
        return b5r28a;
    }

    public void setB5r28a(Integer b5r28a) {
        this.b5r28a = b5r28a;
    }

    public Integer getB5r28b() {
        return b5r28b;
    }

    public void setB5r28b(Integer b5r28b) {
        this.b5r28b = b5r28b;
    }

    public Integer getB5r28c() {
        return b5r28c;
    }

    public void setB5r28c(Integer b5r28c) {
        this.b5r28c = b5r28c;
    }

    public Integer getB5r28d() {
        return b5r28d;
    }

    public void setB5r28d(Integer b5r28d) {
        this.b5r28d = b5r28d;
    }

    public Integer getB5r28e() {
        return b5r28e;
    }

    public void setB5r28e(Integer b5r28e) {
        this.b5r28e = b5r28e;
    }

    public Integer getB5r28f() {
        return b5r28f;
    }

    public void setB5r28f(Integer b5r28f) {
        this.b5r28f = b5r28f;
    }

    public Integer getB5r28g() {
        return b5r28g;
    }

    public void setB5r28g(Integer b5r28g) {
        this.b5r28g = b5r28g;
    }

    public Integer getB5r29() {
        return b5r29;
    }

    public void setB5r29(Integer b5r29) {
        this.b5r29 = b5r29;
    }

    public Integer getB5r30() {
        return b5r30;
    }

    public void setB5r30(Integer b5r30) {
        this.b5r30 = b5r30;
    }

    public Integer getB5r31() {
        return b5r31;
    }

    public void setB5r31(Integer b5r31) {
        this.b5r31 = b5r31;
    }

    public String getB5r31lainnya() {
        return b5r31lainnya;
    }

    public void setB5r31lainnya(String b5r31lainnya) {
        this.b5r31lainnya = b5r31lainnya;
    }

    public Integer getB5r32() {
        return b5r32;
    }

    public void setB5r32(Integer b5r32) {
        this.b5r32 = b5r32;
    }

    public String getB5r32lainnya() {
        return b5r32lainnya;
    }

    public void setB5r32lainnya(String b5r32lainnya) {
        this.b5r32lainnya = b5r32lainnya;
    }

    public String getB5r33aprov() {
        return b5r33aprov;
    }

    public void setB5r33aprov(String b5r33aprov) {
        this.b5r33aprov = b5r33aprov;
    }

    public String getB5r33akab() {
        return b5r33akab;
    }

    public void setB5r33akab(String b5r33akab) {
        this.b5r33akab = b5r33akab;
    }

    public Integer getB5r33b() {
        return b5r33b;
    }

    public void setB5r33b(Integer b5r33b) {
        this.b5r33b = b5r33b;
    }

    public Integer getB5r33c() {
        return b5r33c;
    }

    public void setB5r33c(Integer b5r33c) {
        this.b5r33c = b5r33c;
    }

    public Integer getB5r33d() {
        return b5r33d;
    }

    public void setB5r33d(Integer b5r33d) {
        this.b5r33d = b5r33d;
    }

    public Integer getB5r33e() {
        return b5r33e;
    }

    public void setB5r33e(Integer b5r33e) {
        this.b5r33e = b5r33e;
    }

    public Integer getB5r34() {
        return b5r34;
    }

    public void setB5r34(Integer b5r34) {
        this.b5r34 = b5r34;
    }

    public Integer getB5r35() {
        return b5r35;
    }

    public void setB5r35(Integer b5r35) {
        this.b5r35 = b5r35;
    }

    public Integer getB5r36() {
        return b5r36;
    }

    public void setB5r36(Integer b5r36) {
        this.b5r36 = b5r36;
    }

    public Integer getB5r37asen() {
        return b5r37asen;
    }

    public void setB5r37asen(Integer b5r37asen) {
        this.b5r37asen = b5r37asen;
    }

    public Integer getB5r37asel() {
        return b5r37asel;
    }

    public void setB5r37asel(Integer b5r37asel) {
        this.b5r37asel = b5r37asel;
    }

    public Integer getB5r37arab() {
        return b5r37arab;
    }

    public void setB5r37arab(Integer b5r37arab) {
        this.b5r37arab = b5r37arab;
    }

    public Integer getB5r37akam() {
        return b5r37akam;
    }

    public void setB5r37akam(Integer b5r37akam) {
        this.b5r37akam = b5r37akam;
    }

    public Integer getB5r37ajum() {
        return b5r37ajum;
    }

    public void setB5r37ajum(Integer b5r37ajum) {
        this.b5r37ajum = b5r37ajum;
    }

    public Integer getB5r37asab() {
        return b5r37asab;
    }

    public void setB5r37asab(Integer b5r37asab) {
        this.b5r37asab = b5r37asab;
    }

    public Integer getB5r37amin() {
        return b5r37amin;
    }

    public void setB5r37amin(Integer b5r37amin) {
        this.b5r37amin = b5r37amin;
    }

    public Integer getB5r37ajumlah() {
        return b5r37ajumlah;
    }

    public void setB5r37ajumlah(Integer b5r37ajumlah) {
        this.b5r37ajumlah = b5r37ajumlah;
    }

    public Integer getB5r37b() {
        return b5r37b;
    }

    public void setB5r37b(Integer b5r37b) {
        this.b5r37b = b5r37b;
    }

    public Integer getB5r38a() {
        return b5r38a;
    }

    public void setB5r38a(Integer b5r38a) {
        this.b5r38a = b5r38a;
    }

    public Integer getB5r38b() {
        return b5r38b;
    }

    public void setB5r38b(Integer b5r38b) {
        this.b5r38b = b5r38b;
    }

    public Integer getB5r39() {
        return b5r39;
    }

    public void setB5r39(Integer b5r39) {
        this.b5r39 = b5r39;
    }

    public Integer getB5r40() {
        return b5r40;
    }

    public void setB5r40(Integer b5r40) {
        this.b5r40 = b5r40;
    }

    public Integer getB5r41() {
        return b5r41;
    }

    public void setB5r41(Integer b5r41) {
        this.b5r41 = b5r41;
    }

    public Integer getB5r42() {
        return b5r42;
    }

    public void setB5r42(Integer b5r42) {
        this.b5r42 = b5r42;
    }

    public String getB5r42lainnya() {
        return b5r42lainnya;
    }

    public void setB5r42lainnya(String b5r42lainnya) {
        this.b5r42lainnya = b5r42lainnya;
    }

    public Integer getB5r43() {
        return b5r43;
    }

    public void setB5r43(Integer b5r43) {
        this.b5r43 = b5r43;
    }

    public Integer getB5r44() {
        return b5r44;
    }

    public void setB5r44(Integer b5r44) {
        this.b5r44 = b5r44;
    }

    public Integer getB5r45() {
        return b5r45;
    }

    public void setB5r45(Integer b5r45) {
        this.b5r45 = b5r45;
    }

    public String getB5r45negara() {
        return b5r45negara;
    }

    public void setB5r45negara(String b5r45negara) {
        this.b5r45negara = b5r45negara;
    }

    public Integer getB5r46() {
        return b5r46;
    }

    public void setB5r46(Integer b5r46) {
        this.b5r46 = b5r46;
    }

    public Integer getB5r47a() {
        return b5r47a;
    }

    public void setB5r47a(Integer b5r47a) {
        this.b5r47a = b5r47a;
    }

    public Integer getB5r47b() {
        return b5r47b;
    }

    public void setB5r47b(Integer b5r47b) {
        this.b5r47b = b5r47b;
    }

    public Integer getB5r47c() {
        return b5r47c;
    }

    public void setB5r47c(Integer b5r47c) {
        this.b5r47c = b5r47c;
    }

    public Integer getB5r47d() {
        return b5r47d;
    }

    public void setB5r47d(Integer b5r47d) {
        this.b5r47d = b5r47d;
    }

    public Integer getB5r48a() {
        return b5r48a;
    }

    public void setB5r48a(Integer b5r48a) {
        this.b5r48a = b5r48a;
    }

    public Integer getB5r48b() {
        return b5r48b;
    }

    public void setB5r48b(Integer b5r48b) {
        this.b5r48b = b5r48b;
    }

    public Integer getB5r49() {
        return b5r49;
    }

    public void setB5r49(Integer b5r49) {
        this.b5r49 = b5r49;
    }

    public Integer getB5r50() {
        return b5r50;
    }

    public void setB5r50(Integer b5r50) {
        this.b5r50 = b5r50;
    }

    public Integer getB5r51() {
        return b5r51;
    }

    public void setB5r51(Integer b5r51) {
        this.b5r51 = b5r51;
    }

    public Integer getB5r52() {
        return b5r52;
    }

    public void setB5r52(Integer b5r52) {
        this.b5r52 = b5r52;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}
