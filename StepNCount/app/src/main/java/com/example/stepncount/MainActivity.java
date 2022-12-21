package com.example.stepncount;


import static com.example.stepncount.ConfigActivity.CONFIG_PREFS;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent ghost = new Intent(getApplicationContext(), ConfigActivity.class);
//        //Intent Con = new Intent(getApplicationContext(), Connect.class);
//        startActivity(ghost);

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




        }


    }
