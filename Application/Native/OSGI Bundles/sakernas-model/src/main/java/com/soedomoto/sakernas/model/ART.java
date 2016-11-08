package com.soedomoto.sakernas.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by soedomoto on 07/11/16.
 */

@DatabaseTable(tableName = "sak_art")
public class ART {
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
    private Integer b5r1d1;
    @DatabaseField
    private Integer b5r1d2;

    //  BLOK V.B
    @DatabaseField
    private Integer b5r2a1;
    @DatabaseField
    private Integer b5r2a2;
    @DatabaseField
    private Integer b5r2a3;
    @DatabaseField
    private Integer b5r2a4;
    @DatabaseField
    private Integer b5r2b;
    @DatabaseField
    private Integer b5r3;
    @DatabaseField
    private Integer b5r4;
    @DatabaseField
    private Integer b5r5;
    @DatabaseField
    private Integer b5r6;
    @DatabaseField
    private String b5r6l;
    @DatabaseField
    private Integer b5r7;
    @DatabaseField
    private Integer b5r8a;
    @DatabaseField
    private Integer b5r8b;
    @DatabaseField
    private Integer b5r8b1;
    @DatabaseField
    private Integer b5r8b2;
    @DatabaseField
    private Integer b5r8b3;
    @DatabaseField
    private Integer b5r8b4;
    @DatabaseField
    private Integer b5r8b5;
    @DatabaseField
    private Integer b5r8b6;
    @DatabaseField
    private Integer b5r8b7;
    @DatabaseField
    private Integer b5r8bj;

    //  BLOK V.C
    @DatabaseField
    private Integer b5r9;
    @DatabaseField
    private Integer b5r10;
    @DatabaseField
    private Integer b5r11;
    @DatabaseField
    private Integer b5r12;
    @DatabaseField
    private Integer b5r13a;
    @DatabaseField
    private Integer b5r13b;
    @DatabaseField
    private Integer b5r14y;
    @DatabaseField
    private Integer b5r14m;
    @DatabaseField
    private String b5r15a1;
    @DatabaseField
    private String b5r15a2;
    @DatabaseField
    private Integer b5r15b;
    @DatabaseField
    private Integer b5r15c;
    @DatabaseField
    private Integer b5r15d;
    @DatabaseField
    private Integer b5r15e;
    @DatabaseField
    private Integer b5r16a;
    @DatabaseField
    private Integer b5r16b;

    //  BLOK V.D
    @DatabaseField
    private Integer b5r17;
    @DatabaseField
    private Integer b5r18;

    //  BLOK V.E
    @DatabaseField
    private Integer b5r19;
    @DatabaseField
    private String b5r19l;
    @DatabaseField
    private Integer b5r20o1;
    @DatabaseField
    private Integer b5r20o2;
    @DatabaseField
    private Integer b5r20o3;
    @DatabaseField
    private Integer b5r20o4;
    @DatabaseField
    private Integer b5r20o5;
    @DatabaseField
    private Integer b5r20o6;
    @DatabaseField
    private Integer b5r20o7;
    @DatabaseField
    private Integer b5r20o8;
    @DatabaseField
    private String b5r20o8l;
    @DatabaseField
    private Integer b5r21y;
    @DatabaseField
    private Integer b5r21m;
    @DatabaseField
    private Integer b5r22;

    //  BLOK V.F
    @DatabaseField
    private Integer b5r23;
    @DatabaseField
    private Integer b5r24;
    @DatabaseField
    private Integer b5r25;
    @DatabaseField
    private String b5r25l;
    @DatabaseField
    private Integer b5r26;
    @DatabaseField
    private Integer b5r27;


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

    public Integer getB5r1d1() {
        return b5r1d1;
    }

    public void setB5r1d1(Integer b5r1d1) {
        this.b5r1d1 = b5r1d1;
    }

    public Integer getB5r1d2() {
        return b5r1d2;
    }

    public void setB5r1d2(Integer b5r1d2) {
        this.b5r1d2 = b5r1d2;
    }

