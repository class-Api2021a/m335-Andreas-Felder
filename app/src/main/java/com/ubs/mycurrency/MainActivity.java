package com.ubs.mycurrency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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
    public static List<ExchangeRate> EXCHANGE_RATES;

    // Map to store currency IDs
    private Button selectCurrencyButtonMain;
    private Button selectCurrencyButtonSecond;
    private Button selectCurrencyButtonThird;

    private CalculateRateService calculateRateService;

    public static final Map<Integer, Currency> CURRENCY_MAP = new HashMap<>();
    private Button moreCurrencyButton;
    private final List<Integer> buttonIds = new ArrayList<>();
    public static NoImeEditText mainEditText;
    public static NoImeEditText secondaryEditText;

    public static EditText thirdEditText;
    private TextView thirdLabel;
    private TextView thirdRateLabel;
    private ImageView thirdCountryImage;
    private ImageView thirdCurrencyArrowImage;

    private Boolean showThirdCurrency = false;
    private boolean isRotated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prevents dark mode from affecting the UI
        Intent intent = new Intent(MainActivity.this, CurrencySelectionActivity.class);

        //prevents that dark mode makes the UI unusable by stopping it from making all white elements dark
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        EXCHANGE_RATES = new ArrayList<>();

        // Initialize moreCurrencyButton
        moreCurrencyButton = findViewById(R.id.moreCurrencyButton);

        // OnClickListener for moreCurrencyButton to open CurrencySelectionActivity
        selectCurrencyButtonMain = findViewById(R.id.changeMainCurrencyButton);
        selectCurrencyButtonSecond = findViewById(R.id.changesecondaryCurrencyButton);

        selectCurrencyButtonThird = findViewById(R.id.changethirdCurrencyButton);
        selectCurrencyButtonThird.setVisibility(View.INVISIBLE);
        thirdEditText = findViewById(R.id.thirdCurrencyInput);
        thirdEditText.setVisibility(View.INVISIBLE);
        thirdLabel = findViewById(R.id.thirdCurrencyTag);
        thirdLabel.setVisibility(View.INVISIBLE);
        thirdRateLabel = findViewById(R.id.thirdCurrencyRateLabel);
        thirdRateLabel.setVisibility(View.INVISIBLE);
        thirdCountryImage = findViewById(R.id.thirdCountryImage);
        thirdCountryImage.setVisibility(View.INVISIBLE);
        thirdCurrencyArrowImage = findViewById(R.id.thirdCurrencyArrowImage);
        thirdCurrencyArrowImage.setVisibility(View.INVISIBLE);

        moreCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float startAngle, endAngle;

                if (!isRotated) {
                    startAngle = 0f;
                    endAngle = 180f;
                } else {
                    startAngle = 180f;
                    endAngle = 0f;
                }

                ObjectAnimator rotation = ObjectAnimator.ofFloat(moreCurrencyButton, "rotation", startAngle, endAngle);
                rotation.setDuration(500); // Set the duration as needed
                rotation.setInterpolator(new LinearInterpolator());

                rotation.start();

                isRotated = !isRotated; // Toggle the state for the next click
                if (!showThirdCurrency) {
                    ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
                    // Get the display metrics to calculate the screen height
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int screenHeight = displayMetrics.heightPixels;

                    // Calculate the desired percentage of the screen height
                    int desiredHeightPercentage = 42; // Change this value to your desired percentage

                    // Convert the percentage to pixels
                    int desiredHeight = (int) (screenHeight * (desiredHeightPercentage / 100.0));

                    // Set the calculated height to the view
                    ViewGroup.LayoutParams params = constraintLayout.getLayoutParams();
                    params.height = desiredHeight;
                    constraintLayout.setLayoutParams(params);

                    thirdEditText.setVisibility(View.VISIBLE);
                    thirdLabel.setVisibility(View.VISIBLE);
                    thirdRateLabel.setVisibility(View.VISIBLE);
                    thirdCountryImage.setVisibility(View.VISIBLE);
                    thirdCurrencyArrowImage.setVisibility(View.VISIBLE);
                    selectCurrencyButtonThird.setVisibility(View.VISIBLE);
                    showThirdCurrency = true;
                } else {
                    ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
                    // Get the display metrics to calculate the screen height
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int screenHeight = displayMetrics.heightPixels;

                    // Calculate the desired percentage of the screen height
                    int desiredHeightPercentage = 31; // Change this value to your desired percentage

                    // Convert the percentage to pixels
                    int desiredHeight = (int) (screenHeight * (desiredHeightPercentage / 100.0));

                    // Set the calculated height to the view
                    ViewGroup.LayoutParams params = constraintLayout.getLayoutParams();
                    params.height = desiredHeight;
                    constraintLayout.setLayoutParams(params);

                    thirdEditText.setVisibility(View.INVISIBLE);
                    thirdLabel.setVisibility(View.INVISIBLE);
                    thirdRateLabel.setVisibility(View.INVISIBLE);
                    thirdCountryImage.setVisibility(View.INVISIBLE);
                    thirdCurrencyArrowImage.setVisibility(View.INVISIBLE);
                    selectCurrencyButtonThird.setVisibility(View.INVISIBLE);
                    showThirdCurrency = false;
                }

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

        selectCurrencyButtonThird.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                intent.putExtra("currencyselected", "third");
                startActivity(intent);
            }
        });


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
        CalculatorButtonClickListener thirdButtonClickListener = new CalculatorButtonClickListener(this, thirdEditText);

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
                    } else if (v == thirdEditText) {
                        for (int buttonId : buttonIds) {
                            Button button = findViewById(buttonId);
                            button.setOnClickListener(thirdButtonClickListener);
                        }
                    }
                }
            }
        };

        // Set the focus change listener for main and secondary EditText
        mainEditText.setOnFocusChangeListener(focusChangeListener);
        secondaryEditText.setOnFocusChangeListener(focusChangeListener);
        thirdEditText.setOnFocusChangeListener(focusChangeListener);

        // Default currencies
        CURRENCY_MAP.put(1, Currency.getEnumFromString(Currency.class, "CHF"));
        CURRENCY_MAP.put(2, Currency.getEnumFromString(Currency.class, "EUR"));
        CURRENCY_MAP.put(3, Currency.getEnumFromString(Currency.class, "USD"));

        // Load flags for default currencies

    }

    // Load flag image for the main currency
    @Override
    public void onResume() {
        super.onResume();
        calculateRateService = new CalculateRateService();
        loadMainFlag(getFlagDefinition(Objects.requireNonNull(CURRENCY_MAP.get(1)).getIsoCountryCode()));
        loadSecondaryFlag(getFlagDefinition(Objects.requireNonNull(CURRENCY_MAP.get(2)).getIsoCountryCode()));
        loadThirdFlag(getFlagDefinition(Objects.requireNonNull(CURRENCY_MAP.get(3)).getIsoCountryCode()));
        if (mainEditText.getText() != null && !mainEditText.getText().toString().isEmpty()) {
            calculateRateService.calculateRateService(mainEditText);
        }

        // Set default currency names on TextViews
        TextView mainLabel = findViewById(R.id.mainCurrencyTag);
        TextView secondaryLabel = findViewById(R.id.secondaryCurrencyTag);
        TextView thirdLabel = findViewById(R.id.thirdCurrencyTag);
        mainLabel.setText(Objects.requireNonNull(CURRENCY_MAP.get(1)).name());
        secondaryLabel.setText(Objects.requireNonNull(CURRENCY_MAP.get(2)).name());
        thirdLabel.setText(Objects.requireNonNull(CURRENCY_MAP.get(3)).name());

        // Log default currency information
        Log.i("MainActivity", "Main currency: " + Objects.requireNonNull(CURRENCY_MAP.get(1)).getCountry());
        Log.i("MainActivity", "Secondary currency: " + Objects.requireNonNull(CURRENCY_MAP.get(2)).getCountry());
        Log.i("MainActivity", "Third currency: " + Objects.requireNonNull(CURRENCY_MAP.get(3)).getCountry());

        // Run API threads to fetch exchange rates
        runApiThreads();

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

    private void loadThirdFlag(String flagDefinition) {
        // Load image into the ImageView using Picasso
        ImageView imageView = findViewById(R.id.thirdCountryImage);
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
        EXCHANGE_RATES = new ArrayList<>();
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
            TextView thirdLabel = findViewById(R.id.thirdCurrencyRateLabel);
            mainLabel.setText("\n\nx " + getExchangeRateByCode(Objects.requireNonNull(CURRENCY_MAP.get(1)).name(), EXCHANGE_RATES).getExchangeRate());
            secondaryLabel.setText("\n\nx " + getExchangeRateByCode(Objects.requireNonNull(CURRENCY_MAP.get(2)).name(), EXCHANGE_RATES).getExchangeRate());
            thirdLabel.setText("\n\nx " + getExchangeRateByCode(Objects.requireNonNull(CURRENCY_MAP.get(3)).name(), EXCHANGE_RATES).getExchangeRate());
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