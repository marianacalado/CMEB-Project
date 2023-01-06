package com.example.stepncount;

import static com.example.stepncount.AcquisitionNotification.CHANNEL_ID;
import static com.example.stepncount.ConfigActivity.CONFIG_PREFS;
import static com.example.stepncount.ConfigActivity.WEIGHT;
import static com.example.stepncount.GoalNotification.CHANNEL_ID2;
import static com.example.stepncount.GoalsActivity.CAL_GOAL;
import static com.example.stepncount.GoalsActivity.DIST_GOAL;
import static com.example.stepncount.GoalsActivity.DIST_GOAL_U;
import static com.example.stepncount.GoalsActivity.GOALS_PREFS;
import static com.example.stepncount.GoalsActivity.STEPS_GOAL;
import static com.example.stepncount.GoalsActivity.TIME_GOAL;
import static com.example.stepncount.GoalsActivity.TIME_GOAL_U;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Bio.Library.namespace.BioLib;

public class AcquisitionService extends Service {

    private static final String TAG = "AcquisitionService";

    // Handler and BioLib Variables

    private BioLib lib = null;
    private HandlerThread AcquisitionThread = new HandlerThread("Acquisition");
    private Handler mHandler;

    private String address = "";
    private String mConnectedDeviceName = "";
    private BluetoothDevice deviceToConnect;

    private String accConf = "";
    private int BATTERY_LEVEL = 0;
    private int PULSE = 0;
    private Date DATETIME_PUSH_BUTTON = null;
    private Date DATETIME_RTC = null;
    private Date DATETIME_TIMESPAN = null;
    private int SDCARD_STATE = 0;
    private int numOfPushButton = 0;
    private String deviceId = "";
    private String firmwareVersion = "";
    private byte accSensibility = 1;    // NOTE: 2G= 0, 4G= 1
    private byte typeRadioEvent = 0;
    private byte[] infoRadioEvent = null;
    private short countEvent = 0;
    public static final String DEVICE_NAME = "Vital Jacket";

    // ACC data variables

    private static BioLib.DataACC dataACC = null;
    private static Double[] dadosAnteriores = {0.0,0.0,0.0};
    private static double agregadoMagnitudes = 0.0;
    private static Double kcalTotais;
    private static int notMovingCounter = 0;
    private static int walkingCounter = 0;
    private static int runningCounter = 0;
    private static Integer stepCount = 0;

    // DB

    private dataHelper helper; //dataHelper as our bridge to the database
    private Date currentTime;

    // Config

    private SharedPreferences configPreferences;
    private int Weight;

    // Goals

    private int Steps;
    private int Calories;
    private int Distance;
    private int Time;
    private String TimeU; // U for the string Unit (e.g., h or min)
    private String DistU;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: " + address);

        AcquisitionThread.start();

