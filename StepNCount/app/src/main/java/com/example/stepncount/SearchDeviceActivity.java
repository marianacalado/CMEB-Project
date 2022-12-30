package com.example.stepncount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SearchDeviceActivity extends AppCompatActivity {

    private static final String TAG = "SearchDeviceActivity";

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private String selectedValue = "";
    private BluetoothAdapter mBluetoothAdapter = null;
    private Button buttonOK;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);


        buttonOK = findViewById(R.id.cmdOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent serviceIntent = new Intent(getApplicationContext(),AcquisitionService.class);
                serviceIntent.putExtra("Bluetooth", selectedValue);
                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);

                (new Handler()).postDelayed(this::goToResults, 5000);



            }

            private void goToResults() {
                Intent resultAct = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(resultAct);
            }

        });


        mainListView = findViewById(R.id.lst_Devices);
        ArrayList<String> lstDevices = new ArrayList<String>();
        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstDevices);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {

            if (mBluetoothAdapter.isEnabled()) {

                // Listing paired devices

                @SuppressLint("MissingPermission") Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

                if (devices.size()>0)
                {
                    for (BluetoothDevice device : devices)
                    {
                        listAdapter.add(device.getAddress() + "   " + device.getName());
                    }
                }
                else
                {
                    listAdapter.add("No Paired Device.");
                }
            }


        }



        mainListView.setAdapter( listAdapter );

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick( AdapterView<?> parent, View item, int position, long id)
            {
                selectedValue = (String) listAdapter.getItem(position);

                String[] aux = selectedValue.split("   ");
                selectedValue = aux[0];

                String txt = (String) buttonOK.getText();
                buttonOK.setText(txt + " to\n\n "+ selectedValue);
            }
        });

    }




}