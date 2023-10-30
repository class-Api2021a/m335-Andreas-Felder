package com.ubs.mycurrency.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ubs.mycurrency.record.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountryUtil {
    private static final String PREFERENCES_NAME = "CountryPreferences";
    private static final String FAVORITE_COUNTRIES_KEY = "favoriteCountries";

    private List<Country> countryList;

    public CountryUtil(Context context) {
        loadFavoriteCountries(context);
    }

    public void toggleFavorite(Context context, Country country) {
        country.setFavorite(!country.isFavorite());
        saveFavoriteCountries(context);
    }


    private void saveFavoriteCountries(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> favoriteCodes = new HashSet<>();

        for (Country country : countryList) {
            favoriteCodes.add(country.getCode());
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(FAVORITE_COUNTRIES_KEY, favoriteCodes);
        editor.apply();
    }

    public void loadFavoriteCountries(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> favoriteCodes = preferences.getStringSet(FAVORITE_COUNTRIES_KEY, new HashSet<>());
        countryList = new ArrayList<>();
        favoriteCodes.forEach(code -> {
            Currency currency = Currency.getEnumFromString(Currency.class, code);
            countryList.add(new Country(currency.name(), currency.getCountry(), currency.getCountry(), currency.getCountry(), true));
        });

    }

    public List<Currency> getSortedCurrencyNamesByFavorite(Context context) {
        List<Currency> currencies = new ArrayList<>();
        loadFavoriteCountries(context);

        // Iterate through the Currency enum and add country names to the list
        for (Country country : countryList) {
            currencies.add(Currency.getEnumFromString(Currency.class, country.getCode()));
        }

        // Sort the country names alphabetically
        Collections.sort(currencies);

        return currencies;
    }

    // Method to find the Currency enum by partial country name or currency code match
    public List<Currency> findCurrencyByInput(String input) {
        List<Currency> currencies = new ArrayList<>();
        for (Country country : countryList) {
            // Check if the input matches partially with the country name or currency code (case-insensitive)
            Currency currency = Currency.getEnumFromString(Currency.class, country.getCode());
            if (currency.getCountry().toLowerCase().contains(input.toLowerCase()) || currency.name().toLowerCase().contains(input.toLowerCase())) {
                currencies.add(currency);
            }
        }
        return currencies;
    }
}
