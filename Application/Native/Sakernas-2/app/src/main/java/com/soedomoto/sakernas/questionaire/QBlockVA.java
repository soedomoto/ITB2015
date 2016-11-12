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

    public static Fragment newInstance() {
        QBlockVA blockI = new QBlockVA();
        return blockI;
    }

    public QBlockVA() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.questionaire_block_5a, container, false);

        EditText txtJurusan = (EditText) view.findViewById(R.id.editText15);
        txtJurusan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(QBlockVA.this.getContext());
                dialog.setTitle("Ijazah/STTB Tertinggi");
                dialog.setContentView(R.layout.dialog_b5a_r1a);

                dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.hide();
                    }
                });

                dialog.show();
            }
        });

        return view;
    }
}
