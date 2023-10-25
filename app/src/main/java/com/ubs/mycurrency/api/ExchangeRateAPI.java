package com.ubs.mycurrency.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class ExchangeRateAPI {

    public interface OnDataAvailable {
        void onDataAvailable(String currencyCode, double exchangeRate);
    }

    private static final String TAG = "ExchangeRateAPI";
    private final Handler handler;
    private String apiUrl; // The base API URL

    public ExchangeRateAPI() {
        handler = new Handler(Looper.getMainLooper());
    }

    // Set the base API URL
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void fetchExchangeRates(String currencyCode, final OnDataAvailable callback) {
        Thread thread = new Thread(() -> {
            try {
                if (apiUrl == null || apiUrl.isEmpty() || currencyCode == null || currencyCode.isEmpty()) {
                    handler.post(() -> {
                        callback.onDataAvailable(null, 0.0);
                    });
                    return;
                }

                Log.d(TAG, "Fetching exchange rates for currency: " + currencyCode);

                // Create the complete URL by appending the currency code to the base URL
                String completeUrl = apiUrl + currencyCode;

                Log.d(TAG, "API URL: " + completeUrl);

                // Create a URL object
                URL url = new URL(completeUrl);

                // Open a connection to the URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Get the response code
                int responseCode = connection.getResponseCode();

                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response from the input stream
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    Log.d(TAG, "API Response: " + response.toString());

                    // Parse the JSON response
                    try {
                        JSONObject json = new JSONObject(response.toString());
                        JSONObject conversionRates = json.getJSONObject("conversion_rates");

                        // Iterate through the conversion rates and invoke the callback for each currency
                        for (Iterator<String> it = conversionRates.keys(); it.hasNext(); ) {
                            String code = it.next();
                            double exchangeRate = conversionRates.getDouble(code);
                            handler.post(() -> {
                                callback.onDataAvailable(code, exchangeRate);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        handler.post(() -> {
                            callback.onDataAvailable(null, 0.0);
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "API request failed: " + e.getMessage());
                handler.post(() -> {
                    callback.onDataAvailable(null, 0.0);
                });
            }
        });
        thread.start();
    }
}
