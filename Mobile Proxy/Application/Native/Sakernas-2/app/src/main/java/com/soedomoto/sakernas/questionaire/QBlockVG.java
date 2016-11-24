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


public class QBlockVG extends Fragment {

    public static QBlockVG newInstance() {
        QBlockVG blockI = new QBlockVG();
        return blockI;
    }

    public QBlockVG() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_5g, container, false);
        return view;
    }

    public String getB5r40() {
        RadioGroup b5r40 = (RadioGroup) getView().findViewById(R.id.b5r40);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r40.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r41() {
        RadioGroup b5r41 = (RadioGroup) getView().findViewById(R.id.b5r41);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r41.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r42() {
        RadioGroup b5r42 = (RadioGroup) getView().findViewById(R.id.b5r42);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r42.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r42lainnya() {
        QEditText b5r42lainnya = (QEditText) getView().findViewById(R.id.b5r42lainnya);
        return b5r42lainnya.getValue();
    }

    public String getB5r43() {
        QEditText b5r43 = (QEditText) getView().findViewById(R.id.b5r43);
        return b5r43.getValue();
    }

    public String getB5r44() {
        RadioGroup b5r44 = (RadioGroup) getView().findViewById(R.id.b5r44);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r44.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r45() {
        RadioGroup b5r45 = (RadioGroup) getView().findViewById(R.id.b5r45);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r45.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r45negara() {
        QEditText b5r45negara = (QEditText) getView().findViewById(R.id.b5r45negara);
        return b5r45negara.getValue();
    }
}
