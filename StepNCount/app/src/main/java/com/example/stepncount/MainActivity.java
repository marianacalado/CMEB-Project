package com.example.stepncount;


import static com.example.stepncount.ConfigActivity.CONFIG_PREFS;

import androidx.appcompat.app.AppCompatActivity;
<<<<<<< HEAD

import android.annotation.SuppressLint;
=======
import androidx.core.content.ContextCompat;

>>>>>>> 5f3e6938e58f84188359f84a744ab28361098688
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
<<<<<<< HEAD
import android.widget.Button;
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


    private Button buttonConnect;


    @SuppressLint("MissingInflatedId")
=======

import java.util.Calendar;
import java.util.Date;

import Bio.Library.namespace.BioLib;

public class MainActivity extends AppCompatActivity {

>>>>>>> 5f3e6938e58f84188359f84a744ab28361098688
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent ghost = new Intent(getApplicationContext(), ConfigActivity.class);
//        //Intent Con = new Intent(getApplicationContext(), Connect.class);
//        startActivity(ghost);

<<<<<<< HEAD
        buttonConnect = (Button) findViewById(R.id.buttonCon);
        buttonConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent cf = new Intent(getApplicationContext(), ConfigActivity.class);
                //Intent Con = new Intent(getApplicationContext(), Connect.class);
                startActivity(cf);
            }
        });






        //SharedPreferences configPref = getSharedPreferences(CONFIG_PREFS, MODE_PRIVATE);
        // Second value (true in this case) is always the default value if nothing is saved yet

=======
        /* ------------------------------- Shared preferences ------------------------------- */
>>>>>>> 5f3e6938e58f84188359f84a744ab28361098688


<<<<<<< HEAD

=======
        if (firstStart) { // Checks if it is the first time the user opens the app
                          // If so, the user sets the Config inputs

            Intent configAct = new Intent(getApplicationContext(), ConfigActivity.class);
            startActivity(configAct);


        } else {
            Intent resultAct = new Intent(getApplicationContext(), ResultsActivity.class);
            startActivity(resultAct);
            startService();
>>>>>>> 5f3e6938e58f84188359f84a744ab28361098688
        }


    }
<<<<<<< HEAD
=======

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
>>>>>>> 5f3e6938e58f84188359f84a744ab28361098688
