package com.example.stepncount;

import static com.example.stepncount.ConfigActivity.CONFIG_PREFS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configs
        SharedPreferences configPref = getSharedPreferences(CONFIG_PREFS, MODE_PRIVATE);
        boolean firstStart = configPref.getBoolean("firstStart", true); // Second value (true in this case) is always the default value if nothing is saved yet

        if (firstStart) {
            // Checks if it is the first time the user opens the app
            // If so, the user sets the Config inputs

            Intent configAct = new Intent(getApplicationContext(), ConfigActivity.class);
            startActivity(configAct);
        } else {
            Intent resultAct = new Intent(getApplicationContext(), ResultsActivity.class);
            startActivity(resultAct);
        }


    }




}
