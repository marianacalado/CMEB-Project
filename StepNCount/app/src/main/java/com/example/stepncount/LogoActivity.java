package com.example.stepncount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LogoActivity extends AppCompatActivity {
    //View variables
    private ImageView logo;
    private TextView slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        logo = findViewById(R.id.imgLo);
        slogan = findViewById(R.id.slogan);

        startService(); //dentro daqui ve se esta ou nao conectado
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