    public Integer getB5r2a1() {
        return b5r2a1;
    }

    public void setB5r2a1(Integer b5r2a1) {
        this.b5r2a1 = b5r2a1;
    }

    public Integer getB5r2a2() {
        return b5r2a2;
    }

    public void setB5r2a2(Integer b5r2a2) {
        this.b5r2a2 = b5r2a2;
    }

    public Integer getB5r2a3() {
        return b5r2a3;
    }

    public void setB5r2a3(Integer b5r2a3) {
        this.b5r2a3 = b5r2a3;
    }

    public Integer getB5r2a4() {
        return b5r2a4;
    }

    public void setB5r2a4(Integer b5r2a4) {
        this.b5r2a4 = b5r2a4;
    }

    public Integer getB5r2b() {
        return b5r2b;
    }

    public void setB5r2b(Integer b5r2b) {
        this.b5r2b = b5r2b;
    }

    public Integer getB5r3() {
        return b5r3;
    }

    public void setB5r3(Integer b5r3) {
        this.b5r3 = b5r3;
    }

    public Integer getB5r4() {
        return b5r4;
    }

    public void setB5r4(Integer b5r4) {
        this.b5r4 = b5r4;
    }

    public Integer getB5r5() {
        return b5r5;
    }

    public void setB5r5(Integer b5r5) {
        this.b5r5 = b5r5;
    }

    public Integer getB5r6() {
        return b5r6;
    }

    public void setB5r6(Integer b5r6) {
        this.b5r6 = b5r6;
    }

    public String getB5r6l() {
        return b5r6l;
    }

    public void setB5r6l(String b5r6l) {
        this.b5r6l = b5r6l;
    }

    public Integer getB5r7() {
        return b5r7;
    }

    public void setB5r7(Integer b5r7) {
        this.b5r7 = b5r7;
    }

    public Integer getB5r8a() {
        return b5r8a;
    }

    public void setB5r8a(Integer b5r8a) {
        this.b5r8a = b5r8a;
    }

    public Integer getB5r8b() {
        return b5r8b;
    }

    public void setB5r8b(Integer b5r8b) {
        this.b5r8b = b5r8b;
    }

    public Integer getB5r8b1() {
        return b5r8b1;
    }

    public void setB5r8b1(Integer b5r8b1) {
        this.b5r8b1 = b5r8b1;
    }

    public Integer getB5r8b2() {
        return b5r8b2;
    }

    public void setB5r8b2(Integer b5r8b2) {
        this.b5r8b2 = b5r8b2;
    }

    public Integer getB5r8b3() {
        return b5r8b3;
    }

    public void setB5r8b3(Integer b5r8b3) {
        this.b5r8b3 = b5r8b3;
    }

    public Integer getB5r8b4() {
        return b5r8b4;
    }

    public void setB5r8b4(Integer b5r8b4) {
        this.b5r8b4 = b5r8b4;
    }

    public Integer getB5r8b5() {
        return b5r8b5;
    }

    public void setB5r8b5(Integer b5r8b5) {
        this.b5r8b5 = b5r8b5;
    }

    public Integer getB5r8b6() {
        return b5r8b6;
    }

    public void setB5r8b6(Integer b5r8b6) {
        this.b5r8b6 = b5r8b6;
    }

    public Integer getB5r8b7() {
        return b5r8b7;
    }

    public void setB5r8b7(Integer b5r8b7) {
        this.b5r8b7 = b5r8b7;
    }

    public Integer getB5r8bj() {
        return b5r8bj;
    }

