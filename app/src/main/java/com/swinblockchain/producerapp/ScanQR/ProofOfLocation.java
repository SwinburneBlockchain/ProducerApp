package com.swinblockchain.producerapp.ScanQR;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.swinblockchain.producerapp.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;

public class ProofOfLocation extends AppCompatActivity {

    Button aquireProof;
    Button connectToDevice;
    TextView outputView;
    String TAG = "client";
    Button btn_start, btn_device;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice device;
    BluetoothDevice remoteDevice;
    public static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final String NAME = "ProductChain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proof_of_location);

        aquireProof = (Button) findViewById(R.id.aquireProof);
        connectToDevice = (Button) findViewById(R.id.connectToDevice);
        outputView = (TextView) findViewById(R.id.outputView);

        aquireProof.setEnabled(false);
    }

    public void aquireProof(View view) {
        if (device != null) {
            new Thread(new ConnectThread(device)).start();
        }
    }

    public void connectToDevice(View view) {
        startClient();

    }

    public TextView getOutputView() {
        return outputView;
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), msg.getData().getString("msg"), Toast.LENGTH_LONG).show();
            return true;
        }

    });

    public void mkmsg(String str) {
        //handler junk, because thread can't update screen!
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    public void startClient() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            mkmsg("Bluetooth not supported");
            return;
        }
        //make sure bluetooth is enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            querypaired();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        //startClient();
    }

        /*
     * This method will query the bluetooth device and ask for a list of all
     * paired devices.  It will then display to the screen the name of the device and the address
     *   In client fragment we need this address to so we can connect to the bluetooth device that is acting as the server.
     */

    public void checkIfPairedDevices() {
        mkmsg("Paired Devices:");
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            final BluetoothDevice blueDev[] = new BluetoothDevice[pairedDevices.size()];
            String item;
            int i = 0;
            for (BluetoothDevice devicel : pairedDevices) {
                blueDev[i] = devicel;
                item = blueDev[i].getName() + ": " + blueDev[i].getAddress();
                //mkmsg("Device: " + item);
                i++;
            }

        } else {
            mkmsg("There are no paired devices, pair a device first");
        }
    }

    public void querypaired() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            mkmsg("at least 1 paired device\n");
            final BluetoothDevice blueDev[] = new BluetoothDevice[pairedDevices.size()];
            String[] items = new String[blueDev.length];
            int i = 0;
            for (BluetoothDevice devicel : pairedDevices) {
                blueDev[i] = devicel;
                items[i] = blueDev[i].getName() + ": " + blueDev[i].getAddress();
                //outputView.append("Device: " + items[i] + "\n");
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                i++;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ProofOfLocation.this);
            builder.setTitle("Choose Bluetooth:");
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    if (item >= 0 && item < blueDev.length) {
                        device = blueDev[item];
                        connectToDevice.setText("Device to query: " + blueDev[item].getName());
                        aquireProof.setEnabled(true);

                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */

    private class ConnectThread extends Thread {
        private BluetoothSocket socket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                mkmsg("Client connection failed: " + e.getMessage());
            }
            socket = tmp;

        }

        public void run() {
            mkmsg("Client started\n");
            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket.connect();
            } catch (IOException e) {
                mkmsg("Connection failed");
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e2) {
                    mkmsg("unable to close() socket during connection failure: " + e2.getMessage() + "\n");
                    socket = null;
                }
                // Start the service over to restart listening mode
            }
            // If a connection was accepted
            if (socket != null) {
                mkmsg("Connection made\n");
                System.out.println("Remote device address: " + socket.getRemoteDevice().getAddress() + "\n");
                //Note this is copied from the TCPdemo code.
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    mkmsg("Sending request");
                    out.println("Send proof of location");
                    out.flush();
                    System.out.println("Message sent...\n");

                    mkmsg("Waiting for response\n");
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = in.readLine();
                    System.out.println("received a message:\n" + str + "\n");

                    String[] message = null;

                    if (str.contains(",")) {
                        message = str.split(",");
                        if (message.length == 2) {
                            mkmsg("Message checked, ready to query blockchain");
                            System.out.println("Closing connection\n");
                            finishActivity(message);
                        }
                    }
                    System.out.println("Message not verified");
                    System.out.println("We are done, closing connection\n");
                    finishActivity(message);


                } catch (Exception e) {
                    mkmsg("Error happened sending/receiving\n");

                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        mkmsg("Unable to close socket" + e.getMessage() + "\n");
                    }
                }
            } else {
                mkmsg("Made connection, but socket is null\n");
            }
            mkmsg("Client ending \n");

        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                mkmsg("close() of connect socket failed: " + e.getMessage() + "\n");
            }
        }

        private void finishActivity(String[] message) {
            Intent i = new Intent();
            i.putExtra("sign", message[0]);
            i.putExtra("pubkey", message[1]);
            i.putExtra("timestamp", message[2]);
            i.putExtra("hash", message[3]);
            setResult(5, i);
            finish();
        }
    }
}
