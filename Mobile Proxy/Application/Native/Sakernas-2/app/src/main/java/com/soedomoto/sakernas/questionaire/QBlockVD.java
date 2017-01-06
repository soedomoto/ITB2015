package com.soedomoto.sakernas.questionaire;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.soedomoto.sakernas.R;
import com.soedomoto.sakernas.widget.QEditText;
import com.soedomoto.sakernas.widget.QRadioButton;


public class QBlockVD extends Fragment {

    public static QBlockVD newInstance() {
        QBlockVD blockI = new QBlockVD();
        return blockI;
    }

    public QBlockVD() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_5d, container, false);
        return view;
    }

    public String getB5r19() {
        QEditText b5r19 = (QEditText) getView().findViewById(R.id.b5r19);
        return b5r19.getValue();
    }

    public String getB5r20() {
        QEditText b5r20 = (QEditText) getView().findViewById(R.id.b5r20);
        return b5r20.getValue();
    }

    public String getB5r21a() {
        RadioGroup b5r21a = (RadioGroup) getView().findViewById(R.id.b5r21a);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r21a.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r21atahun() {
        QEditText b5r21atahun = (QEditText) getView().findViewById(R.id.b5r21atahun);
        return b5r21atahun.getValue();
    }

    public String getB5r21abulan() {
        QEditText b5r21abulan = (QEditText) getView().findViewById(R.id.b5r21abulan);
        return b5r21abulan.getValue();
    }

    public String getB5r21b() {
        QEditText b5r21b = (QEditText) getView().findViewById(R.id.b5r21b);
        return b5r21b.getValue();
    }

    public String getB5r22asen() {
        QEditText b5r22asen = (QEditText) getView().findViewById(R.id.b5r22asen);
        return b5r22asen.getValue();
    }

    public String getB5r22asel() {
        QEditText b5r22asel = (QEditText) getView().findViewById(R.id.b5r22asel);
        return b5r22asel.getValue();
    }

    public String getB5r22arab() {
        QEditText b5r22arab = (QEditText) getView().findViewById(R.id.b5r22arab);
        return b5r22arab.getValue();
    }

    public String getB5r22akam() {
        QEditText b5r22akam = (QEditText) getView().findViewById(R.id.b5r22akam);
        return b5r22akam.getValue();
    }

    public String getB5r22ajum() {
        QEditText b5r22ajum = (QEditText) getView().findViewById(R.id.b5r22ajum);
        return b5r22ajum.getValue();
    }

    public String getB5r22asab() {
        QEditText b5r22asab = (QEditText) getView().findViewById(R.id.b5r22asab);
        return b5r22asab.getValue();
    }

    public String getB5r22amin() {
        QEditText b5r22amin = (QEditText) getView().findViewById(R.id.b5r22amin);
        return b5r22amin.getValue();
    }

    public String getB5r22b() {
        QEditText b5r22b = (QEditText) getView().findViewById(R.id.b5r22b);
        return b5r22b.getValue();
    }

    public String getB5r23() {
        RadioGroup b5r23 = (RadioGroup) getView().findViewById(R.id.b5r23);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r23.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r24() {
        RadioGroup b5r24 = (RadioGroup) getView().findViewById(R.id.b5r24);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r24.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r25() {
        RadioGroup b5r25 = (RadioGroup) getView().findViewById(R.id.b5r25);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r25.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r26uang() {
        QEditText b5r26uang = (QEditText) getView().findViewById(R.id.b5r26uang);
        return b5r26uang.getValue();
    }

    public String getB5r26barang() {
        QEditText b5r26barang = (QEditText) getView().findViewById(R.id.b5r26barang);
        return b5r26barang.getValue();
    }

    public String getB5r27() {
        RadioGroup b5r27 = (RadioGroup) getView().findViewById(R.id.b5r27);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r27.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r28a() {
        RadioGroup b5r28a = (RadioGroup) getView().findViewById(R.id.b5r28a);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r28a.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r28b() {
        RadioGroup b5r28b = (RadioGroup) getView().findViewById(R.id.b5r28b);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r28b.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r28c() {
        RadioGroup b5r28c = (RadioGroup) getView().findViewById(R.id.b5r28c);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r28c.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r28d() {
        RadioGroup b5r28d = (RadioGroup) getView().findViewById(R.id.b5r28d);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r28d.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r28e() {
        RadioGroup b5r28e = (RadioGroup) getView().findViewById(R.id.b5r28e);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r28e.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r28f() {
        RadioGroup b5r28f = (RadioGroup) getView().findViewById(R.id.b5r28f);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r28f.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r28g() {
        RadioGroup b5r28g = (RadioGroup) getView().findViewById(R.id.b5r28g);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r28g.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r29() {
        RadioGroup b5r29 = (RadioGroup) getView().findViewById(R.id.b5r29);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r29.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r30() {
        RadioGroup b5r30 = (RadioGroup) getView().findViewById(R.id.b5r30);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r30.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r31() {
        RadioGroup b5r31 = (RadioGroup) getView().findViewById(R.id.b5r31);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r31.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r31lainnya() {
        QEditText b5r31lainnya = (QEditText) getView().findViewById(R.id.b5r31lainnya);
        return b5r31lainnya.getValue();
    }

    public String getB5r32() {
        RadioGroup b5r32 = (RadioGroup) getView().findViewById(R.id.b5r32);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r32.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r32lainnya() {
        QEditText b5r32lainnya = (QEditText) getView().findViewById(R.id.b5r32lainnya);
        return b5r32lainnya.getValue();
    }

    public String getB5r33aprov() {
        QEditText b5r33aprov = (QEditText) getView().findViewById(R.id.b5r33aprov);
        return b5r33aprov.getValue();
    }

    public String getB5r33akab() {
        QEditText b5r33akab = (QEditText) getView().findViewById(R.id.b5r33akab);
        return b5r33akab.getValue();
    }

    public String getB5r33b() {
        RadioGroup b5r33b = (RadioGroup) getView().findViewById(R.id.b5r33b);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r33b.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r33c() {
        RadioGroup b5r33c = (RadioGroup) getView().findViewById(R.id.b5r33c);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r33c.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r33d() {
        RadioGroup b5r33d = (RadioGroup) getView().findViewById(R.id.b5r33d);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r33d.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r33e() {
        RadioGroup b5r33e = (RadioGroup) getView().findViewById(R.id.b5r33e);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r33e.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }
}
