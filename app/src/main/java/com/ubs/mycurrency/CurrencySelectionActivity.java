package com.ubs.mycurrency;

import static com.ubs.mycurrency.util.CurrencyUtil.getSortedCountryNames;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ubs.mycurrency.util.Currency;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CurrencySelectionActivity extends AppCompatActivity {

    private Button returnToMainScreen;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //prevents that dark mode makes the UI unusable by stopping it from making all white elements dark

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_selection);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        returnToMainScreen = findViewById(R.id.returnToMainScreen);


        returnToMainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();

                String currencySelected = intent.getStringExtra("currencyselected");
                switch(currencySelected){
                    case "main":
                        MainActivity.CURRENCY_MAP.put(1, Currency.getEnumFromString(Currency.class, "USD"));
                        break;
                    case "second":
                        MainActivity.CURRENCY_MAP.put(2, Currency.getEnumFromString(Currency.class, "CHF"));
                        break;
                    case "third":

                        break;
                    default:
                        break;
                }

                intent.putExtra("result", "newData" +"  " + intent.getStringExtra("currencyselected"));

                finish();
            }
        });

        LinearLayout layout = findViewById(R.id.countryScreen);

        // Your list of countries (ensure it's sorted)
        String[] countriesArray = {"Afghanistan", "Argentina", "Australia", "Andorra", "Brazil", "Belgium"};
        List<String> countries = Arrays.asList(countriesArray);
        Collections.sort(countries);

        Character lastHeaderChar = null;

        for (String country : countries) {
            char firstChar = country.charAt(0);

            // If this country's first letter is different from the previous one, add a header
            if (lastHeaderChar == null || !lastHeaderChar.equals(firstChar)) {
                TextView headerTextView = new TextView(this);
                headerTextView.setText(String.valueOf(firstChar));
                headerTextView.setTextSize(24f);
                headerTextView.setTypeface(null, Typeface.BOLD); // Make it stand out as a header


                layout.addView(headerTextView);

                lastHeaderChar = firstChar;
            }

            // Add the country name
            TextView countryTextView = new TextView(this);
            countryTextView.setText(country);
            layout.addView(countryTextView);
        }
    }



}
