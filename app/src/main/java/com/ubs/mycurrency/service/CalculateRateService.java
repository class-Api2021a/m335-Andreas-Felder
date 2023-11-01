package com.ubs.mycurrency.service;

import static com.ubs.mycurrency.MainActivity.CURRENCY_MAP;
import static com.ubs.mycurrency.MainActivity.EXCHANGE_RATES;

import android.util.Log;
import android.widget.EditText;

import com.ubs.mycurrency.MainActivity;
import com.ubs.mycurrency.R;
import com.ubs.mycurrency.record.ExchangeRate;
import com.ubs.mycurrency.util.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateRateService {
    // Tag for logging
    private static final String TAG = "CalculateRateService";


    // EditTexts for main and secondary currencies, and an index to determine selected EditText
    private EditText mainEditText;
    private EditText secondaryEditText;

    private EditText thirdEditText;

    private int selectedEditTextIndex;

    // Method to calculate the exchange rate based on the edited EditText
    public void calculateRateService(EditText editText) {
        if (!(editText.getText().toString().equals(""))) {
            Log.d(TAG, "EditText has text: " + editText.getText().toString());

            // Identify which EditText is being edited
            if (editText.getId() == R.id.mainCurrencyInput) {
                selectedEditTextIndex = 0; // Indicates the main currency EditText
                mainEditText = editText;
                secondaryEditText = MainActivity.secondaryEditText;
                thirdEditText = MainActivity.thirdEditText;
            } else if (editText.getId() == R.id.secondaryCurrencyInput){
                selectedEditTextIndex = 1; // Indicates the secondary currency EditText
                mainEditText = MainActivity.mainEditText;
                secondaryEditText = editText;
                thirdEditText = MainActivity.thirdEditText;
            } else if (editText.getId() == R.id.thirdCurrencyInput) {
                selectedEditTextIndex = 2; // Indicates the secondary currency EditText
                mainEditText = MainActivity.mainEditText;
                secondaryEditText = MainActivity.secondaryEditText;
                thirdEditText = editText;
            }

            Log.d(TAG, "Selected EditText index: " + selectedEditTextIndex);

            // Calculate and update values based on the selected EditText
            if (selectedEditTextIndex == 0) {
                // If the main currency EditText is edited
                ExchangeRate secondaryExchangeRate = MainActivity.getExchangeRateByCode(CURRENCY_MAP.get(2).name(), EXCHANGE_RATES);
                ExchangeRate thirdExchangeRate = MainActivity.getExchangeRateByCode(CURRENCY_MAP.get(3).name(), EXCHANGE_RATES);
                double secondaryExchangeRateValue = secondaryExchangeRate.getExchangeRate() * Double.parseDouble(mainEditText.getText().toString());
                double thirdExchangeRateValue = thirdExchangeRate.getExchangeRate() * Double.parseDouble(mainEditText.getText().toString());
                secondaryEditText.setText(String.valueOf(Math.round(secondaryExchangeRateValue * 100.0) / 100.0));
                thirdEditText.setText(String.valueOf(Math.round(thirdExchangeRateValue * 100.0) / 100.0));
                Log.d(TAG, "Updated secondaryEditText with calculated value: " + secondaryEditText.getText());
            } else if (selectedEditTextIndex == 1) { // If the secondary currency EditText is edited
                ExchangeRate secondaryExchangeRate = MainActivity.getExchangeRateByCode(CURRENCY_MAP.get(2).name(), EXCHANGE_RATES);
                ExchangeRate thirdExchangeRate = MainActivity.getExchangeRateByCode(CURRENCY_MAP.get(3).name(), EXCHANGE_RATES);
                double mainExchangeRateValue = (1 / secondaryExchangeRate.getExchangeRate()) * Double.parseDouble(secondaryEditText.getText().toString());
                double thirdExchangeRateValue = thirdExchangeRate.getExchangeRate() * mainExchangeRateValue;
                mainEditText.setText(String.valueOf(Math.round(mainExchangeRateValue * 100.0) / 100.0));
                thirdEditText.setText(String.valueOf(Math.round(thirdExchangeRateValue * 100.0) / 100.0));
                Log.d(TAG, "Updated mainEditText with calculated value: " + mainEditText.getText());
            } else if (selectedEditTextIndex == 2) {
                ExchangeRate secondaryExchangeRate = MainActivity.getExchangeRateByCode(CURRENCY_MAP.get(2).name(), EXCHANGE_RATES);
                ExchangeRate thirdExchangeRate = MainActivity.getExchangeRateByCode(CURRENCY_MAP.get(3).name(), EXCHANGE_RATES);
                double mainExchangeRateValue = (1 / thirdExchangeRate.getExchangeRate()) * Double.parseDouble(thirdEditText.getText().toString());
                double secondaryExchangeRateValue = secondaryExchangeRate.getExchangeRate() * mainExchangeRateValue;
                mainEditText.setText(String.valueOf(Math.round(mainExchangeRateValue * 100.0) / 100.0));
                secondaryEditText.setText(String.valueOf(Math.round(secondaryExchangeRateValue * 100.0) / 100.0));
                Log.d(TAG, "Updated mainEditText with calculated value: " + mainEditText.getText());
            }
        } else {
            clearEditTexts(); // Clear both EditTexts if the edited one is empty
            Log.d(TAG, "EditText is empty. Cleared both EditTexts.");
        }
    }

    // Method to clear both EditTexts
    private void clearEditTexts() {
        mainEditText.setText("");
        secondaryEditText.setText("");
        thirdEditText.setText("");
        Log.d(TAG, "Cleared both EditTexts.");
    }
}