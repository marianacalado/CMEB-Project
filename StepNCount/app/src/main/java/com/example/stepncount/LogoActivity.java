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

        boolean isRunning = isServiceRunningInForeground(this,AcquisitionNotification.class);

        if(isRunning)
        {
            (new Handler()).postDelayed(this::goToAct, 500);
        }
        else
        {
            Intent serviceIntent = new Intent(this,AcquisitionService.class);
            serviceIntent.putExtra("Bluetooth", "00:23:FE:00:0B:4E");
            ContextCompat.startForegroundService(this, serviceIntent);

            (new Handler()).postDelayed(this::goToAct, 5000);
        }



    }

    public void goToAct(){
        Intent mainAct = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainAct);

    }

    public static boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }

            }
        }
        return false;
    }

}