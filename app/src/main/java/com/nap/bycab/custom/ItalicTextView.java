package com.nap.bycab.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.nap.bycab.app.MyApplication;


/**
 * Created by nirav on 10/10/15.
 */
public class ItalicTextView extends AppCompatTextView {

    public ItalicTextView(Context context) {
        super(context);
        setTypeface(context);
    }

    public ItalicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(context);
    }

    public ItalicTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
        if (context != null && !isInEditMode()) {
            setTypeface(MyApplication.getitalicFont());
        }
    }
}
