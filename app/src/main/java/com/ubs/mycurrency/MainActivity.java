package com.ubs.mycurrency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.ubs.mycurrency.api.ExchangeRateAPI;
import com.ubs.mycurrency.record.ExchangeRate;
import com.ubs.mycurrency.service.CalculatorButtonClickListener;
import com.ubs.mycurrency.service.NoImeEditText;
import com.ubs.mycurrency.util.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final List<ExchangeRate> EXCHANGE_RATES = new ArrayList<>();

    public static final Map<Integer, Currency> CURRENCY_MAP = new HashMap<>();

    private final List<Integer> buttonIds = new ArrayList<>();
    
    public static NoImeEditText mainEditText;
    public static NoImeEditText secondaryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //prevents that dark mode makes the UI unusable by stopping it from making all white elements dark
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gridLayout = findViewById(R.id.gridLayout);

        // Loop through the GridLayout to get the Button IDs
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                buttonIds.add(button.getId());
            }
        }

        mainEditText = findViewById(R.id.mainCurrencyInput);
        secondaryEditText = findViewById(R.id.secondaryCurrencyInput);

        CalculatorButtonClickListener mainButtonClickListener = new CalculatorButtonClickListener(this, mainEditText);
        CalculatorButtonClickListener secondaryButtonClickListener = new CalculatorButtonClickListener(this, secondaryEditText);

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Perform actions for the selected EditText
                    if (v == mainEditText) {
                        for (int buttonId : buttonIds) {
                            Button button = findViewById(buttonId);
                            button.setOnClickListener(mainButtonClickListener);
                        }
                    } else if (v == secondaryEditText) {
                        for (int buttonId : buttonIds) {
                            Button button = findViewById(buttonId);
                            button.setOnClickListener(secondaryButtonClickListener);
                        }
                    }
                }
            }
        };

        mainEditText.setOnFocusChangeListener(focusChangeListener);
        secondaryEditText.setOnFocusChangeListener(focusChangeListener);






        CURRENCY_MAP.put(1, Currency.getEnumFromString(Currency.class, "CHF")); //Default
        CURRENCY_MAP.put(2, Currency.getEnumFromString(Currency.class,"EUR"));  //Default
        loadMainFlag(getFlagDefinition(Objects.requireNonNull(CURRENCY_MAP.get(1)).getIsoCountryCode()));
        loadSecondaryFlag(getFlagDefinition(Objects.requireNonNull(CURRENCY_MAP.get(2)).getIsoCountryCode()));
        TextView mainLabel = findViewById(R.id.mainCurrencyTag);
        TextView secondaryLabel = findViewById(R.id.secondaryCurrencyTag);
        mainLabel.setText(Objects.requireNonNull(CURRENCY_MAP.get(1)).name());
        secondaryLabel.setText(Objects.requireNonNull(CURRENCY_MAP.get(2)).name());


        Log.i("MainActivity", "Main currency: " + Objects.requireNonNull(CURRENCY_MAP.get(1)).getCountry());
        Log.i("MainActivity", "Secondary currency: " + Objects.requireNonNull(CURRENCY_MAP.get(2)).getCountry());

        runApiThreads();
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
        String flagDefinition = "https://flagcdn.com/256x192/" + countryNameAbbreviation.toLowerCase() + ".png";
        Log.i("MainActivity", "Flag definition: " + flagDefinition);
        return flagDefinition;
    }

    public static ExchangeRate getExchangeRateByCode(String currencyCode, List<ExchangeRate> exchangeRateList) {
        for (ExchangeRate exchangeRate : exchangeRateList) {
            if (exchangeRate.getCurrencyCode().equals(currencyCode)) {
                return exchangeRate;
            }
        }
        return null; // Return null if not found
    }

    private void callAPI(String cC) {
        ExchangeRateAPI exchangeRateAPI = new ExchangeRateAPI();
        exchangeRateAPI.setApiUrl("https://v6.exchangerate-api.com/v6/dd4a10dd68634b9030cf3b92/latest/");
        exchangeRateAPI.fetchExchangeRates(cC, (currencyCode, exchangeRate) -> {
            if (currencyCode != null) {
                // Handle the specific currency and its exchange rate
                Log.i("ExchangeRateAPI", currencyCode + ": " + exchangeRate);
                EXCHANGE_RATES.add(new ExchangeRate(currencyCode, exchangeRate));
            } else {
                // Handle the case where the API call failed
                Log.e("ExchangeRateAPI", "API call failed");
            }
        });
    }

    private synchronized void runApiThreads() {
        Thread mainThread = new Thread(() -> callAPI(Objects.requireNonNull(CURRENCY_MAP.get(1)).name()));


        Thread secondaryThread = new Thread(() -> {
            TextView mainLabel = findViewById(R.id.mainCurrencyRateLabel);
            TextView secondaryLabel = findViewById(R.id.secondaryCurrencyRateLabel);
            mainLabel.setText("\n\nx " + getExchangeRateByCode(Objects.requireNonNull(CURRENCY_MAP.get(1)).name(), EXCHANGE_RATES).getExchangeRate());
            secondaryLabel.setText("\n\nx " + getExchangeRateByCode(Objects.requireNonNull(CURRENCY_MAP.get(2)).name(), EXCHANGE_RATES).getExchangeRate());
        });

        mainThread.start();

        try {
            mainThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!mainThread.isAlive()) {
            secondaryThread.start();
        }
    }

}