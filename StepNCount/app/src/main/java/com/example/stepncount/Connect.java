package com.example.stepncount;


import static com.example.stepncount.ConfigActivity.CONFIG_PREFS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Bio.Library.namespace.BioLib;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;


public class Connect extends Activity {
    public static final String CONNECT_PREFS = "connectPrefs";
    private int notMovingCounter = 0;
    private int walkingCounter = 0;
    private int runningCounter = 0;
    private BioLib lib = null;
    private int testBoasOla = 0;
    private String address = "00:23:FE:00:0B:54";
    private String mConnectedDeviceName = "";
    private BluetoothDevice deviceToConnect;
    private Double[] dadosAnteriores = {0.0,0.0,0.0};
    private double agregadoMagnitudes = 0.0;
    private Double kcalTotais;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private Integer stepCount = 0;
    private TextView text;
    private TextView textRTC;
    private TextView textPUSH;
    private TextView textPULSE;
    private TextView textBAT;
    private TextView textDataReceived;
    private TextView textSDCARD;
    private TextView textACC;
    private TextView textHR;
    private TextView textECG;
    private TextView textDeviceId;
    private TextView textRadioEvent;
    private TextView textTimeSpan;

    private Button buttonConnect;
    private Button buttonDisconnect;
    private Button buttonGetRTC;
    private Button buttonSetRTC;
    private Button buttonRequest;
    private Button buttonSearch;
    private Button buttonSetLabel;
    private Button buttonGetDeviceId;
    private Button buttonGetAcc;
    private Button buttonDisplay;

    private BioLib.DataACC dataACC = null;
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
    private boolean isConn = false;


    private byte[][] ecg = null;
    private int nBytes = 0;

