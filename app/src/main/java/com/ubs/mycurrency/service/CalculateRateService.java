package com.ubs.mycurrency.service;

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

    private static final String TAG = "CalculateRateService";
    private static final List<ExchangeRate> EXCHANGE_RATES = MainActivity.EXCHANGE_RATES;

    private static final Map<Integer, Currency> CURRENCY_MAP = MainActivity.CURRENCY_MAP;

    private EditText mainEditText;
    private EditText secondaryEditText;

    private int selectedEditTextIndex;

    public void calculateRateService(EditText editText) {
        if (!(editText.getText().toString().equals(""))) {

            if (editText.getId() == R.id.mainCurrencyInput) {
                selectedEditTextIndex = 0;
                mainEditText = editText;
                secondaryEditText = MainActivity.secondaryEditText;
            } else {
                selectedEditTextIndex = 1;
                mainEditText = MainActivity.mainEditText;
                secondaryEditText = editText;
            }

            if (selectedEditTextIndex == 0) {
                ExchangeRate secondaryExchangeRate = MainActivity.getExchangeRateByCode(CURRENCY_MAP.get(2).name(), EXCHANGE_RATES);
                double secondaryExchangeRateValue = secondaryExchangeRate.getExchangeRate() * Double.parseDouble(mainEditText.getText().toString());
                secondaryEditText.setText(String.valueOf(Math.round(secondaryExchangeRateValue * 100.0) / 100.0));
            } else if (selectedEditTextIndex == 1) {
                ExchangeRate mainExchangeRate = MainActivity.getExchangeRateByCode(CURRENCY_MAP.get(1).name(), EXCHANGE_RATES);
                double mainExchangeRateValue = mainExchangeRate.getExchangeRate() * Double.parseDouble(secondaryEditText.getText().toString());
                mainEditText.setText(String.valueOf(Math.round(mainExchangeRateValue * 100.0) / 100.0));
            }
        } else {
            clearEditTexts();
        }
    }
    private void clearEditTexts() {
        mainEditText.setText("");
        secondaryEditText.setText("");
    }
}
