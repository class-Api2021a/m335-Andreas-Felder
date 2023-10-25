package com.ubs.mycurrency;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.ubs.mycurrency.api.ExchangeRateAPI;
import com.ubs.mycurrency.record.ExchangeRate;
import com.ubs.mycurrency.util.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final List<ExchangeRate> exchangeRates = new ArrayList<>();

    private final Map<Integer, Currency> currencyMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currencyMap.put(1, Currency.getEnumFromString(Currency.class, "USD")); //Default
        currencyMap.put(2, Currency.getEnumFromString(Currency.class,"EUR"));  //Default
        loadMainFlag(getFlagDefinition(Objects.requireNonNull(currencyMap.get(1)).getIsoCountryCode()));
        loadSecondaryFlag(getFlagDefinition(Objects.requireNonNull(currencyMap.get(2)).getIsoCountryCode()));

        Log.i("MainActivity", "Main currency: " + Objects.requireNonNull(currencyMap.get(1)).getCountry());
        Log.i("MainActivity", "Secondary currency: " + Objects.requireNonNull(currencyMap.get(2)).getCountry());

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

    private ExchangeRate getExchangeRateByCode(String currencyCode, List<ExchangeRate> exchangeRateList) {
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
                exchangeRates.add(new ExchangeRate(currencyCode, exchangeRate));
            } else {
                // Handle the case where the API call failed
                Log.e("ExchangeRateAPI", "API call failed");
            }
        });
    }

    private synchronized void runApiThreads() {
        Thread mainThread = new Thread(() -> callAPI(Objects.requireNonNull(currencyMap.get(1)).name()));


        Thread secondaryThread = new Thread(() -> {
            TextView mainLabel = findViewById(R.id.mainCurrencyRateLabel);
            TextView secondaryLabel = findViewById(R.id.secondaryCurrencyRateLabel);
            mainLabel.setText("\n\nx " + getExchangeRateByCode(Objects.requireNonNull(currencyMap.get(1)).name(), exchangeRates).getExchangeRate());
            secondaryLabel.setText("\n\nx " + getExchangeRateByCode(Objects.requireNonNull(currencyMap.get(2)).name(), exchangeRates).getExchangeRate());
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