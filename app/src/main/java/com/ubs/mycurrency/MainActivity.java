package com.ubs.mycurrency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.ubs.mycurrency.api.ExchangeRateAPI;
import com.ubs.mycurrency.record.ExchangeRate;
import com.ubs.mycurrency.service.CalculateRateService;
import com.ubs.mycurrency.service.CalculatorButtonClickListener;
import com.ubs.mycurrency.service.NoImeEditText;
import com.ubs.mycurrency.util.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // List to store exchange rates
    public static final List<ExchangeRate> EXCHANGE_RATES = new ArrayList<>();

    // Map to store currency IDs
    private Button selectCurrencyButtonMain;
    private Button selectCurrencyButtonSecond;
    private Button selectCurrencyButtonThird;

    public static final Map<Integer, Currency> CURRENCY_MAP = new HashMap<>();
    private Button moreCurrencyButton;
    private final List<Integer> buttonIds = new ArrayList<>();
    public static NoImeEditText mainEditText;
    public static NoImeEditText secondaryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prevents dark mode from affecting the UI
        Intent intent = new Intent(MainActivity.this, CurrencySelectionActivity.class);

        //prevents that dark mode makes the UI unusable by stopping it from making all white elements dark
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize moreCurrencyButton
        moreCurrencyButton = findViewById(R.id.moreCurrencyButton);

        // OnClickListener for moreCurrencyButton to open CurrencySelectionActivity
        selectCurrencyButtonMain = findViewById(R.id.changeMainCurrencyButton);
        selectCurrencyButtonSecond = findViewById(R.id.changesecondaryCurrencyButton);

        moreCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        selectCurrencyButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("currencyselected", "main");
                startActivity(intent);
            }
        });

        selectCurrencyButtonSecond.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                intent.putExtra("currencyselected", "second");
                startActivity(intent);
            }
        });

        /*selectCurrencyButtonThird.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                intent.putExtra("currencyselected", "third");
                startActivity(intent);
            }
        });*/


        // Get reference to GridLayout and collect Button IDs
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        // Loop through the GridLayout to get the Button IDs
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                buttonIds.add(button.getId());
            }
        }

        // Initialize main and secondary EditText and their respective ButtonClickListeners
        mainEditText = findViewById(R.id.mainCurrencyInput);
        secondaryEditText = findViewById(R.id.secondaryCurrencyInput);
        CalculatorButtonClickListener mainButtonClickListener = new CalculatorButtonClickListener(this, mainEditText);
        CalculatorButtonClickListener secondaryButtonClickListener = new CalculatorButtonClickListener(this, secondaryEditText);

        // Assign ButtonClickListeners based on the focused EditText
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
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

        // Set the focus change listener for main and secondary EditText
        mainEditText.setOnFocusChangeListener(focusChangeListener);
        secondaryEditText.setOnFocusChangeListener(focusChangeListener);

        // Default currencies
        CURRENCY_MAP.put(1, Currency.getEnumFromString(Currency.class, "CHF"));
        CURRENCY_MAP.put(2, Currency.getEnumFromString(Currency.class, "EUR"));

        // Load flags for default currencies
        loadMainFlag(getFlagDefinition(Objects.requireNonNull(CURRENCY_MAP.get(1)).getIsoCountryCode()));
        loadSecondaryFlag(getFlagDefinition(Objects.requireNonNull(CURRENCY_MAP.get(2)).getIsoCountryCode()));

        // Set default currency names on TextViews
        TextView mainLabel = findViewById(R.id.mainCurrencyTag);
        TextView secondaryLabel = findViewById(R.id.secondaryCurrencyTag);
        mainLabel.setText(Objects.requireNonNull(CURRENCY_MAP.get(1)).name());
        secondaryLabel.setText(Objects.requireNonNull(CURRENCY_MAP.get(2)).name());

        // Log default currency information
        Log.i("MainActivity", "Main currency: " + Objects.requireNonNull(CURRENCY_MAP.get(1)).getCountry());
        Log.i("MainActivity", "Secondary currency: " + Objects.requireNonNull(CURRENCY_MAP.get(2)).getCountry());

        // Run API threads to fetch exchange rates
        runApiThreads();
    }

    // Load flag image for the main currency
    @Override
    public void onResume() {
        super.onResume();


    }

    private void loadMainFlag(String flagDefinition) {
        // Load image into the ImageView using Picasso
        ImageView imageView = findViewById(R.id.mainCountryImage);
        Picasso.get().load(flagDefinition).into(imageView);
    }

    // Load flag image for the secondary currency
    private void loadSecondaryFlag(String flagDefinition) {
        // Load image into the ImageView using Picasso
        ImageView imageView = findViewById(R.id.secondaryCountryImage);
        Picasso.get().load(flagDefinition).into(imageView);
    }

    // Get flag image URL for a specific country
    private String getFlagDefinition(String countryNameAbbreviation) {
        String flagDefinition = "https://flagcdn.com/256x192/" + countryNameAbbreviation.toLowerCase() + ".png";
        Log.i("MainActivity", "Flag definition: " + flagDefinition);
        return flagDefinition;
    }

    // Get ExchangeRate object based on currency code
    public static ExchangeRate getExchangeRateByCode(String currencyCode, List<ExchangeRate> exchangeRateList) {
        for (ExchangeRate exchangeRate : exchangeRateList) {
            if (exchangeRate.getCurrencyCode().equals(currencyCode)) {
                return exchangeRate;
            }
        }
        return null; // Return null if not found
    }

    // Call API to fetch exchange rates for a given currency
    private void callAPI(String currencyCode) {
        ExchangeRateAPI exchangeRateAPI = new ExchangeRateAPI();
        exchangeRateAPI.setApiUrl("https://v6.exchangerate-api.com/v6/dd4a10dd68634b9030cf3b92/latest/");
        exchangeRateAPI.fetchExchangeRates(currencyCode, (code, exchangeRate) -> {
            if (code != null) {
                Log.i("ExchangeRateAPI", code + ": " + exchangeRate);
                EXCHANGE_RATES.add(new ExchangeRate(code, exchangeRate));
            } else {
                Log.e("ExchangeRateAPI", "API call failed");
            }
        });
    }

    // Run API threads to fetch exchange rates for default currencies
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