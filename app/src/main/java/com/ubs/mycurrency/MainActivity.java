package com.ubs.mycurrency;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;


import com.squareup.picasso.Picasso;
import com.ubs.mycurrency.api.ExchangeRateAPI;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_CURRENCY = 1;
    private TextView mainCurrencyRateLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMainFlag(getFlagDefinition("us"));
        loadSecondaryFlag(getFlagDefinition("gb"));

        ExchangeRateAPI exchangeRateAPI = new ExchangeRateAPI();
        exchangeRateAPI.setApiUrl("https://v6.exchangerate-api.com/v6/dd4a10dd68634b9030cf3b92/latest/");

        exchangeRateAPI.fetchExchangeRates("EUR", (currencyCode, exchangeRate) -> {
            if (currencyCode != null) {
                // Handle the specific currency and its exchange rate
                Log.i("ExchangeRateAPI", currencyCode + ": " + exchangeRate);
            } else {
                // Handle the case where the API call failed
                System.out.println("Failed to fetch data for a currency.");
            }
        });

    }

    private void loadMainFlag(String flagDefinition) {
        // Find the ImageView in your layout
        ImageView imageView = findViewById(R.id.mainCountryImage);

        // Use Picasso to load the image into the ImageView
        Picasso.get().load(flagDefinition).into(imageView);
    }
    private void loadSecondaryFlag(String flagDefinition) {
        // Find the ImageView in your layout
        ImageView imageView = findViewById(R.id.secondaryCountryImage);

        // Use Picasso to load the image into the ImageView
        Picasso.get().load(flagDefinition).into(imageView);
    }

    private String getFlagDefinition(String countryNameAbbreviation) {
        String flagDefinition = "https://flagcdn.com/256x192/" + countryNameAbbreviation + ".png";
        Log.i("MainActivity", "Flag definition: " + flagDefinition);
        return flagDefinition;
    }
}