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


public class QBlockVC extends Fragment {

    public static QBlockVC newInstance() {
        QBlockVC blockI = new QBlockVC();
        return blockI;
    }

    public QBlockVC() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_5c, container, false);
        return view;
    }

    public String getB5r11() {
        RadioGroup b5r11 = (RadioGroup) getView().findViewById(R.id.b5r11);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r11.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r12() {
        RadioGroup b5r12 = (RadioGroup) getView().findViewById(R.id.b5r12);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r12.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r13() {
        QEditText b5r13 = (QEditText) getView().findViewById(R.id.b5r13);
        return b5r13.getValue();
    }

    public String getB5r14() {
        RadioGroup b5r14 = (RadioGroup) getView().findViewById(R.id.b5r14);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r14.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r14lainnya() {
        QEditText b5r14lainnya = (QEditText) getView().findViewById(R.id.b5r14lainnya);
        return b5r14lainnya.getValue();
    }

    public String getB5r15a() {
        RadioGroup b5r15a = (RadioGroup) getView().findViewById(R.id.b5r15a);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r15a.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r15b() {
        RadioGroup b5r15b = (RadioGroup) getView().findViewById(R.id.b5r15b);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r15b.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r15c() {
        RadioGroup b5r15c = (RadioGroup) getView().findViewById(R.id.b5r15c);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r15c.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r15d() {
        RadioGroup b5r15d = (RadioGroup) getView().findViewById(R.id.b5r15d);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r15d.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r15e() {
        RadioGroup b5r15e = (RadioGroup) getView().findViewById(R.id.b5r15e);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r15e.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r15f() {
        RadioGroup b5r15f = (RadioGroup) getView().findViewById(R.id.b5r15f);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r15f.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r15g() {
        RadioGroup b5r15g = (RadioGroup) getView().findViewById(R.id.b5r15g);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r15g.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r15h() {
        RadioGroup b5r15h = (RadioGroup) getView().findViewById(R.id.b5r15h);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r15h.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r15i() {
        RadioGroup b5r15i = (RadioGroup) getView().findViewById(R.id.b5r15i);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r15i.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r15ilainnya() {
        QEditText b5r15ilainnya = (QEditText) getView().findViewById(R.id.b5r15ilainnya);
        return b5r15ilainnya.getValue();
    }

    public String getB5r16a() {
        RadioGroup b5r16a = (RadioGroup) getView().findViewById(R.id.b5r16a);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r16a.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r16alainnya() {
        QEditText b5r16alainnya = (QEditText) getView().findViewById(R.id.b5r16alainnya);
        return b5r16alainnya.getValue();
    }

    public String getB5r16b() {
        RadioGroup b5r16b = (RadioGroup) getView().findViewById(R.id.b5r16b);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r16b.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r17a() {
        RadioGroup b5r17a = (RadioGroup) getView().findViewById(R.id.b5r17a);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r17a.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r17b() {
        RadioGroup b5r17b = (RadioGroup) getView().findViewById(R.id.b5r17b);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r17b.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r18() {
        RadioGroup b5r18 = (RadioGroup) getView().findViewById(R.id.b5r18);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r18.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }
}
