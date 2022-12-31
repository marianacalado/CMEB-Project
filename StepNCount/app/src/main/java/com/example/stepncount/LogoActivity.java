package com.example.stepncount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        (new Handler()).postDelayed(this::goToAct, 3000);
    }

    public void goToAct(){
        Intent mainAct = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainAct);
    }



}