package com.ubs.mycurrency;

import static com.ubs.mycurrency.util.CountryUtil.createCountry;
import static com.ubs.mycurrency.util.CurrencyUtil.getSortedCountryNames;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.ubs.mycurrency.record.Country;
import com.ubs.mycurrency.util.CountryUtil;
import com.ubs.mycurrency.util.Currency;
import com.ubs.mycurrency.util.CurrencyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CurrencySelectionActivity extends AppCompatActivity {

    private Button returnToMainScreen;

    //Layout parameters for the letter header
    public static final ConstraintLayout.LayoutParams layoutParamsHeader = new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT);

    public static final ConstraintLayout.LayoutParams layoutParamsInnerConstraintlayout = new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            80);

    public static boolean isFavoriteSwitchChecked = false;

    private static List<String> countryNames = getSortedCountryNames();
    private static HashMap<Character, List<String>> firstLetters = new HashMap<>();

    private static String selectedCurrency = "USD";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //prevents that dark mode makes the UI unusable by stopping it from making all white elements dark
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_selection);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        returnToMainScreen = findViewById(R.id.returnToMainScreen);

        char oldLetter = countryNames.get(0).charAt(0);
        List<String> orderedCountry = new ArrayList<>();
        for (String str: countryNames){
            if(str.charAt(0) == oldLetter){
                orderedCountry.add(str);
            }else{
                firstLetters.put(oldLetter, orderedCountry);
                oldLetter = str.charAt(0);
                orderedCountry = new ArrayList<>();
                orderedCountry.add(str);
            }
        }
        if(orderedCountry.size() != 0){
            firstLetters.put(oldLetter, orderedCountry);
        }

        Switch faveSwitch =  findViewById(R.id.favoriteSwitch);
        faveSwitch.setChecked(isFavoriteSwitchChecked);

        faveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isFavoriteSwitchChecked = isChecked;
                drawCountryList();

            }
        });
        returnToMainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        drawCountryList();


    }

    private void drawCountryList (){
        LinearLayout mainLayout = findViewById(R.id.countryScreen);
        mainLayout.removeAllViews();
        CountryUtil countryUtil = new CountryUtil(CurrencySelectionActivity.this);

        countryUtil.loadFavoriteCountries(CurrencySelectionActivity.this);
        List<String> countries = countryUtil.getSortedCountriesNamesByFavorite(CurrencySelectionActivity.this);

        HashSet<Character> faveCharacters = new HashSet<>();
        for(String s : countries){
            faveCharacters.add(s.charAt(0));
        }
        for (HashMap.Entry<Character, List<String>> entry : firstLetters.entrySet()) {
                if(isFavoriteSwitchChecked && !faveCharacters.contains(entry.getKey()) ){
                    continue;
                }
            // Create a new LinearLayout for each letter
            LinearLayout letterLayout = new LinearLayout(this);
            letterLayout.setOrientation(LinearLayout.VERTICAL);

            // Add header TextView for the initial letter
            TextView headerTextView = new TextView(this);
            headerTextView.setText(String.valueOf(entry.getKey()));
            headerTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.border_bottom));
            mainLayout.addView(headerTextView);

            // Loop through countries under this letter
            for (String country : entry.getValue()) {
                if(isFavoriteSwitchChecked && !countries.contains(CurrencyUtil.findCurrencyByCountryName(CurrencySelectionActivity.this, country).getCountry())) {
                    continue;
                }
                // Create ConstraintLayout for each country
                ConstraintLayout countryLayout = new ConstraintLayout(this);
                countryLayout.setTag(country);
                countryLayout.setId(View.generateViewId());  // Generate unique ID for constraints
                countryLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.border_bottom));
                countryLayout.setLayoutParams(layoutParamsInnerConstraintlayout);

                // Create and setup ImageView
                ImageView imageView = new ImageView(this);
                imageView.setId(View.generateViewId());
                ConstraintLayout.LayoutParams imageViewLayoutParams = new ConstraintLayout.LayoutParams(
                        convertToDp(150), convertToDp(100));
                imageViewLayoutParams.setMargins(convertToDp(24), convertToDp(10), 0, convertToDp(10));
                imageView.setLayoutParams(imageViewLayoutParams);

                //set the flag
                Picasso.get().load("https://flagcdn.com/256x192/"+ Currency.getCountryCodeByName(country).toLowerCase()+".png").into(imageView);

                countryLayout.addView(imageView);

                // Create and setup TextView
                TextView textView = new TextView(this);
                textView.setId(View.generateViewId());
                textView.setText(country);
                ConstraintLayout.LayoutParams textViewLayoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(textViewLayoutParams);
                countryLayout.addView(textView);

                // Create and setup Button
                Button button = new Button(this, null, androidx.appcompat.R.style.Widget_AppCompat_Button_Borderless_Colored);
                button.setId(View.generateViewId());
                ConstraintLayout.LayoutParams buttonLayoutParams = new ConstraintLayout.LayoutParams(50, 50);
                button.setLayoutParams(buttonLayoutParams);


                if (countries.contains(CurrencyUtil.findCurrencyByCountryName(CurrencySelectionActivity.this, country).getCountry())) {
                    button.setBackground(ContextCompat.getDrawable(this, R.drawable.filled_star));
                    button.setTag("filled");
                } else {
                    button.setBackground(ContextCompat.getDrawable(this, R.drawable.unfilled_star));
                    button.setTag("unfilled");
                }


                //Button Callback that will allways alternate between two methods, one meant to add
                //the chosen country and in turn it's currency to your favorites, and pressing again
                //in turn removes it from your favorites
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentState = (String) button.getTag();

                        countryUtil.loadFavoriteCountries(CurrencySelectionActivity.this);
                        String countryName = (String) ((ConstraintLayout) button.getParent()).getTag();
                        Currency currency = CurrencyUtil.findCurrencyByCountryName(CurrencySelectionActivity.this, countryName);
                        Country country = createCountry(currency);
                        // Toggle the state and background, and call the respective method
                        if ("unfilled".equals(currentState)) {
                            // Change to filled state
                            button.setBackground(ContextCompat.getDrawable(CurrencySelectionActivity.this, R.drawable.filled_star));
                            button.setTag("filled");
                            countryUtil.addCountry(country);
                            countryUtil.saveFavoriteCountries(CurrencySelectionActivity.this);
                        } else {
                            // Change to unfilled state
                            button.setBackground(ContextCompat.getDrawable(CurrencySelectionActivity.this, R.drawable.unfilled_star));
                            button.setTag("unfilled");
                            countryUtil.removeFavoriteCountry(CurrencySelectionActivity.this, country.getCode());

                        }
                    }
                });


                countryLayout.addView(button);

                // Set up constraints
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(countryLayout);

                // Constraints for imageView
                constraintSet.connect(imageView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, convertToDp(24));
                constraintSet.connect(imageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, convertToDp(10));

                // Constraints for textView
                constraintSet.connect(textView.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END, 0);
                constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);

                // Constraints for button
                constraintSet.connect(button.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, convertToDp(24));
                constraintSet.connect(button.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);

                constraintSet.applyTo(countryLayout);

                // Add countryLayout to outerConstraintLayout
                mainLayout.addView(countryLayout);


            }

        }
    }

    private void changeCurrency (String country){
        Intent intent = getIntent();

        String currencySelected = intent.getStringExtra("currencyselected");
        switch(currencySelected){
            case "main":
                MainActivity.CURRENCY_MAP.put(1, Currency.getEnumFromString(Currency.class, Currency.getCurrencyByName("")));
                break;
            case "second":
                MainActivity.CURRENCY_MAP.put(2, Currency.getEnumFromString(Currency.class, Currency.getCurrencyByName("")));
                break;
            case "third":
                MainActivity.CURRENCY_MAP.put(3, Currency.getEnumFromString(Currency.class, Currency.getCurrencyByName("")));

                break;
            default:
                break;
        }

        intent.putExtra("result", "newData" +"  " + intent.getStringExtra("currencyselected"));

        finish();
    }

    private int convertToDp(int px) {
        return (int) (px / getResources().getDisplayMetrics().density);
    }

}
