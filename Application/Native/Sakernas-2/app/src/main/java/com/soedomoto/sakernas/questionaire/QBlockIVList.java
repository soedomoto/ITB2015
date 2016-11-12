package com.soedomoto.sakernas.questionaire;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soedomoto.sakernas.R;
import com.soedomoto.sakernas.activity.QuestionaireARTActivity;
import com.soedomoto.sakernas.model.ART;

import java.util.Collections;
import java.util.List;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;


public class QBlockIVList extends Fragment {

    public static Fragment newInstance() {
        QBlockIVList blockI = new QBlockIVList();
        return blockI;
    }

    public QBlockIVList() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View qBlockIVView = inflater.inflate(R.layout.questionaire_block_4_list, container, false);

        // Sortable Table
        SortableTableView<ART> tableView = (SortableTableView<ART>)
                qBlockIVView.findViewById(R.id.table_block_4);

        // Set weight for each column
        tableView.setColumnWeight(0, 2);
        tableView.setColumnWeight(1, 5);
        tableView.setColumnWeight(2, 3);

        // Add column header
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), "No", "Nama", "Hubungan KRT"));

        // Add data
        tableView.setDataAdapter(new ARTTableDataAdapter(getContext(), Collections.<ART>emptyList()));
        tableView.addDataClickListener(new ARTClickListener());

        // Add ruta
        FloatingActionButton fabAddArt = (FloatingActionButton) qBlockIVView.findViewById(R.id.fab_add_art);
        fabAddArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewer = new Intent(QBlockIVList.this.getActivity(), QuestionaireARTActivity.class);
                // viewer.putExtra(ViewerActivity.EXTRA_OS, androidOS);
                startActivity(viewer);
            }
        });

        return qBlockIVView;
    }

    public class ARTTableDataAdapter extends TableDataAdapter<ART> {
        private int paddingLeft = 20;
        private int paddingTop = 15;
        private int paddingRight = 20;
        private int paddingBottom = 15;
        private int textSize = 18;
        private int typeface = Typeface.NORMAL;
        private int textColor = 0x99000000;

        public ARTTableDataAdapter(Context context, List<ART> data) {
            super(context, data);
        }

        @Override
        public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
            ART art = getRowData(rowIndex);

            final TextView textView = new TextView(getContext());
            textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            textView.setTypeface(textView.getTypeface(), typeface);
            textView.setTextSize(textSize);
            textView.setTextColor(textColor);
            textView.setSingleLine();
            textView.setEllipsize(TextUtils.TruncateAt.END);

            switch (columnIndex) {
                case 0:
                    textView.setText(rowIndex + 1);
                    break;
                case 1:
                    textView.setText(art.getB4r2());
                    break;
                case 2:
                    textView.setText(art.getB4r3());
                    break;
            }

            return textView;
        }
    }

    private class ARTClickListener implements TableDataClickListener<ART> {
        @Override
        public void onDataClicked(int rowIndex, ART art) {
            String clickedCarString = art.getB4r2();
            Toast.makeText(getContext(), clickedCarString, Toast.LENGTH_SHORT).show();
        }
    }
}
