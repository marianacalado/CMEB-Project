package com.example.stepncount;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class Bluetooth extends AppCompatActivity {
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    //Edifier R2000DB é para teste, trocar pelo ID do vitaljacket dps
    private static final String VITALJACKETNAME = "EDIFIER R2000DB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        testingBluetooth();

    }

    public void testingBluetooth() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(Bluetooth.this, "NO SUPPORT", Toast.LENGTH_LONG).show();
        }
        ActivityResultLauncher<Intent> getResult =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    Intent data = result.getData();

                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(Bluetooth.this, BLE_PERMISSIONS, 1234);

                                    }
                                    Toast.makeText(Bluetooth.this, "Keep going!", Toast.LENGTH_LONG).show();
                                    Set<BluetoothDevice> btDevices = bluetoothAdapter.getBondedDevices();
                                    ArrayList<String> deviceNames = new ArrayList<>();
                                    ArrayList<String> deviceIds = new ArrayList<>();
                                    String nomesTodos = "Devices:";

                                    for(BluetoothDevice device : btDevices){
                                        deviceNames.add(device.getName());
                                        deviceIds.add(device.getAddress());
                                        nomesTodos += " " + device.getName();
                                    }
                                    TextView deviceNamesText = (TextView) findViewById(R.id.testeDeviceNames);
                                    TextView specificDeviceId = (TextView) findViewById(R.id.specificDeviceId);

                                    //Trocar variável global VITALJACKETNAME
                                    String vitalJacketAddress = "";
                                    try {
                                         vitalJacketAddress = deviceIds.get(deviceNames.indexOf(VITALJACKETNAME));

                                    }catch (Exception e){
                                        vitalJacketAddress = "A device with that name was not found...";
                                    }

                                    if(!deviceIds.equals("MAC Addresses:")) {
                                        deviceNamesText.setText(nomesTodos);
                                    }else{
                                        deviceNamesText.setText("No addresses found...");
                                    }

                                        specificDeviceId.setText("Specific address: " + vitalJacketAddress);


                                }else{
                                    Toast.makeText(Bluetooth.this, "ACTIVITY NOT OK",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
        {



            //Old Way
            //if (!bluetoothAdapter.isEnabled()) {
            //  Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            //}
            getResult.launch(btIntent);
        }
    }
}