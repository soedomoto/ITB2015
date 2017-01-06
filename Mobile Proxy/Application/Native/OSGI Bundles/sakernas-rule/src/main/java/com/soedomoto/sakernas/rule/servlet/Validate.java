package com.soedomoto.sakernas.rule.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soedomoto.sakernas.model.ART;
import com.soedomoto.sakernas.model.ErrorHandler;
import com.soedomoto.sakernas.model.Ruta;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by soedomoto on 05/11/16.
 */
public class Validate extends HttpServlet {
    public static String PATH = "/validate";
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String strFormAK = IOUtils.toString(req.getInputStream(), Charset.defaultCharset());
        Ruta ruta = gson.fromJson(strFormAK, Ruta.class);

        checkErrorRuta(ruta);
        for(ART art : ruta.getArts()) {
            checkErrorART(art);
        }

        resp.getWriter().println(strFormAK);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private ErrorHandler checkErrorRuta(Ruta ruta) {
        ErrorHandler error = new ErrorHandler(ruta.getID());

        return error;
    }

    private ErrorHandler checkErrorART(ART art) {
        ErrorHandler error = new ErrorHandler(art.getID());

        if(! art.getB4r2().equalsIgnoreCase("") && art.getB4r2().length() < 3) {
            error.addError("b4r2",
                    "Rincian b4_k2 kurang dari 3 digit",
                    "Rincian b4_k2 min 3 digit");
        }

        if(art.getB4r1() == 1 && art.getB4r3() != 1) {
            error.addError("b4r3",
                    "Rincian b4_k1 berkode 1 tetapi b4_k3 tidak berkode 1",
                    "Rincian b4_k1 berkode 1 maka b4_k3 berkode 1");
        }

        if(art.getB4r1() != null && art.getB4r1() != 1 && art.getB4r3() == 1) {
            error.addError("b4r3",
                    "Rincian b4_k1 tidak terisi 1 tetapi b4_k3 terisi 1",
                    "Rincian b4_k1 tidak terisi 1 maka b4_k3 tidak boleh terisi 1");
        }

        // b4_k6<10 AND b4_k7<>blank
        if(art.getB4r6() < 10 && art.getB4r7() != null) {
            error.addError("b4r7",
                    "Rincian b4_k6 terisi kurang dari 10 th tetapi b4_k7 ada isian",
                    "Jika rincian b4_k6 terisi kurang dari 10 th maka b4_k7 harus kosong");
        }

        // b4_k6>=10 AND b4_k7=blank
        if(art.getB4r6() >= 10 && art.getB4r7() != null) {
            error.addError("b4r7",
                    "Rincian b4_k6 terisi lebih dari atau sama dengan 10 th tetapi b4_k7 tidak ada isian",
                    "Jika rincian b4_k6 terisi lebih dari atau sama dengan 10 th maka b4_k7 harus ada isian");
        }

        // b4_k3=2 AND b4_k7<>2
        if(art.getB4r3() == 2 && art.getB4r7() != 2) {
            error.addError("b4r7",
                    "Rincian b4_k3 berkode 2 tetapi b4_k7 tidak berkode 2",
                    "Jika rincian b4_k3 berkode 2 maka b4_k7 harus berkode 2");
        }

        // b4_k3=6 AND b4_k7=1
        if(art.getB4r3() == 6 && art.getB4r7() == 1) {
            error.addError("b4r7",
                    "Rincian b4_k3 berkode 6 tetapi b4_k7 berkode 1",
                    "Jika rincian b4_k3 berkode 6 maka b4_k7 tidak boleh berkode 1");
        }

        // b4_k7=2 AND b4_k6<10
        if(art.getB4r7() == 2 && art.getB4r6() < 10) {
            error.addError("b4r7",
                    "Rincian b4_k7 berkode 2 tetapi b4_k6 terisi kurang dari 10 th",
                    "Rincian b4_k7 berkode 2 maka b4_k6 terisi minimal 10 th");
        }

        // b4_k6<10 AND b4_k8<>blank
        if(art.getB4r6() < 10 && art.getB4r8() != null) {
            error.addError("b4r8",
                    "Rincian b4_k6 terisi kurang dari 10 th tetapi b4_k8 ada isian",
                    "Jika rincian b4_k6 terisi kurang dari 10 th maka b4_k8 harus kosong");
        }

        // b4_k6>=10 AND b4_k8=blank
        if(art.getB4r6() >= 10 && art.getB4r8() == null) {
            error.addError("b4r8",
                    "Rincian b4_k6 terisi lebih dari atau sama dengan 10 th tetapi b4_k8 tidak ada isian",
                    "Jika rincian b4_k6 terisi lebih dari atau sama dengan 10 th maka b4_k8 harus ada isian");
        }

        // b4_k6>64 AND b4_k8=2
        if(art.getB4r6() > 64 && art.getB4r8() == 2) {
            error.addError("b4r8",
                    "Rincian b4_k6 terisi lebih dari 64 tetapi b4_k8 berkode 2",
                    "Rincian b4_k6 terisi lebih dari 64 maka b4_k8 tidak boleh berkode 2");
        }

        // b4_k6>=10 AND b5_r1a=blank
        if(art.getB4r6() >= 10 && art.getB5r1a() == null) {
            error.addError("b5r1a",
                    "Rincian b4_k6 terisi 10 th ke atas tetapi b5_r1a tidak ada isian",
                    "Jika rincian b4_k6 terisi 10 th ke atas maka b5_r1a harus ada isian");
        }

        // b4_k6<10 AND b5_r1a<>blank
        if(art.getB4r6() < 10 && art.getB5r1a() != null) {
            error.addError("b5r1a",
                    "Rincian b4_k6 terisi kurang dari 10 th tetapi b5_r1a ada isian",
                    "Jika rincian b4_k6 terisi kurang dari 10 th maka b5_r1a harus kosong");
        }

        // b4_k8=1 AND b5_r1a<>1
        if(art.getB4r8() == 1 && art.getB5r1a() != 1) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 1 tetapi rincian b5_r1a tidak berkode 1",
                    "Jika rincian b4_k8 berkode 1 maka rincian b5_r1a harus berkode 1");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6<21 AND b5_r1a>=15
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() < 21 && art.getB5r1a() >= 15) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 21 th tetapi b5_r1a berkode 15 atau 16",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 21 th maka b5_r1a tidak boleh berkode 15 atau 16");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6<20 AND b5_r1a=14
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() < 20 && art.getB5r1a() == 14) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 20 th tetapi b5_r1a berkode 14",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 20 th maka b5_r1a tidak boleh berkode 14");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6<17 AND b5_r1a=12
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() < 17 && art.getB5r1a() == 12) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 17 th tetapi b5_r1a berkode 12",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 17 th maka b5_r1a tidak boleh berkode 12");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6<16 AND b5_r1a=11
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() < 16 && art.getB5r1a() == 12) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 16 th tetapi b5_r1a berkode 11",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 16 th maka b5_r1a tidak boleh berkode 11");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6<16 AND b5_r1a=10
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() < 16 && art.getB5r1a() == 10) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 16 th tetapi b5_r1a berkode 10",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 16 th maka b5_r1a tidak boleh berkode 10");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6<13 AND b5_r1a=7
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() < 13 && art.getB5r1a() == 7) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 13 th tetapi b5_r1a berkode 7",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 13 th maka b5_r1a tidak boleh berkode 7");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6<10 AND b5_r1a=4
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() < 10 && art.getB5r1a() == 4) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 10 th tetapi b5_r1a berkode 4",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 10 th maka b5_r1a tidak boleh berkode 4");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6>17 AND b5_r1a=1
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() > 17 && art.getB5r1a() == 1) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 lebih dari 17 th tetapi b5_r1a berkode 1",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 lebih dari 17 th maka b5_r1a tidak boleh berkode 1");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6>18 AND (b5_r1a=1 OR b5_r1a=4)
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() > 18 && (art.getB5r1a() == 1 || art.getB5r1a() == 4)) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 lebih dari 18 th tetapi b5_r1a berkode 1 atau 4",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 lebih dari 18 th maka b5_r1a tidak boleh berkode 1 atau 4");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6<13 AND b5_r1a=5
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() > 13 && art.getB5r1a() == 5) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 13 th tetapi b5_r1a berkode 5",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 13 th maka b5_r1a tidak boleh berkode 5");
        }

        // b4_k8=2 AND b4_k6<>blank AND b4_k6<16 AND b5_r1a=8
        if(art.getB4r8() == 2 && art.getB4r6() != null && art.getB4r6() < 16 && art.getB5r1a() == 8) {
            error.addError("b5r1a",
                    "Rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 16 th tetapi b5_r1a berkode 8",
                    "Jika rincian b4_k8 berkode 2 dan rincian b4_k6 kurang dari 16 th maka b5_r1a tidak boleh berkode 8");
        }


        return error;
    }
}
