package com.soedomoto.sakernas.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.soedomoto.sakernas.R;

/**
 * Created by soedomoto on 13/11/16.
 */

public class QRadioButton extends RadioButton {
    private String value;

    public QRadioButton(Context context) {
        super(context);
    }

    public QRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyXML(context, attrs);
    }

    public QRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyXML(context, attrs);
    }

    private void applyXML(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.QRadioButton,
                0, 0);

        try {
            value = a.getString(R.styleable.QRadioButton_qRadioButton_value);
        } finally {
            a.recycle();
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