    private String accConf = "";
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BioLib.STATE_CONNECTING:
                    Toast.makeText(getApplicationContext(), "Connecting to device ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.STATE_CONNECTED:
                    if (ActivityCompat.checkSelfPermission(Connect.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Connected to " + deviceToConnect.getName(), Toast.LENGTH_SHORT).show();
                    isConn = true;

                    /* por aqui a comunicação vai para config
                    Intent menus = new Intent(quarto.this, terceiro.class);
                    menus.putExtra("lib", (Parcelable) lib);
                    startActivity(menus);
                    setContentView(R.layout.menu);
                    Button Treino;
                    Treino = findViewById(R.id.inic_treino);
                    Treino.setOnClickListener(ConnectReceive.this::onClick);
                    stati = findViewById(R.id.stats);
                    stati.setOnClickListener(ConnectReceive.this::onClick);*/

                    break;

                case BioLib.UNABLE_TO_CONNECT_DEVICE:
                    Toast.makeText(getApplicationContext(), "Unable to connect device! ", Toast.LENGTH_SHORT).show();
                    isConn = false;
                    break;

                case BioLib.MESSAGE_DISCONNECT_TO_DEVICE:
                    Toast.makeText(getApplicationContext(), "Device connection was lost", Toast.LENGTH_SHORT).show();
                    isConn = false;
                    break;

                case BioLib.MESSAGE_ACC_SENSIBILITY:
                    accSensibility = (byte) msg.arg1;
                    accConf = "4G";
                    switch (accSensibility) {
                        case 0:
                            accConf = "2G";
                            break;

                        case 1:
                            accConf = "4G";
                            break;
                    }

                    //textACC.setText("ACC [" + accConf + "]:  X: " + dataACC.X + "  Y: " + dataACC.Y + "  Z: " + dataACC.Z);
                    break;

                case BioLib.MESSAGE_PEAK_DETECTION:
                    BioLib.QRS qrs = (BioLib.QRS) msg.obj;
                    break;

                case BioLib.MESSAGE_ACC_UPDATED:
                    dataACC = (BioLib.DataACC) msg.obj;
                    //System.out.println("Nova leitura");
                    try {
                        stepCounter();
                        //handleACC(dadosDefault);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   /* if (accConf == "")
                        //textACC.setText("ACC:  X: " + dataACC.X + "  Y: " + dataACC.Y + "  Z: " + dataACC.Z);
                    else
                        //textACC.setText("ACC [" + accConf + "]:  X: " + dataACC.X + "  Y: " + dataACC.Y + "  Z: " + dataACC.Z);*/
                    break;

                case BioLib.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    /**
     * @return
     */



    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        //aqui vai correr o handler
        try{
            lib = new BioLib(this,mHandler);
        }catch (Exception e) {
            e.printStackTrace();
        }

        buttonConnect = (Button) findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {

                Connect();
            }

            /***
             * Connect to device.
             */
            private void Connect()
            {
                try
                {
                    deviceToConnect =  lib.mBluetoothAdapter.getRemoteDevice(address);
                    Toast.makeText(Connect.this, "Keep going12!", Toast.LENGTH_SHORT).show();
                    Reset();
                    lib.Connect(address, 5);
                    SharedPreferences connectPref = getSharedPreferences(CONNECT_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = connectPref.edit();
                    boolean isConnect = connectPref.getBoolean("isConn",false);
                    //Intent intent = new Intent(this, DataIntentService.class);
                    //startService(intent);
                    //Mudar activity dps de connect
                    /*
                    if (firstStart) { // Checks if it is the first time the user opens the app
                        Intent configAct = new Intent(getApplicationContext(), ConfigActivity.class);
                        startActivity(configAct);

                    } else {
                        Intent resultAct = new Intent(getApplicationContext(), ResultsActivity.class);
                        startActivity(resultAct);
                    }
                     */
                } catch (Exception e)
                {
                    text.setText("Error to connect device: " + address);
                    e.printStackTrace();
                }
            }


        });
        buttonDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        buttonDisconnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Disconnect();
            }
            private void Disconnect(){
                try
                {
                    lib.Disconnect();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Reset();
                }
            }
        });

    }

    private void Reset() {
        try {

            // textACC.setText("ACC:  X: - -  Y: - -  Z: - -");

            accConf = "";

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void stepCounter()
    {


        String X_acc = "X coord: "+dataACC.X;
        String Y_acc = "Y coord: "+dataACC.Y;
        String Z_acc = "Z coord: "+dataACC.Z;

        //conversion to double value
        Double x = Double.valueOf(dataACC.X);
        Double y = Double.valueOf(dataACC.Y);
        Double z = Double.valueOf(dataACC.Z);

        //display of data that VJ aquired
        TextView  test = findViewById(R.id.datatest);
        test.setText(X_acc+"\n"+Y_acc+"\n"+Z_acc);

        //if the conection worked
        TextView teste2 = findViewById(R.id.conection_);
        teste2.setText("Done");


        //Count of steps



        //test a ver se faz bem a contagem
        TextView stepsC = findViewById(R.id.stepshow);
        double MagnitudePrevious = Math.sqrt(dadosAnteriores[0]*dadosAnteriores[0] + dadosAnteriores[1]*dadosAnteriores[1] + dadosAnteriores[2]*dadosAnteriores[2]);
        double Magnitude = Math.sqrt(x*x + y*y + z*z);
        double MagnitudeDelta = Magnitude - MagnitudePrevious;
        agregadoMagnitudes+= Math.abs(MagnitudeDelta);
        encherDados(x,y,z);

        Date currentTime = Calendar.getInstance().getTime();
        TextView boas = findViewById(R.id.teste);
        boas.setText(currentTime.toString());
        TextView status = findViewById(R.id.status);
        if (MagnitudeDelta >= 16.5 && MagnitudeDelta < 30){
            stepCount++;
            walkingCounter++;
            if(walkingCounter == 3){
                status.setText("Walking");
                walkingCounter = 0;
            }
        }else if(MagnitudeDelta < 5){
            notMovingCounter++;
            if(notMovingCounter == 3){
                status.setText("Not moving");
                notMovingCounter = 0;
            }
        }else if(MagnitudeDelta > 30){
            stepCount++;
            runningCounter++;
            if(runningCounter == 3){
                runningCounter = 0;
                status.setText("Running");
            }

        }
        stepsC.setText(Integer.toString(stepCount));

        //if the user clicks on the button display it show the magnitude calculation
        buttonDisplay  = findViewById(R.id.buttonDisplayData);
        Integer finalStepCount = stepCount;
        buttonDisplay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
              displayDados();
            }
            private void displayDados(){
                //TextView mag = findViewById(R.id.data);
                stepsC.setText(finalStepCount.toString());
            }
        });


        //EE calculation
        calculateKcal();
    }

    private void calculateKcal() {
            kcalTotais = (0.001064 + agregadoMagnitudes
                    + 0.087512 * 75 - 5.500229)/2500;

            TextView kcal = findViewById(R.id.Kcal);
            kcal.setText(String.valueOf(kcalTotais));
        }



    private void encherDados(Double x, Double y, Double z){
        dadosAnteriores[0] = x;
        dadosAnteriores[1] = y;
        dadosAnteriores[2] = z;
    }
