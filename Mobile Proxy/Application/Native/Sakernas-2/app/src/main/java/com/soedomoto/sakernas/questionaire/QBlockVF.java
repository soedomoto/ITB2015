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


public class QBlockVF extends Fragment {

    public static QBlockVF newInstance() {
        QBlockVF blockI = new QBlockVF();
        return blockI;
    }

    public QBlockVF() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_5f, container, false);
        return view;
    }

    public String getB5r37asen() {
        QEditText b5r37asen = (QEditText) getView().findViewById(R.id.b5r37asen);
        return b5r37asen.getValue();
    }

    public String getB5r37asel() {
        QEditText b5r37asel = (QEditText) getView().findViewById(R.id.b5r37asel);
        return b5r37asel.getValue();
    }

    public String getB5r37arab() {
        QEditText b5r37arab = (QEditText) getView().findViewById(R.id.b5r37arab);
        return b5r37arab.getValue();
    }

    public String getB5r37akam() {
        QEditText b5r37akam = (QEditText) getView().findViewById(R.id.b5r37akam);
        return b5r37akam.getValue();
    }

    public String getB5r37ajum() {
        QEditText b5r37ajum = (QEditText) getView().findViewById(R.id.b5r37ajum);
        return b5r37ajum.getValue();
    }

    public String getB5r37asab() {
        QEditText b5r37asab = (QEditText) getView().findViewById(R.id.b5r37asab);
        return b5r37asab.getValue();
    }

    public String getB5r37amin() {
        QEditText b5r37amin = (QEditText) getView().findViewById(R.id.b5r37amin);
        return b5r37amin.getValue();
    }

    public String getB5r37b() {
        QEditText b5r37b = (QEditText) getView().findViewById(R.id.b5r37b);
        return b5r37b.getValue();
    }

    public String getB5r38a() {
        RadioGroup b5r38a = (RadioGroup) getView().findViewById(R.id.b5r38a);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r38a.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r38b() {
        RadioGroup b5r38b = (RadioGroup) getView().findViewById(R.id.b5r38b);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r38b.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r39() {
        RadioGroup b5r39 = (RadioGroup) getView().findViewById(R.id.b5r39);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r39.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }
}
