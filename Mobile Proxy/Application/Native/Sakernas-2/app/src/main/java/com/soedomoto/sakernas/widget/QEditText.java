package com.soedomoto.sakernas.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.soedomoto.sakernas.R;

/**
 * Created by soedomoto on 13/11/16.
 */

public class QEditText extends EditText {
    private String value;

    public QEditText(Context context) {
        super(context);
    }

    public QEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyXML(context, attrs);
    }

    public QEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyXML(context, attrs);
    }

    private void applyXML(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.QEditText,
                0, 0);

        try {
            value = a.getString(R.styleable.QEditText_qEditText_value);
        } finally {
            a.recycle();
        }
    }

    public String getValue() {
        if(value == null) {
            return getText().toString();
        } else {
            return value;
        }
    }

    public void setValue(String value) {
        this.value = value;
    }
}
