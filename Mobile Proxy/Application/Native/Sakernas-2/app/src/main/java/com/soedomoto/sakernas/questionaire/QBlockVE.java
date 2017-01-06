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


public class QBlockVE extends Fragment {

    public static QBlockVE newInstance() {
        QBlockVE blockI = new QBlockVE();
        return blockI;
    }

    public QBlockVE() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_5e, container, false);
        return view;
    }

    public String getB5r34() {
        RadioGroup b5r34 = (RadioGroup) getView().findViewById(R.id.b5r34);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r34.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r35() {
        QEditText b5r35 = (QEditText) getView().findViewById(R.id.b5r35);
        return b5r35.getValue();
    }

    public String getB5r36() {
        RadioGroup b5r36 = (RadioGroup) getView().findViewById(R.id.b5r36);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r36.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }
}
