package com.ubs.mycurrency.service;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubs.mycurrency.R;

public class CalculatorButtonClickListener implements View.OnClickListener {

    private final EditText editText;
    private Context context;

    private CalculateRateService calculateRateService;

    public CalculatorButtonClickListener(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;
        this.calculateRateService =  new CalculateRateService();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            String buttonText = button.getText().toString();

            if (buttonText.equals(context.getString(R.string.empty_string)) && editText.getText().toString().length() > 0) {
                String text = editText.getText().toString();
                editText.setText(text.substring(0, text.length() - 1));
            } else {
                String[] parts = editText.getText().toString().split("\\.", 2);
                if ((parts.length > 1)) {
                    if (!(parts[1].length() >= 2)) {
                        appendEditText(buttonText);
                    }
                } else {
                    appendEditText(buttonText);
                }
            }
        }
        calculateRateService.calculateRateService(editText);
    }

    private void appendEditText(String buttonText) {
        String regex = ".*\\*.*\\*.*";

        if (!editText.getText().toString().matches(regex) && !buttonText.equals(context.getString(R.string.empty_string))) {
            editText.append(buttonText);
        }
        if (editText.getText().toString().equals(".") && buttonText.equals(".")) {
            editText.setText("0.");
        }
    }
}

