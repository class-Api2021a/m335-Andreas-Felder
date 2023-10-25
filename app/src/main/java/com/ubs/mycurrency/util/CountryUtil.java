package com.ubs.mycurrency.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ubs.mycurrency.record.Country;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountryUtil {
    private static final String PREFERENCES_NAME = "CountryPreferences";
    private static final String FAVORITE_COUNTRIES_KEY = "favoriteCountries";

    private final List<Country> countryList;

    public CountryUtil(Context context, List<Country> countryList) {
        this.countryList = countryList;
        loadFavoriteCountries(context);
    }

    public List<Country> getCountryList() {
        return countryList;
    }

    public void toggleFavorite(Context context, Country country) {
        country.setFavorite(!country.isFavorite());
        saveFavoriteCountries(context);
    }


    private void saveFavoriteCountries(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> favoriteCodes = new HashSet<>();

        for (Country country : countryList) {
            if (country.isFavorite()) {
                favoriteCodes.add(country.getCode());
            }
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(FAVORITE_COUNTRIES_KEY, favoriteCodes);
        editor.apply();
    }

    private void loadFavoriteCountries(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> favoriteCodes = preferences.getStringSet(FAVORITE_COUNTRIES_KEY, new HashSet<>());

        for (Country country : countryList) {
            country.setFavorite(favoriteCodes.contains(country.getCode()));
        }
    }
}
