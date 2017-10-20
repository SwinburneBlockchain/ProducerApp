package com.swinblockchain.producerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.swinblockchain.producerapp.GenQR.QRCodeParametersActivity;
import com.swinblockchain.producerapp.ScanQR.ScanActivity;
/*
  Swinburne Capstone Project - ICT90004
  Aidan Beale & John Humphrys
  https://github.com/SwinburneBlockchain
*/
  
/**
 * The main activity displays the main menu
 *
 * @author John Humphrys
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * Check to see if an error message is present
     */
    private void init() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("errorMessage")) {
                String errorMessage = extras.getString("errorMessage");

                // Display error message
                Toast.makeText(getApplicationContext(),
                        errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Start QR code scanning activity
     *
     * @param view
     */
    public void scanQRCode(View view) {
        Intent i = new Intent(MainActivity.this, ScanActivity.class);
        startActivity(i);
    }

    /**
     * Start requesting QR code activity
     *
     * @param view
     */
    public void requestQRCode(View view) {
        Intent i = new Intent(MainActivity.this, QRCodeParametersActivity.class);
        startActivity(i);
    }

    public void displayAck(View view) {
        Intent i = new Intent(MainActivity.this, AcknowledgementsActivity.class);
        startActivity(i);
    }

    /**
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
