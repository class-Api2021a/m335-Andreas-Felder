package com.ubs.mycurrency.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyUtil {

    private static final String TAG = CurrencyUtil.class.getSimpleName();

    // Shared Preferences Keys for main and secondary selected currencies
    private static final String PREFERENCE_KEY_MAIN_CURRENCY = "main_currency";
    private static final String PREFERENCE_KEY_SECONDARY_CURRENCY = "secondary_currency";

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

    // Method to find and save the main currency by country name
    public static Currency findAndSaveMainCurrencyByCountryName(Context context, String targetCountryName) {
        Currency currency = findCurrencyByCountryName(context, targetCountryName);
        if (currency != null) {
            saveSelectedCurrency(context, PREFERENCE_KEY_MAIN_CURRENCY, currency);
            Log.d(TAG, "Main currency selected: " + currency.name());
        }
        return currency;
    }

    // Method to find and save the secondary currency by country name
    public static Currency findAndSaveSecondaryCurrencyByCountryName(Context context, String targetCountryName) {
        Currency currency = findCurrencyByCountryName(context, targetCountryName);
        if (currency != null) {
            saveSelectedCurrency(context, PREFERENCE_KEY_SECONDARY_CURRENCY, currency);
            Log.d(TAG, "Secondary currency selected: " + currency.name());
        }
        return currency;
    }

    // Method to retrieve the main selected currency from SharedPreferences
    public static Currency getMainCurrency(Context context) {
        return getSelectedCurrency(context, PREFERENCE_KEY_MAIN_CURRENCY);
    }

    // Method to retrieve the secondary selected currency from SharedPreferences
    public static Currency getSecondaryCurrency(Context context) {
        return getSelectedCurrency(context, PREFERENCE_KEY_SECONDARY_CURRENCY);
    }

    private static Currency findCurrencyByCountryName(Context context, String targetCountryName) {
        for (Currency currency : Currency.values()) {
            if (currency.getCountry().equalsIgnoreCase(targetCountryName)) {
                return currency;
            }
        }
        return null; // Return null if no matching currency is found
    }

    private static void saveSelectedCurrency(Context context, String preferenceKey, Currency currency) {
        SharedPreferences preferences = context.getSharedPreferences("CurrencyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(preferenceKey, currency.getIsoCountryCode());
        editor.apply();
    }

    private static Currency getSelectedCurrency(Context context, String preferenceKey) {
        SharedPreferences preferences = context.getSharedPreferences("CurrencyPreferences", Context.MODE_PRIVATE);
        String isoCountryCode = preferences.getString(preferenceKey, null);
        if (isoCountryCode != null) {
            for (Currency currency : Currency.values()) {
                if (currency.getIsoCountryCode().equalsIgnoreCase(isoCountryCode)) {
                    return currency;
                }
            }
        }
        return null;
    }
}
