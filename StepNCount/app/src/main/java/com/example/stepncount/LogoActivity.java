package com.example.stepncount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        startService();

        (new Handler()).postDelayed(this::goToMain, 5000);



    }

    public void goToMain(){
        Intent mainAct = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainAct);

    }

    public void startService() {

        Intent serviceIntent = new Intent(this, AcquisitionService.class);
        //serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

}