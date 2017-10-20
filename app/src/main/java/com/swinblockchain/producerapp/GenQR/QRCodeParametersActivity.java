package com.swinblockchain.producerapp.GenQR;

import android.app.Service;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.swinblockchain.producerapp.MainActivity;
import com.swinblockchain.producerapp.QueryActivity;
import com.swinblockchain.producerapp.R;
import com.swinblockchain.producerapp.ScanQR.LocationParameterActivity;
import com.swinblockchain.producerapp.ScanQR.Scan;
import com.swinblockchain.producerapp.ScanQR.ScanActivity;

import static com.swinblockchain.producerapp.R.id.proveLocation;
/*
  Swinburne Capstone Project - ICT90004
  Aidan Beale & John Humphrys
  https://github.com/SwinburneBlockchain
*/
  
/**
 * This activity takes in parameters to send to the server
 *
 * @author John Humphrys
 */
public class QRCodeParametersActivity extends AppCompatActivity {

    EditText productNameText;
    EditText productIDText;
    EditText batchIDText;

    String accAddr;
    String pubKey;
    String privKey;

    Button requestQRCodeButton;
    Button scanProducer;

    /**
     * Run when the activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_parameters);

        init();
    }

    /**
     * Initialise the variables
     */
    private void init() {
        productNameText = (EditText) findViewById(R.id.productNameText);
        productIDText = (EditText) findViewById(R.id.productIDText);
        batchIDText = (EditText) findViewById(R.id.batchIDText);

        requestQRCodeButton = (Button) findViewById(R.id.requestQRCodeButton);
        scanProducer = (Button) findViewById(R.id.scanProducer);

        requestQRCodeButton.setEnabled(false);
    }

    /**
     * Check the parameters are valid and start new activity
     *
     * @param view
     */
    public void requestQR(View view) {

        // Check for empty inputs
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
        if (productNameText.length() == 0) {
            Toast urlMismatchToast = Toast.makeText(getApplicationContext(),
                    "The product name cannot be empty", Toast.LENGTH_LONG);
            productNameText.requestFocus();
            imm.showSoftInput(productNameText, 0);

            urlMismatchToast.show();
        } else if (productIDText.length() == 0) {
            Toast urlMismatchToast = Toast.makeText(getApplicationContext(),
                    "The product ID cannot be empty", Toast.LENGTH_LONG);
            productIDText.requestFocus();
            imm.showSoftInput(productIDText, 0);

            urlMismatchToast.show();
        } else if (batchIDText.length() == 0) {
            Toast urlMismatchToast = Toast.makeText(getApplicationContext(),
                    "The batch ID cannot be empty", Toast.LENGTH_LONG);
            batchIDText.requestFocus();
            imm.showSoftInput(batchIDText, 0);

            urlMismatchToast.show();
        } else {
            String productName = productNameText.getText().toString();
            String productID = productIDText.getText().toString();
            String batchID = batchIDText.getText().toString();

            Intent i = new Intent(QRCodeParametersActivity.this, QueryActivity.class);
            i.putExtra("accAddr", accAddr);
            i.putExtra("pubKey", pubKey);
            i.putExtra("privKey", privKey);
            i.putExtra("productName", productName);
            i.putExtra("productID", productID);
            i.putExtra("batchID", batchID);

            i.putExtra("requestType", "genQR");

            startActivity(i);
        }
    }

    /**
     * Scans a QR code
     */
    private void scan() {
        IntentIntegrator scan = new IntentIntegrator(QRCodeParametersActivity.this);

        // Display message is Scanner application is not installed on the device
        scan.setMessage("Scanner needs to be downloaded in order to use this application.");
        scan.initiateScan();

    }

    /**
     * Called when the scan activity finishes
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null && scanningResult.getContents() != null) {
            JsonObject returnedJsonObject = stringToJsonObject(scanningResult.getContents().toString());
            accAddr = returnedJsonObject.getString("accAddr", "accAddrError");
            pubKey = returnedJsonObject.getString("pubKey", "pubKeyError");
            privKey = returnedJsonObject.getString("privKey", "privKeyError");

            requestQRCodeButton.setEnabled(true);
            scanProducer.setEnabled(false);
        }
    }


    /**
     * Converts a string to a json object
     *
     * @param stringToJson The string to convert to json
     * @return The created json object
     */
    private JsonObject stringToJsonObject(String stringToJson) {
        JsonValue jsonResponse;

        try {
            // Parses the string response into a JsonValue
            jsonResponse = Json.parse(stringToJson);
            // Converts the JsonValue into an Object
            JsonObject objectResponse = jsonResponse.asObject();
            // Returns JsonObject
            return objectResponse;
        } catch (Exception e) {
            e.printStackTrace();
            startError("The scanned QR code is not valid.\nError Code: Cannot convert QR code to JSON object");
        }
        return null;
    }

    /**
     * Displays an error message to the user and returns them to the main menu
     *
     * @param errorMessage
     */
    private void startError(String errorMessage) {
        Intent i = new Intent(QRCodeParametersActivity.this, MainActivity.class);
        i.putExtra("errorMessage", errorMessage);
        startActivity(i);
    }

    /**
     * Scan method is called if button pressed
     *
     * @param view
     */
    public void scanProducer(View view) {
        scan();
    }

    /**
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(QRCodeParametersActivity.this, MainActivity.class);
        startActivity(i);
    }
}
