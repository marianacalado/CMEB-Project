package com.example.stepncount;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class GoalNotification extends Application {
    public static final String CHANNEL_ID2 = "GoalNotification";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel achievementChannel = new NotificationChannel(
                    CHANNEL_ID2, "Goal",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(achievementChannel);
        }
    }
}