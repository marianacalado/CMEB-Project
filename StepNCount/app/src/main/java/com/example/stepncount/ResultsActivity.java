package com.example.stepncount;

import static com.example.stepncount.GoalsActivity.CAL_GOAL;
import static com.example.stepncount.GoalsActivity.DIST_GOAL;
import static com.example.stepncount.GoalsActivity.GOALS_PREFS;
import static com.example.stepncount.GoalsActivity.STEPS_GOAL;
import static com.example.stepncount.GoalsActivity.TIME_GOAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;



import android.Manifest;
import android.annotation.SuppressLint;
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

import Bio.Library.namespace.BioLib;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class ResultsActivity extends AppCompatActivity {

    private ProgressBar stepsBar;
    private ProgressBar calsBar;
    private ProgressBar distBar;
    private ProgressBar timeBar;

    private int stepGoal;
    private int calGoal;
    private int distGoal;
    private int timeGoal;

    //database
    dataHelper helper; //dataHelper as our bridge to the database
    float dayStep;
    int dayCal;
    float dayDist;
    float dayTime;
    Date currentTime;


    //conect ca dentro futuros nos
    public static final String CONNECT_PREFS = "connectPrefs";
    private int notMovingCounter = 0;
    private int walkingCounter = 0;
    private int runningCounter = 0;
    private BioLib lib = null;
    private int testBoasOla = 0;
    private String address = "00:23:FE:00:0B:4D";
    private String mConnectedDeviceName = "";
    private BluetoothDevice deviceToConnect;
    private Double[] dadosAnteriores = {0.0,0.0,0.0};
    private double agregadoMagnitudes = 0.0;
    private double kcalTotais;
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

    private TextView stepsT;
    private TextView calT;
    private TextView distT;
    private TextView timeT;

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
                    //Toast.makeText(getApplicationContext(), "Connecting to device ", Toast.LENGTH_SHORT).show();
                    break;

                case BioLib.STATE_CONNECTED:
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    //Toast.makeText(getApplicationContext(), "Connected to " + deviceToConnect.getName(), Toast.LENGTH_SHORT).show();
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
                    //Toast.makeText(getApplicationContext(), "Device connection was lost", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        helper = new dataHelper(this); //instaciar

        //connect
        try{
            lib = new BioLib(this,mHandler);
        }catch (Exception e) {
            e.printStackTrace();
        }


        //buttonConnect = (Button) findViewById(R.id.buttonCon);
        //buttonConnect.setOnClickListener(new View.OnClickListener(){
        //    public void onClick(View view)
        //    {
//
        //        Connect();
        //    }

    /***
     * Connect to device.
     *
     */
        Connect();

        // Goals
        SharedPreferences goalPref   = getSharedPreferences(GOALS_PREFS, MODE_PRIVATE);

        stepGoal = goalPref.getInt(STEPS_GOAL,10000);
        calGoal = goalPref.getInt(CAL_GOAL, 500);
        distGoal = goalPref.getInt(DIST_GOAL, 8);
        timeGoal = goalPref.getInt(TIME_GOAL, 1);

        // Importing color scheme from Resource Files: res/values/colors.xml

        int StepsDataPointsColor = ContextCompat.getColor(this, R.color.DataPointVal);
        int ProgBarColor         = ContextCompat.getColor(this,R.color.MainColor);

        /* ------------------------------- ProgressBar Views ------------------------------- */

        stepsBar = findViewById(R.id.progBarSteps);
        calsBar = findViewById(R.id.progBarCal);
        distBar = findViewById(R.id.progBarDist);
        timeBar = findViewById(R.id.progBarTime);

        /* ------------------------------- Week data ------------------------------- */

        // Steps

        ArrayList<Entry> steps = new ArrayList<>();

        steps.add(new Entry(0, 200));
        steps.add(new Entry(1, 3000));
        steps.add(new Entry(2, 1000));
        steps.add(new Entry(3, 400));
        steps.add(new Entry(4, 40));
        steps.add(new Entry(5, 5000));
        steps.add(new Entry(6, 0));

        // Calories

        ArrayList<Entry> calories = new ArrayList<>();

        calories.add(new Entry(0, 26));
        calories.add(new Entry(1, 350));
        calories.add(new Entry(2, 135));
        calories.add(new Entry(3, 45));
        calories.add(new Entry(4, 4));
        calories.add(new Entry(5, 798));
        calories.add(new Entry(6, 0));

        // Distance

        ArrayList<Entry> distance = new ArrayList<>();

        distance.add(new Entry(0, (float) 0.4));
        distance.add(new Entry(1, (float) 2.7));
        distance.add(new Entry(2, (float) 1.40));
        distance.add(new Entry(3, (float) 0.8));
        distance.add(new Entry(4, (float) 0.11));
        distance.add(new Entry(5, (float) 5.8));
        distance.add(new Entry(6, (float) 0));

        // Time

        ArrayList<Entry> time = new ArrayList<>();

        time.add(new Entry(0, 2));
        time.add(new Entry(1, 3));
        time.add(new Entry(2, 1));
        time.add(new Entry(3, 4));
        time.add(new Entry(4, 4));
        time.add(new Entry(5, 5));
        time.add(new Entry(6, 0));

        /* ------------------------------- Text Views ------------------------------- */

        stepsT = (TextView) findViewById(R.id.stepsTxt);
        calT   = (TextView) findViewById(R.id.calTxt);
        distT  = (TextView) findViewById(R.id.distTxt);
        timeT  = (TextView) findViewById(R.id.timeTxt);

        // Setting TextViews and Progress with the latest day values

        int arraySz = steps.size()-1;

        dayStep = steps.get(arraySz).getY();
        dayCal  = (int)calories.get(arraySz).getY();
        dayDist = distance.get(arraySz).getY();
        dayTime = time.get(arraySz).getY();

        stepsT.setText(String.valueOf(dayStep));
        calT.setText(String.valueOf(dayCal));
        distT.setText(String.valueOf( dayDist));
        timeT.setText(String.valueOf(dayTime));

        updateBarProgress(dayStep,dayCal,dayDist,dayTime);

        //adddata();

        /* ------------------------------- Weekly Chart ------------------------------- */

        LineChart chart = (LineChart) findViewById(R.id.graph);

        GraphDisplay graph = new GraphDisplay(this);
        LineDataSet weekSteps = graph.chartSetUp(chart,steps,StepsDataPointsColor,ProgBarColor);
        graph.graphFade(weekSteps,R.drawable.fade_red, StepsDataPointsColor);


        // Click listener for the value selected on the graph

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float dayStep = e.getY();

                int idx = 0;
                for (int i = 0; i <= arraySz; i++) {
                    if (dayStep == steps.get(i).getY()) {
                        idx = i;
                    }
                }

                int dayCal  = (int)calories.get(idx).getY();
                float dayDist = distance.get(idx).getY();
                float dayTime = time.get(idx).getY();

                stepsT.setText(String.valueOf((int) dayStep));
                calT.setText(String.valueOf((int)dayCal));
                distT.setText(String.valueOf(dayDist));
                timeT.setText(String.valueOf(dayTime));

                updateBarProgress(dayStep,dayCal,dayDist,dayTime);

            }

            @Override
            public void onNothingSelected() {

            }
        });

        /* ------------------------------- Goal Button ------------------------------- */

        Button goalsButton = (Button) findViewById(R.id.goalsBtn);
        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGoals = new Intent(getApplicationContext(), GoalsActivity.class);
                startActivity(openGoals);
            }
        });

        /* ------------------------------- Config Button ------------------------------- */

        Button configButton = (Button) findViewById(R.id.configBtn);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openConfig = new Intent(getApplicationContext(), ConfigActivity.class);
                startActivity(openConfig);

            }
        });

        /* ------------------------------- Expand Button ------------------------------- */

        Button expandBtn = (Button) findViewById(R.id.expandBtn);
        expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent opengraphPage = new Intent(getApplicationContext(), GraphPage.class);
                startActivity(opengraphPage);

            }
        });

    }


    public void updateBarProgress(float dayStep, int dayCal, float dayDist, float dayTime){

        // Updates ProgressBars based on previously set goals

        stepsBar.setProgress(Math.round(dayStep/stepGoal * 100));
        calsBar.setProgress(dayCal/calGoal * 100);
        distBar.setProgress(Math.round(dayDist/distGoal * 100));
        timeBar.setProgress(Math.round(dayTime/timeGoal * 100));

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

        //Count of steps

        //test a ver se faz bem a contagem
        TextView stepsC = findViewById(R.id.stepshow);
        double MagnitudePrevious = Math.sqrt(dadosAnteriores[0]*dadosAnteriores[0] + dadosAnteriores[1]*dadosAnteriores[1] + dadosAnteriores[2]*dadosAnteriores[2]);
        double Magnitude = Math.sqrt(x*x + y*y + z*z);
        double MagnitudeDelta = Magnitude - MagnitudePrevious;
        agregadoMagnitudes+= Math.abs(MagnitudeDelta);
        encherDados(x,y,z);

        currentTime= Calendar.getInstance().getTime();
        //TextView boas = findViewById(R.id.teste);
        //boas.setText(currentTime.toString());
        //TextView status = findViewById(R.id.status);
        if (MagnitudeDelta >= 16.5 && MagnitudeDelta < 30){
            stepCount++;
            walkingCounter++;
            adddata();
            if(walkingCounter == 3){
                //status.setText("Walking");
                walkingCounter = 0;
            }
        }else if(MagnitudeDelta < 5){
            notMovingCounter++;
            if(notMovingCounter == 3){
                //status.setText("Not moving");
                notMovingCounter = 0;
            }
        }else if(MagnitudeDelta > 30){
            stepCount++;
            runningCounter++;
            adddata();
            if(runningCounter == 3){
                runningCounter = 0;
                //status.setText("Running");
            }

        }
        //stepsC.setText(Integer.toString(stepCount));

        //if the user clicks on the button display it show the magnitude calculation
       //buttonDisplay  = findViewById(R.id.buttonDisplayData);
       //Integer finalStepCount = stepCount;
       //buttonDisplay.setOnClickListener(new View.OnClickListener(){
       //    public void onClick(View view){
       //        displayDados();
       //    }
       //    private void displayDados(){
       //        //TextView mag = findViewById(R.id.data);
       //        stepsC.setText(finalStepCount.toString());
       //    }
       //});

        //EE calculation
        calculateKcal();
        calT.setText((int) kcalTotais+ " KCal");
        stepsT.setText(stepCount.toString());

        updateBarProgress(stepCount,(int)kcalTotais,0,0);
    }

    private void calculateKcal() {
        kcalTotais = (0.001064 + agregadoMagnitudes
                + 0.087512 * 75 - 5.500229)/2500;


    }

    private void encherDados(Double x, Double y, Double z){
        dadosAnteriores[0] = x;
        dadosAnteriores[1] = y;
        dadosAnteriores[2] = z;
    }

    private void Reset() {
        try {

            // textACC.setText("ACC:  X: - -  Y: - -  Z: - -");

            accConf = "";

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Connect()
    {
        try
        {
            deviceToConnect =  lib.mBluetoothAdapter.getRemoteDevice(address);
            //Toast.makeText(getApplicationContext(), "Keep going12!", Toast.LENGTH_SHORT).show();
            Reset();
            lib.Connect(address, 5);
            SharedPreferences connectPref = getSharedPreferences(CONNECT_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = connectPref.edit();
            //isConn = true;
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


    /* ------------------------------- database ------------------------------- */
    public void adddata(){
        helper.insert(stepCount, (int) kcalTotais, dayDist, currentTime.toString() ); //falta por estes argumentos timeT, mudar variavel de dist
    }

    @Override //fecha DB
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }
}