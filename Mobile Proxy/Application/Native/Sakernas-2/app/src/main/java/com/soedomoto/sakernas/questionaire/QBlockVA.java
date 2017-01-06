package com.soedomoto.sakernas.questionaire;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.soedomoto.sakernas.R;


public class QBlockVA extends Fragment {

    public static QBlockVA newInstance() {
        QBlockVA blockI = new QBlockVA();
        return blockI;
    }

    public QBlockVA() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_5a, container, false);



        return view;
    }
}
