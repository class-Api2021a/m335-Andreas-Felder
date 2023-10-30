package com.ubs.mycurrency.record;

public class Country {
    private String code; // Currency Code
    private String name; // Currency Name
    private String countryName; // Country Name
    private String isoCountryCode; // ISO Country Code
    private boolean favorite; // Flag to indicate if it's a favorite

    public Country(String code, String name, String countryName, String isoCountryCode, Boolean favorite) {
        this.code = code;
        this.name = name;
        this.countryName = countryName;
        this.isoCountryCode = isoCountryCode;
        this.favorite = favorite; // Default to not a favorite
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
