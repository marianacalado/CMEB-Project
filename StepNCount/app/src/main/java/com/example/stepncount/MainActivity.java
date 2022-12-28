package com.example.stepncount;


import static com.example.stepncount.ConfigActivity.CONFIG_PREFS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

import Bio.Library.namespace.BioLib;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ------------------------------- Shared preferences ------------------------------- */

        // Configs
        SharedPreferences configPref = getSharedPreferences(CONFIG_PREFS, MODE_PRIVATE);
        boolean firstStart = configPref.getBoolean("firstStart", true); // Second value (true in this case) is always the default value if nothing is saved yet

        if (firstStart) { // Checks if it is the first time the user opens the app
                          // If so, the user sets the Config inputs

            Intent configAct = new Intent(getApplicationContext(), ConfigActivity.class);
            startActivity(configAct);


        } else {
            Intent resultAct = new Intent(getApplicationContext(), ResultsActivity.class);
            startActivity(resultAct);
            startService();
        }

    }

    public void startService() {

        Intent serviceIntent = new Intent(this, AcquisitionService.class);
        //serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, AcquisitionService.class);
        stopService(serviceIntent);
    }



}
