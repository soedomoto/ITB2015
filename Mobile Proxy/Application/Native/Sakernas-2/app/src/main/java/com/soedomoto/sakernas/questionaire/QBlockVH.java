package com.soedomoto.sakernas.questionaire;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.soedomoto.sakernas.R;
import com.soedomoto.sakernas.model.ART;
import com.soedomoto.sakernas.widget.QEditText;
import com.soedomoto.sakernas.widget.QRadioButton;


public class QBlockVH extends Fragment {

    public static QBlockVH newInstance() {
        QBlockVH blockI = new QBlockVH();
        return blockI;
    }

    public QBlockVH() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_5h, container, false);
        return view;
    }

    public String getB5r46() {
        RadioGroup b5r46 = (RadioGroup) getView().findViewById(R.id.b5r46);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r46.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r47a() {
        RadioGroup b5r47a = (RadioGroup) getView().findViewById(R.id.b5r47a);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r47a.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r47b() {
        RadioGroup b5r47b = (RadioGroup) getView().findViewById(R.id.b5r47b);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r47b.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r47c() {
        RadioGroup b5r47c = (RadioGroup) getView().findViewById(R.id.b5r47c);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r47c.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r47d() {
        RadioGroup b5r47d = (RadioGroup) getView().findViewById(R.id.b5r47d);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r47d.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r48a() {
        RadioGroup b5r48a = (RadioGroup) getView().findViewById(R.id.b5r48a);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r48a.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r48b() {
        RadioGroup b5r48b = (RadioGroup) getView().findViewById(R.id.b5r48b);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r48b.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r49() {
        RadioGroup b5r49 = (RadioGroup) getView().findViewById(R.id.b5r49);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r49.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }

    public String getB5r50() {
        QEditText b5r50 = (QEditText) getView().findViewById(R.id.b5r50);
        return b5r50.getValue();
    }

    public String getB5r51() {
        QEditText b5r51 = (QEditText) getView().findViewById(R.id.b5r51);
        return b5r51.getValue();
    }

    public String getB5r52() {
        RadioGroup b5r52 = (RadioGroup) getView().findViewById(R.id.b5r52);
        QRadioButton selected = (QRadioButton) getView().findViewById(b5r52.getCheckedRadioButtonId());
        return selected != null ? selected.getValue() : null;
    }
}