    public void setB5r8bj(Integer b5r8bj) {
        this.b5r8bj = b5r8bj;
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

    public Integer getB5r13a() {
        return b5r13a;
    }

    public void setB5r13a(Integer b5r13a) {
        this.b5r13a = b5r13a;
    }

    public Integer getB5r13b() {
        return b5r13b;
    }

    public void setB5r13b(Integer b5r13b) {
        this.b5r13b = b5r13b;
    }

    public Integer getB5r14y() {
        return b5r14y;
    }

    public void setB5r14y(Integer b5r14y) {
        this.b5r14y = b5r14y;
    }

    public Integer getB5r14m() {
        return b5r14m;
    }

    public void setB5r14m(Integer b5r14m) {
        this.b5r14m = b5r14m;
    }

    public String getB5r15a1() {
        return b5r15a1;
    }

    public void setB5r15a1(String b5r15a1) {
        this.b5r15a1 = b5r15a1;
    }

    public String getB5r15a2() {
        return b5r15a2;
    }

    public void setB5r15a2(String b5r15a2) {
        this.b5r15a2 = b5r15a2;
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

    public Integer getB5r16a() {
        return b5r16a;
    }

    public void setB5r16a(Integer b5r16a) {
        this.b5r16a = b5r16a;
    }

    public Integer getB5r16b() {
        return b5r16b;
    }

    public void setB5r16b(Integer b5r16b) {
        this.b5r16b = b5r16b;
    }

    public Integer getB5r17() {
        return b5r17;
    }

    public void setB5r17(Integer b5r17) {
        this.b5r17 = b5r17;
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

    public String getB5r19l() {
        return b5r19l;
    }

    public void setB5r19l(String b5r19l) {
        this.b5r19l = b5r19l;
    }

    public Integer getB5r20o1() {
        return b5r20o1;
    }

    public void setB5r20o1(Integer b5r20o1) {
        this.b5r20o1 = b5r20o1;
    }

    public Integer getB5r20o2() {
        return b5r20o2;
    }

    public void setB5r20o2(Integer b5r20o2) {
        this.b5r20o2 = b5r20o2;
    }

    public Integer getB5r20o3() {
        return b5r20o3;
    }

    public void setB5r20o3(Integer b5r20o3) {
        this.b5r20o3 = b5r20o3;
    }

    public Integer getB5r20o4() {
        return b5r20o4;
    }

    public void setB5r20o4(Integer b5r20o4) {
        this.b5r20o4 = b5r20o4;
    }

    public Integer getB5r20o5() {
        return b5r20o5;
    }

    public void setB5r20o5(Integer b5r20o5) {
        this.b5r20o5 = b5r20o5;
    }

    public Integer getB5r20o6() {
        return b5r20o6;
    }

    public void setB5r20o6(Integer b5r20o6) {
        this.b5r20o6 = b5r20o6;
    }

    public Integer getB5r20o7() {
        return b5r20o7;
    }

    public void setB5r20o7(Integer b5r20o7) {
        this.b5r20o7 = b5r20o7;
    }

    public Integer getB5r20o8() {
        return b5r20o8;
    }

    public void setB5r20o8(Integer b5r20o8) {
        this.b5r20o8 = b5r20o8;
    }

    public String getB5r20o8l() {
        return b5r20o8l;
    }

    public void setB5r20o8l(String b5r20o8l) {
        this.b5r20o8l = b5r20o8l;
    }

    public Integer getB5r21y() {
        return b5r21y;
    }

    public void setB5r21y(Integer b5r21y) {
        this.b5r21y = b5r21y;
    }

    public Integer getB5r21m() {
        return b5r21m;
    }

    public void setB5r21m(Integer b5r21m) {
        this.b5r21m = b5r21m;
    }

    public Integer getB5r22() {
        return b5r22;
    }

    public void setB5r22(Integer b5r22) {
        this.b5r22 = b5r22;
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

    public String getB5r25l() {
        return b5r25l;
    }

    public void setB5r25l(String b5r25l) {
        this.b5r25l = b5r25l;
    }

    public Integer getB5r26() {
        return b5r26;
    }

    public void setB5r26(Integer b5r26) {
        this.b5r26 = b5r26;
    }

    public Integer getB5r27() {
        return b5r27;
    }

    public void setB5r27(Integer b5r27) {
        this.b5r27 = b5r27;
    }
}
