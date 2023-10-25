package com.ubs.mycurrency.service;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class NoImeEditText extends androidx.appcompat.widget.AppCompatEditText {

    public NoImeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return false;
    }
}
