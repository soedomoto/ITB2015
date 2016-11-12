package com.soedomoto.sakernas.questionaire;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soedomoto.sakernas.R;


public class QBlockVF extends Fragment {

    public static Fragment newInstance() {
        QBlockVF blockI = new QBlockVF();
        return blockI;
    }

    public QBlockVF() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_4, container, false);

        return view;
    }
}
