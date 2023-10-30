package com.ubs.mycurrency.service;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubs.mycurrency.R;

public class CalculatorButtonClickListener implements View.OnClickListener {

    // EditText and Context variables
    private final EditText editText;
    private Context context;

    // Service used for rate calculation
    private CalculateRateService calculateRateService;

    // Constructor to initialize the listener with the associated EditText and Context
    public CalculatorButtonClickListener(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;
        this.calculateRateService = new CalculateRateService(); // Initialize the rate calculation service
    }

    @Override
    public void onClick(View view) {
        // Check if the clicked view is a Button
        if (view instanceof Button) {
            Button button = (Button) view;
            String buttonText = button.getText().toString();

            Log.d("CalculatorButtonListener", "Button clicked with text: " + buttonText);

            // Logic to handle backspace functionality and limit decimal input to 2 places
            if (buttonText.equals(context.getString(R.string.empty_string)) && editText.getText().toString().length() > 0) {
                String text = editText.getText().toString();
                editText.setText(text.substring(0, text.length() - 1));
                Log.d("CalculatorButtonListener", "Backspace triggered. Updated text: " + editText.getText());
            } else {
                String[] parts = editText.getText().toString().split("\\.", 2);
                if (parts.length > 1) {
                    if (!(parts[1].length() >= 2)) {
                        appendEditText(buttonText); // Append to the EditText if conditions are met
                        Log.d("CalculatorButtonListener", "Appended text: " + buttonText + ". Updated text: " + editText.getText());
                    }
                } else {
                    appendEditText(buttonText); // Append to the EditText if no decimal exists
                    Log.d("CalculatorButtonListener", "Appended text: " + buttonText + ". Updated text: " + editText.getText());
                }
            }
        }

        // Perform rate calculation using the service
        calculateRateService.calculateRateService(editText);
        Log.d("CalculatorButtonListener", "Rate calculation performed for EditText: " + editText.getId());
    }

    // Method to append text to the EditText
    private void appendEditText(String buttonText) {
        String regex = ".*\\*.*\\*.*"; // Regex to prevent consecutive multiplication

        // Append the text if conditions are met
        if (!editText.getText().toString().matches(regex) && !buttonText.equals(context.getString(R.string.empty_string))) {
            editText.append(buttonText);
        }

        // Check and correct the situation where the EditText contains only a decimal point
        if (editText.getText().toString().equals(".") && buttonText.equals(".")) {
            editText.setText("0.");
        }
    }
}

