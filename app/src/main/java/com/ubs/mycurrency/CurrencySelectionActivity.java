package com.ubs.mycurrency;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ubs.mycurrency.util.Currency;

import java.util.Objects;

public class CurrencySelectionActivity extends AppCompatActivity {

    private Button returnToMainScreen;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //prevents that dark mode makes the UI unusable by stopping it from making all white elements dark

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_selection);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        returnToMainScreen = findViewById(R.id.returnToMainScreen);


        returnToMainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CurrencySelectionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
