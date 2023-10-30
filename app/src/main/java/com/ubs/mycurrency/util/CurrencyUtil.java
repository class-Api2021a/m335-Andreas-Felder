package com.ubs.mycurrency.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyUtil {

    // Method to get a sorted list of country names
    public static List<String> getSortedCountryNames() {
        List<String> countryNames = new ArrayList<>();

        // Iterate through the Currency enum and add country names to the list
        for (Currency currency : Currency.values()) {
            countryNames.add(currency.getCountry());
        }

        // Sort the country names alphabetically
        Collections.sort(countryNames);

        return countryNames;
    }

    // Method to find the Currency enum by country name
    public static Currency findCurrencyByCountryName(String targetCountryName) {
        for (Currency currency : Currency.values()) {
            if (currency.getCountry().equalsIgnoreCase(targetCountryName)) {
                return currency;
            }
        }
        return null; // Return null if no matching enum is found
    }

    private static Currency findCurrencyByCountryName(Context context, String targetCountryName) {
        for (Currency currency : Currency.values()) {
            if (currency.getCountry().equalsIgnoreCase(targetCountryName)) {
                return currency;
            }
        }
        return null; // Return null if no matching currency is found
    }

    // Method to find the Currency enum by partial country name or currency code match
    public static List<Currency> findCurrenciesByInput(String input) {
        List<Currency> matchedCurrencies = new ArrayList<>();

        for (Currency currency : Currency.values()) {
            // Check if the input matches partially with the country name or currency code (case-insensitive)
            if (currency.getCountry().toLowerCase().contains(input.toLowerCase()) || currency.name().toLowerCase().contains(input.toLowerCase())) {
                matchedCurrencies.add(currency);
            }
        }

        return matchedCurrencies;
    }

}