/*
    public Double[] handleACC(Double[] dadosAnteriores) throws Exception
    {

        String X_acc = "X coord: "+dataACC.X;
        String Y_acc = "Y coord: "+dataACC.Y;
        String Z_acc = "Z coord: "+dataACC.Z;

        //array de valores anteriores
        ArrayList<Double> testeArray = new ArrayList<>();
        testeArray.add(Double.valueOf(X_acc));
        testeArray.add(Double.valueOf(Y_acc));
        testeArray.add(Double.valueOf(Z_acc));

        //display of data that VJ aquired
        TextView  test = findViewById(R.id.datatest);
        test.setText(X_acc+"\n"+Y_acc+"\n"+Z_acc);

        //if the conection worked
        TextView teste2 = findViewById(R.id.conection_);
        teste2.setText("Done");

        //Count of steps


        //test a ver se faz bem a contagem
        TextView stepsC = findViewById(R.id.stepshow);

        double MagnitudePrevious = Math.sqrt(Math.pow(dadosAnteriores.get(0),2)+Math.pow(dadosAnteriores.get(1), 2)+Math.pow(dadosAnteriores.get(2),2));
        double Magnitude = Math.sqrt(Math.pow(testeArray.get(0),2)+Math.pow(testeArray.get(1),2)+Math.pow(testeArray.get(2),2));
        double MagnitudeDelta =  Magnitude - MagnitudePrevious;
        MagnitudePrevious = Magnitude;

        if (MagnitudeDelta > 3){
            stepCount++;
        }
        stepsC.setText(stepCount.toString());

        //if the user clicks on the button display it show the magnitude calculation
        buttonDisplay  = findViewById(R.id.buttonDisplayData);
        //Integer finalStepCount = stepCount;
        Integer finalStepCount = stepCount;
        buttonDisplay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                displayDados();
            }
            private void displayDados(){
               //TextView mag = findViewById(R.id.data);
                stepsC.setText(finalStepCount.toString());
            }
        });

        return testeArray;


        //EE calculation
        //double kcalMin = 0.001064 + Math.sqrt(Math.pow(dataXConta,2)+Math.pow(dataYConta,2)+Math.pow(dataZConta,2))
        //+ 0.087512 * 75 - 5.500229;
    }

//    public ArrayList<Double> handleACC(ArrayList<Double> dadosAnteriores) throws Exception {
//        String X_acc = "X coord: "+dataACC.X;
//        String Y_acc = "Y coord: "+dataACC.Y;
//        String Z_acc = "Z coord: "+dataACC.Z;
//
//        //display of data that VJ aquired
//        TextView  test= findViewById(R.id.datatest);
//        test.setText(X_acc+"\n"+Y_acc+"\n"+Z_acc);
//
//        //if the conection worked
//        TextView teste2 = findViewById(R.id.conection_);
//        teste2.setText("Done");
//
//        //array de valores anteriores
//        ArrayList<Double> testeArray = new ArrayList<>();
//        testeArray.add(Double.valueOf(X_acc));
//        testeArray.add(Double.valueOf(Y_acc));
//        testeArray.add(Double.valueOf(Z_acc));
//
//        //EE calculation
//        //double kcalMin = 0.001064 + Math.sqrt(Math.pow(dataXConta,2)+Math.pow(dataYConta,2)+Math.pow(dataZConta,2))
//        //+ 0.087512 * 75 - 5.500229;
//
//        //Count of steps
//        double MagnitudePrevious = 0;
//        Integer stepCount = 0;
//
//        //test a ver se faz bem a contagem
//        TextView stepsC = findViewById(R.id.stepshow);
//
//
//        double Magnitude = Math.sqrt(X_acc*X_acc + Y_acc*Y_acc + Z_acc*Z_acc);
//        double MagnitudeDelta = Magnitude – MagnitudePrevious;
//        MagnitudePrevious = Magnitude;
//
//        if (MagnitudeDelta > 6){
//            stepCount++;
//        }
//        stepsC.setText(stepCount.toString());
//        }
//
//
//        //double magnitudeDelta = Math.sqrt(Math.pow(dadosAnteriores.get(0),2)+Math.pow(dadosAnteriores.get(1), 2)+Math.pow(dadosAnteriores.get(2),2)) -
//        //        Math.sqrt(Math.pow(testeArray.get(0),2)+Math.pow(testeArray.get(1),2)+Math.pow(testeArray.get(2),2));
//
//
//        //stepsC.setText((int) magnitudeDelta);
//
//        buttonDisplay  = findViewById(R.id.buttonDisplayData);
//        buttonDisplay.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View view){
//                displayDados();
//            }
//            private void displayDados(){
//                TextView displayDados = findViewById(R.id.data);
//                displayDados.setText(testeArray.toString());
//            }
//        });
//        return testeArray;
//    }
//
 */
}

