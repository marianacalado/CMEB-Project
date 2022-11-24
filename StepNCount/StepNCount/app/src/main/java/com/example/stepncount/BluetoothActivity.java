package com.example.stepncount;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothActivity extends Activity {
    private static final String TAG = "BluetoothServer";  // for debug messages on the Logcat
    private static final String NAME_INSECURE = "BluetoothChatInsecure";  // service name
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");  // chosen UUID
    private static final int REQUEST_ENABLE_BT = 1;

    private static final int STATE_NONE = 0;
    private static final int STATE_LISTEN = 1;
    private static final int STATE_READY = 2;
    private static final int STATE_CONNECTED = 3;

    private int state = STATE_NONE;
    private AcceptThread acceptThread = null;
    private ConnectedThread connectedThread = null;

    private ArrayAdapter<String> conversationAdapter;  // for the listview of messages
    private EditText outText;

    private String connectedDeviceName = null;
    private BluetoothAdapter btAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        btAdapter = BluetoothAdapter.getDefaultAdapter();  // getting the BluetoothAdapter object
        if (btAdapter == null) {
            Toast.makeText(this, "This device doesn't have bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "++ ON START ++");  // write on Logcat
        if (!btAdapter.isEnabled()) {   // if bluetooth is not on
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);  // let the user put it on
        } else
            setupChat();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.e(TAG, "+ ON RESUME +");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "stopping");
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        state = STATE_NONE;
        Log.e(TAG, "--- ON DESTROY ---");
    }

    // Called when the user closes the Bluetooth enable request made by this app (startActivityForResult)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        if (requestCode == REQUEST_ENABLE_BT)
            if (resultCode == Activity.RESULT_OK)
                setupChat();
            else {  // if the user does not enable bluetooth
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        // the conversation items
        conversationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ListView conversation = findViewById(R.id.in);
        conversation.setAdapter(conversationAdapter);  // setting the list view for the conversation strings

        outText = findViewById(R.id.edit_text_out);
        Button sendButton = findViewById(R.id.button_send);
        sendButton.setOnClickListener((v) -> { // listener for sending a message
            String message = outText.getText().toString();
            sendMessage(message);
        });
        state = STATE_READY;
        reportState();  // show in the title the connection status
        listenStart();  // start listening for an incoming connection
    }

    private void sendMessage(String message) {
        if (state != STATE_CONNECTED) {
            Toast.makeText(this, R.string.title_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            connectedThread.write(send);
            outText.setText("");
        }
    }

    private void setStatus(int resId) {
        getActionBar().setSubtitle(resId);
    }

    private void setStatus(CharSequence subTitle) {
        getActionBar().setSubtitle(subTitle);
    }

    private void reportState() {
        Log.i(TAG, "STATE_CHANGE: " + state);
        runOnUiThread(() -> {
            if (state == STATE_CONNECTED) {
                setStatus(getString(R.string.title_connected_to) + " " + connectedDeviceName);
                conversationAdapter.clear();
            } else
                setStatus(R.string.title_not_connected);
        });
    }

    // start the accept thread for listening to incoming connections
    public synchronized void listenStart() {
        Log.d(TAG, "Listen start");
        if (connectedThread != null) {      // cancel the Connection Thread if active
            connectedThread.cancel();
            connectedThread = null;
        }

        state = STATE_LISTEN;
        reportState();

        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    // create and run the connected thread after an incoming connection
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "Connected");

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        connectedDeviceName = device.getName();
        runOnUiThread(() -> {  // show the other device name
            Toast.makeText(getApplicationContext(), "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT).show();
        });

        state = STATE_CONNECTED;
        reportState();
    }

    private void connectionLost() {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), "Device connection was lost", Toast.LENGTH_SHORT).show()
        );
    }

    /* classes representing threads */

    /* The thread for listening to an incoming connection */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.

                }
                tmp = btAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
            }
            catch (IOException e) {
                Log.e(TAG, "Socket listen() failed", e);
            }
            serverSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "Socket: BEGIN AcceptThread");
            setName("AcceptThread");
            BluetoothSocket socket;

            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();  // blocks until an incoming connection is made
                }
                catch (IOException e) {
                    Log.e(TAG, "Socket accept() failed", e);
                    break;
                }

                if (socket != null) {
                    synchronized (this) {
                        switch (state) {
                            case STATE_LISTEN:
                                connected(socket, socket.getRemoteDevice());  // creates the connection thread
                                break;
                            case STATE_NONE:
                            case STATE_READY:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                }
                                catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            Log.d(TAG, "Socket cancel " + this);
            try {
                serverSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "Server socket close() failed", e);
            }
        }
    }

    /* The thread that represents a connection and reading and writing */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket cSocket;
        private final InputStream inStream;
        private final OutputStream outStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            cSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            inStream = tmpIn;
            outStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int nbytes;

            while (true) {
                try {
                    nbytes = inStream.read(buffer);  // blocks until some bytes are read
                }
                catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    listenStart();
                    break;
                }
                if (nbytes > 0) {  // display a new message as a new item in the listview
                    final String rmsg =  new String(buffer, 0, nbytes);
                    runOnUiThread(() ->
                            conversationAdapter.add(connectedDeviceName + ":  " + rmsg)
                    );
                }
            }
        }

        // write a message trough the bluetooth channel using the output stream
        public void write(byte[] buffer) {
            try {
                outStream.write(buffer);
                conversationAdapter.add("Me:  " + new String(buffer)); // show the message in the listview
            }
            catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                cSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "Connect socket close() failed", e);
            }
        }
    }


}
