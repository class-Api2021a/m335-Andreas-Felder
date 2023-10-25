package com.ubs.mycurrency.record;

public class ExchangeRate {

    private String currencyCode; // Currency Code
    private double exchangeRate; // Exchange Rate

    public ExchangeRate(String currencyCode, double exchangeRate) {
        this.currencyCode = currencyCode;
        this.exchangeRate = exchangeRate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }
}
