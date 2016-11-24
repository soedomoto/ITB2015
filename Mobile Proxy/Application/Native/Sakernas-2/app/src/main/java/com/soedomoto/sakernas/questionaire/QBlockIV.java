package com.soedomoto.sakernas.questionaire;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.soedomoto.sakernas.R;
import com.soedomoto.sakernas.widget.QRadioButton;


public class QBlockIV extends Fragment {

    public static QBlockIV newInstance() {
        QBlockIV blockI = new QBlockIV();
        return blockI;
    }

    public QBlockIV() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.questionaire_block_4, container, false);

        RadioGroup b4r4 = (RadioGroup) view.findViewById(R.id.b4r4);
        b4r4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                QRadioButton radio = (QRadioButton) view.findViewById(i);
                Log.i("CUSTOM", radio.getValue() + "");
            }
        });

        return view;
    }
}
