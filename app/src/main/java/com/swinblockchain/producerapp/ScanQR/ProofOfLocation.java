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

/**
 * Makes a proof of location request to the bluetooth beacon
 *
 * @author John Humphrys
 */
public class ProofOfLocation extends AppCompatActivity {

    Button aquireProof;
    Button connectToDevice;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice device;
    public static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final String NAME = "ProductChain";

    /**
     * Called on activity creation
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proof_of_location);

        aquireProof = (Button) findViewById(R.id.aquireProof);
        connectToDevice = (Button) findViewById(R.id.connectToDevice);

        aquireProof.setEnabled(false);
    }

    /**
     * Create a new thread to connect to the bluetooth beacon with
     *
     * @param view
     */
    public void aquireProof(View view) {
        if (device != null) {
            new Thread(new ConnectThread(device)).start();
        }
    }

    /**
     * When button is pressed the client is started
     *
     * @param view
     */
    public void connectToDevice(View view) {
        startClient();
    }

    /**
     * The handler updates the console with information on the UI thread
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
            return true;
        }

    });

    /**
     * Handles the message to send to the handler
     *
     * @param str The string to display
     */
    public void mkmsg(String str) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    /**
     * Starts the client thread
     */
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
            queryPaired();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        //startClient();
    }

    /**
     * Checks to see the paired devices on the mobile
     */
    public void queryPaired() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            final BluetoothDevice blueDev[] = new BluetoothDevice[pairedDevices.size()];
            String[] items = new String[blueDev.length];
            int i = 0;
            for (BluetoothDevice devicel : pairedDevices) {
                blueDev[i] = devicel;
                items[i] = blueDev[i].getName() + ": " + blueDev[i].getAddress();
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
        } else {
            mkmsg("A device must be paired before use");
        }
    }

    /**
     * The connect thread connects to the bluetooth server
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                mkmsg("Client connection failed: " + e.getMessage());
            }
            socket = tmp;

        }

        /**
         * run the connect thread
         */
        public void run() {
            mkmsg("Querying bluetooth beacon");
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                socket.connect();
            } catch (IOException e) {
                mkmsg("Connection to bluetooth beacon failed");
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e2) {
                    System.out.println("unable to close() socket during connection failure: " + e2.getMessage() + "\n");
                    socket = null;
                }
            }

            // If a connection was accepted
            if (socket != null) {
                System.out.println("Connection made\n");
                System.out.println("Remote device address: " + socket.getRemoteDevice().getAddress() + "\n");
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    System.out.println("Sending request");
                    out.println("Send proof of location");
                    out.flush();
                    System.out.println("Message sent...\n");

                    System.out.println("Waiting for response\n");
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = in.readLine();
                    System.out.println("received a message:\n" + str + "\n");

                    // Check the response is what we expect
                    String[] message = null;

                    if (str.contains(",")) {
                        message = str.split(",");
                        if (message.length == 4) {
                            mkmsg("Proof of location obtained");
                            System.out.println("Closing connection\n");
                            finishActivity(message);
                        }
                    }
                    System.out.println("Message not verified");
                    System.out.println("We are done, closing connection\n");
                    finishActivity(message);


                } catch (Exception e) {
                    mkmsg("Error happened sending/receiving");

                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Unable to close socket" + e.getMessage());
                        mkmsg("An error occured during transmission");
                    }
                }
            } else {
                System.out.println("Made connection, but socket is null\n");
                mkmsg("An error occured during transmission");
            }
            System.out.println("Client ending \n");
        }

        /**
         * Cancels the thread
         */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                mkmsg("close() of connect socket failed: " + e.getMessage() + "\n");
            }
        }

        /**
         * Finishs the activity and returns the gathered information
         *
         * @param message
         */
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
