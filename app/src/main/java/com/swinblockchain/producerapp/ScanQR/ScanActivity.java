package com.swinblockchain.producerapp.ScanQR;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.swinblockchain.producerapp.MainActivity;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.swinblockchain.producerapp.ProofOfLocation;
import com.swinblockchain.producerapp.R;

import java.io.Serializable;
import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity {

    String type;
    ArrayList<Scan> scanList = new ArrayList<>();

    Button scanProducer;
    Button scanProduct;
    Button setNextDest;
    Button scanNextProducer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        init();
    }

    private void init() {
        scanProducer = (Button) findViewById(R.id.scanProducer);
        scanProduct = (Button) findViewById(R.id.scanProduct);
        scanNextProducer = (Button) findViewById(R.id.scanNextProducer);
        setNextDest = (Button) findViewById(R.id.setNextDest);
        setNextDest.setEnabled(false);
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
        JsonObject returnedJsonObject = null;
        try {
            if (scanningResult.getContents() == null) {
                onBackPressed();
            } else {
                returnedJsonObject = stringToJsonObject(scanningResult.getContents().toString());
                String accAddr = returnedJsonObject.getString("accAddr", "accAddrError");
                String pubKey = returnedJsonObject.getString("pubKey", "pubKeyError");
                String privKey = returnedJsonObject.getString("privKey", "privKeyError");

                scanList.add(new Scan(type, accAddr, pubKey, privKey));

                disableButton();

                type = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            startError("The scanned QR code is not valid.\nError Code: Cannot convert JSON to required objects");
        }
    }

    private void disableButton() {
        if (type.equals("scanProducer")) {
            scanProducer.setEnabled(false);
        } else if (type.equals("scanProduct")) {
            scanProduct.setEnabled(false);
        } else if (type.equals("scanNextProducer")) {
            scanNextProducer.setEnabled(false);
        }

        if (!scanProducer.isEnabled() && !scanProduct.isEnabled() && !scanNextProducer.isEnabled()) {
            setNextDest.setEnabled(true);
        }
    }

    public void scanProducer(View view) {
        type = "scanProducer";
        scan();
    }

    public void scanProduct(View view) {
        type = "scanProduct";
        scan();
    }

    public void scanNextProducer(View view ) {
        type = "scanNextProducer";
        scan();
    }

    public void proveLocation(View view) {
        Intent i = new Intent(ScanActivity.this, ProofOfLocation.class);
        startActivityForResult(i, 5);
    }

    public void setNextDest(View view) {
        Intent i = new Intent(ScanActivity.this, LocationParameterActivity.class);
        for (Scan s : scanList) {
            i.putExtra(s.getType(), s);
        }

        startActivity(i);
    }

    /**
     * Scans a QR code
     */
    private void scan() {
        IntentIntegrator scan = new IntentIntegrator(ScanActivity.this);

        // Display message is Scanner application is not installed on the device
        scan.setMessage("Scanner needs to be downloaded in order to use this application.");
        scan.initiateScan();

    }

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
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ScanActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void startError(String errorMessage) {
        Intent i = new Intent(ScanActivity.this, MainActivity.class);
        i.putExtra("errorMessage", errorMessage);
        startActivity(i);
    }
}
