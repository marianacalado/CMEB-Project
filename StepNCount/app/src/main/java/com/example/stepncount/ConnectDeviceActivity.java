package com.example.stepncount;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class ConnectDeviceActivity extends AppCompatActivity {


    private BluetoothAdapter p_bluetoothAdapter;
    private Button p_access_btn_bluetooth;
    private Button p_btn_discover_device;
    private ListView p_list_device;


    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);

        p_access_btn_bluetooth = (Button) findViewById(R.id.btn_access_bluetooth);
        p_btn_discover_device = (Button) findViewById(R.id.btn_discover_device);
        p_list_device = (ListView) findViewById(R.id.lv_list_devices);


        p_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (p_bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth non disponible sur cette appareil", Toast.LENGTH_SHORT).show();
        }


        if (getApplicationContext().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            Log.e("[MESSAGE]", "ACCES_COARSE_LOCATION GRANTED");
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            Log.e("[MESSAGE]","ACCES_COARSE_LOCATION NOT GRANTED");

        }


        /** Textualisation du bouton p_acces_btn_bluetooth*/

        if (!p_bluetoothAdapter.isEnabled()) {
            p_access_btn_bluetooth.setText("ACTIVER BLUETOOTH");
        } else {
            p_access_btn_bluetooth.setText("DESACTIVER BLUETOOTH");
        }

        /** ETAPE 2
         * Vérification de l'etat d'activation de l'appareil, Si le bluetooth n'est pas activé alors nous l'activons (resp) et changeons le texte du bouton -- */

        p_access_btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btn_logique_text = (Button) p_access_btn_bluetooth;
                String buttonText = btn_logique_text.getText().toString();

                if (!p_bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
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
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    p_access_btn_bluetooth.setText("DESACTIVER BLUETOOTH");
                } else {
                    p_bluetoothAdapter.disable();
                    p_access_btn_bluetooth.setText("ACTIVER BLUETOOTH");
                }
            }
        });
        /**
         * ETAPE 3
         * Analyse des appareils à proximité. -- */
        p_btn_discover_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!p_bluetoothAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "Le bluetooth doit être activé pour effectuer l'analyse", Toast.LENGTH_SHORT).show();
                } else {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    p_bluetoothAdapter.startDiscovery();
                    Toast.makeText(getApplicationContext(), "début d'analyse ...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

    }//OnCreate


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            List<String> p_device_bluetooth = new ArrayList<String>();
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ConnectDeviceActivity.this, android.R.layout.simple_list_item_1, p_device_bluetooth);
            p_list_device.setAdapter(arrayAdapter);


            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.e("[MESSAGE]", "Device enfin trouvé..");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                p_device_bluetooth.add(deviceName);
                arrayAdapter.notifyDataSetChanged();
            }

        }
    };// BroadcastReceiver

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }//onDestroy

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }//OnBackPressed
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
}