package com.ubs.mycurrency.dao;

public class Country implements Comparable<Country> {

    private String countryName;
        private String countryNameAbbreviation;
    private String flagDefinition;

    public Country(String countryName, String countryNameAbbreviation, String flagDefinition) {
        this.countryName = countryName;
        this.countryNameAbbreviation = countryNameAbbreviation;
        this.flagDefinition = flagDefinition;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryNameAbbreviation() {
        return countryNameAbbreviation;
    }

    public void setCountryNameAbbreviation(String countryNameAbbreviation) {
        this.countryNameAbbreviation = countryNameAbbreviation;
    }

    public String getFlagDefinition() {
        return flagDefinition;
    }

    public void setFlagDefinition(String flagDefinition) {
        this.flagDefinition = flagDefinition;
    }

    @Override
    public int compareTo(Country country) {
        return this.countryName.compareTo(country.getCountryName());
    }
}
