package com.soedomoto.sakernas.questionaire;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soedomoto.sakernas.R;


public class QBlockVB extends Fragment {

    public static QBlockVB newInstance() {
        QBlockVB blockI = new QBlockVB();
        return blockI;
    }

    public QBlockVB() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_5b, container, false);
        return view;
    }
}
