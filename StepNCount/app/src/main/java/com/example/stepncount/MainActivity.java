package com.example.stepncount;


import static com.example.stepncount.ConfigActivity.CONFIG_PREFS;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private ProgressBar stepsBar;
    private ProgressBar calsBar;
    private ProgressBar distBar;
    private ProgressBar timeBar;

    private int stepGoal;
    private int calGoal;
    private int distGoal;
    private int timeGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent Con = new Intent(getApplicationContext(), Connect.class);
        //startActivity(Con);

        /* ------------------------------- Shared preferences ------------------------------- */

        // Configs
        SharedPreferences configPref = getSharedPreferences(CONFIG_PREFS, MODE_PRIVATE);
        boolean firstStart = configPref.getBoolean("firstStart", true); // Second value (true in this case) is always the default value if nothing is saved yet

        if (firstStart) { // Checks if it is the first time the user opens the app
            Intent configAct = new Intent(getApplicationContext(), ConfigActivity.class);
            startActivity(configAct);

        } else {
            Intent resultAct = new Intent(getApplicationContext(), ResultsActivity.class);
            startActivity(resultAct);
        }

    }
}
