package com.example.stepncount;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.Set;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import Bio.Library.namespace.BioLib;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import org.w3c.dom.Text;


public class Connect extends Activity {
    private BioLib lib = null;

    private String address = "00:23:FE:00:0B:4A";
    private String mConnectedDeviceName = "";
    private BluetoothDevice deviceToConnect;
    private ArrayList<Double> dadosDefault = new ArrayList<>();
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

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
                    /*Intent menus = new Intent(quarto.this, terceiro.class);
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
                        handleACC(dadosDefault);
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
                    fillDefaultArray();
                    lib.Connect(address, 5);
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
    public ArrayList<Double> handleACC(ArrayList<Double> dadosAnteriores) throws Exception {
        String dataX = "X coord: "+dataACC.X;
        String dataY = "Y coord: "+dataACC.Y;
        String dataZ = "Z coord: "+dataACC.Z;

        TextView teste = (TextView) findViewById(R.id.testeData);
        teste.setText(dataX+"\n"+dataY+"\n"+dataZ);
        TextView teste2 = findViewById(R.id.broNaoSei);
        teste2.setText("Done");

        ArrayList<Double> testeArray = new ArrayList<>();
        testeArray.add(Double.valueOf(dataX));
        testeArray.add(Double.valueOf(dataY));
        testeArray.add(Double.valueOf(dataZ));

        //double kcalMin = 0.001064 + Math.sqrt(Math.pow(dataXConta,2)+Math.pow(dataYConta,2)+Math.pow(dataZConta,2))
        //+ 0.087512 * 75 - 5.500229;

        double magnitudeDelta = Math.sqrt(Math.pow(dadosAnteriores.get(0),2)+Math.pow(dadosAnteriores.get(1), 2)+Math.pow(dadosAnteriores.get(2),2)) -
                Math.sqrt(Math.pow(testeArray.get(0),2)+Math.pow(testeArray.get(1),2)+Math.pow(testeArray.get(2),2));

        TextView stepsC = findViewById(R.id.stepsCount);

        stepsC.setText((int) magnitudeDelta);

        buttonDisconnect = (Button) findViewById(R.id.buttonDisplayData);
        buttonDisconnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                displayDados();
            }
            private void displayDados(){
                TextView displayDados = (TextView) findViewById(R.id.displayDados);
                displayDados.setText(testeArray.toString());
            }
        });
        return testeArray;
    }

    private void fillDefaultArray(){
        dadosDefault.add(0.0);
        dadosDefault.add(0.0);
        dadosDefault.add(0.0);
    }
}