        @SuppressLint("HandlerLeak")
        Handler mHandler = new Handler(AcquisitionThread.getLooper()) {
            @SuppressLint("MissingPermission")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    case BioLib.MESSAGE_DEVICE_NAME:
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        break;

                    case BioLib.MESSAGE_BLUETOOTH_NOT_SUPPORTED:
                        Toast.makeText(getApplicationContext(), "Bluetooth NOT supported. Aborting! ", Toast.LENGTH_SHORT).show();
                        break;

                    case BioLib.MESSAGE_BLUETOOTH_ENABLED:

                        Toast.makeText(getApplicationContext(), "Bluetooth is now enabled! ", Toast.LENGTH_SHORT).show();
                        break;

                    case BioLib.MESSAGE_BLUETOOTH_NOT_ENABLED:
                        Toast.makeText(getApplicationContext(), "Bluetooth not enabled! ", Toast.LENGTH_SHORT).show();
                        break;

                    case BioLib.STATE_CONNECTING:
                        Toast.makeText(getApplicationContext(), "Connecting... ", Toast.LENGTH_SHORT).show();
                        break;

                    case BioLib.STATE_CONNECTED:
                        Toast.makeText(getApplicationContext(), "Connected to " + deviceToConnect.getName(), Toast.LENGTH_SHORT).show();
                        break;

                    case BioLib.UNABLE_TO_CONNECT_DEVICE:
                        Toast.makeText(getApplicationContext(), "Unable to connect device! ", Toast.LENGTH_SHORT).show();
                        onDestroy(); // Destroy service if connection is lost
                        break;

                    case BioLib.MESSAGE_DISCONNECT_TO_DEVICE:
                        Toast.makeText(getApplicationContext(), "Device connection was lost", Toast.LENGTH_SHORT).show();
                        onDestroy(); // Destroy service if connection is lost
                        break;

                    case BioLib.MESSAGE_DATA_UPDATED:
                        BioLib.Output out = (BioLib.Output)msg.obj;
                        BATTERY_LEVEL = out.battery;
                        PULSE = out.pulse;

                        sendBatteryDataToActivity(BATTERY_LEVEL);
                        break;

                    case BioLib.MESSAGE_FIRMWARE_VERSION:
                        // Show firmware version in device VitalJacket ...
                        firmwareVersion = (String)msg.obj;
                        break;

                    case BioLib.MESSAGE_DEVICE_ID:
                        deviceId = (String)msg.obj;
                        break;

                    case BioLib.MESSAGE_ACC_SENSIBILITY:
                        accSensibility = (byte)msg.arg1;
                        accConf = "4G";
                        switch (accSensibility)
                        {
                            case 0:
                                accConf = "2G";
                                break;

                            case 1:
                                accConf = "4G";
                                break;
                        }
                        break;

                    case BioLib.MESSAGE_PEAK_DETECTION:
                        BioLib.QRS qrs = (BioLib.QRS)msg.obj;
                        break;

                    case BioLib.MESSAGE_ACC_UPDATED:
                        dataACC = (BioLib.DataACC) msg.obj;

                        dataReady();

                        break;

                }
            }
        };

        try {
            lib = new BioLib(getApplicationContext(),mHandler);
            Log.d(TAG, "onCreate: BioLib was initialized successfully!");
        } catch (Exception e) {
            Log.d(TAG, "onCreate: BioLib was not init successfully!");
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Configs

        configPreferences = getSharedPreferences(CONFIG_PREFS, MODE_PRIVATE);

        // Goals

        SharedPreferences goalsPreferences = getSharedPreferences(GOALS_PREFS, MODE_PRIVATE);

        Steps = goalsPreferences.getInt(STEPS_GOAL,10000);
        Calories = goalsPreferences.getInt(CAL_GOAL,685);
        Distance = goalsPreferences.getInt(DIST_GOAL, 8);
        Time = goalsPreferences.getInt(TIME_GOAL,1);
        DistU = goalsPreferences.getString(DIST_GOAL_U, "Km");
        TimeU = goalsPreferences.getString(TIME_GOAL_U,"h");

        // Dataset

        stepCount = 0;
        kcalTotais = 0.0;

        helper = new dataHelper(this);
        Cursor dbData = helper.getAll();

        ArrayList <Integer> steps = new ArrayList<>();
        ArrayList <Double> cal = new ArrayList<>();
        ArrayList <String> date = new ArrayList<>();

        if (dbData.getCount() != 0)
        {
            while (dbData.moveToNext())
            {
                steps.add(dbData.getInt(0));
                cal.add(dbData.getDouble(1));
                date.add(dbData.getString(3));
            }

            for (int i = 0; i < steps.size(); i++) {
                stepCount = stepCount + steps.get(i);
                kcalTotais = kcalTotais + cal.get(i);

            }

        }

        helper.close();

        // Bluetooth

        address = intent.getStringExtra("Bluetooth");
        Connect();

        Intent notificationIntent = new Intent(this, LogoActivity.class); // User is notified with the LogoActivity
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Acquiring data...")
                .setContentText("Counting Steps")
                .setSmallIcon(R.drawable.iconapp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_REDELIVER_INTENT;
    }

    private void Connect() { // Bluetooth connection

        try {

            Log.d(TAG, "Connect: " + address);
            deviceToConnect = lib.mBluetoothAdapter.getRemoteDevice(address);

            lib.Connect(address, 5);
            Log.d(TAG, "Connected!\n ");
        } catch (Exception e) {
            Log.d(TAG, "Failed to connect!\n ");
            e.printStackTrace();
        }
    }


    private void dataReady()
    {
        stepCounter();
        calculateKcal();

        sendDataToActivity(stepCount, kcalTotais);

       //if(stepCount == Steps)
       //{
       //    Log.d(TAG, "dataReady: Steps goal accomplished!");
       //    goalsNotification("Steps",R.drawable.steps_icon);
       //}
       //else if (Math.round(kcalTotais) == Calories)
       //{
       //    goalsNotification("Calories",R.drawable.cal);
       //}
    }

    private void sendDataToActivity(int stepCount, double kcal) {
        Intent intent = new Intent("Update UI");
        Bundle b = new Bundle();
        b.putInt("Steps", stepCount);
        b.putDouble("Kcal", kcal);
        intent.putExtra("AppData", b);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBatteryDataToActivity(int battery) {
        Intent intent = new Intent("Update Battery UI");
        Bundle b = new Bundle();
        b.putInt("Battery", battery);
        intent.putExtra("BatData", b);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void adddata(){ // Add data to dataset
        helper.insert(stepCount, kcalTotais, 0, currentTime.toString() ); //falta por estes argumentos timeT, mudar variavel de dist
    }

    private void goalsNotification(String message, int icon){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID2);
        builder.setContentTitle("Congratulations!");
        builder.setContentTitle("You have accomplished your " + message + " goal!");
        builder.setSmallIcon(icon);
        builder.setAutoCancel(true); // True for swipe.

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(2,builder.build());

    }


    @Override
    public void onDestroy() { // Destroy service
        super.onDestroy();

        if(dataACC != null) {
            adddata();
        }
        Intent searchAct = new Intent(getApplicationContext(), SearchDeviceActivity.class);
        searchAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(searchAct);

        helper.close();

        stopForeground(true);
        stopSelf();
    }

    /* ---------------------------------------------------------- Data processing ----------------------------------------------------------------------*/

    private void stepCounter() {

        Double x = (double) dataACC.X;
        Double y = (double) dataACC.Y;
        Double z = (double) dataACC.Z;

        //Count of steps

        double MagnitudePrevious = Math.sqrt(dadosAnteriores[0] * dadosAnteriores[0] + dadosAnteriores[1] * dadosAnteriores[1] + dadosAnteriores[2] * dadosAnteriores[2]);
        double Magnitude = Math.sqrt(x * x + y * y + z * z);
        double MagnitudeDelta = Magnitude - MagnitudePrevious;
        agregadoMagnitudes += Math.abs(MagnitudeDelta);
        encherDados(x, y, z);

        currentTime = Calendar.getInstance().getTime();
        if (MagnitudeDelta >= 16.5 && MagnitudeDelta < 30) {
            stepCount++;
            walkingCounter++;
            if (walkingCounter == 3) {
                walkingCounter = 0;
            }
        } else if (MagnitudeDelta < 5) {
            notMovingCounter++;
            if (notMovingCounter == 3) {
                notMovingCounter = 0;
            }
        } else if (MagnitudeDelta > 30) {
            stepCount++;
            runningCounter++;
            if (runningCounter == 3) {
                runningCounter = 0;
            }

        }

    }

    private void calculateKcal() {
        Weight = configPreferences.getInt(WEIGHT,70);
        final int fs = 10;
        final int adjustment = fs * 60;
        kcalTotais = ((0.001064 * agregadoMagnitudes + 0.087512 * Weight - 5.500229)/adjustment);
        // Equation for every min so its adjusted
    }

    private void encherDados(Double x, Double y, Double z){
        dadosAnteriores[0] = x;
        dadosAnteriores[1] = y;
        dadosAnteriores[2] = z;
    }
}


