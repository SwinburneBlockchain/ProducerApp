package com.swinblockchain.producerapp.GenQR;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.swinblockchain.producerapp.MainActivity;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import org.json.JSONObject;

public class ScanProducerQR extends AppCompatActivity {

    String accNo;
    String batchID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scan();
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

        JsonObject returnedJsonObject = stringToJsonObject(scanningResult.getContents().toString());

        try {

            String accAddr = returnedJsonObject.getString("accAddr", "accAddrError");
            String pubKey = returnedJsonObject.getString("pubKey", "pubKeyError");
            String privKey = returnedJsonObject.getString("privKey", "privKeyError");

            changeActivity(accAddr, pubKey, privKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans a QR code
     */
    private void scan() {
        IntentIntegrator scan = new IntentIntegrator(ScanProducerQR.this);

        // Display message is Scanner application is not installed on the device
        scan.setMessage("Scanner needs to be downloaded in order to use this application.");
        scan.initiateScan();

    }

    /**
     * Used to change the activity
     */
    private void changeActivity(String accAddr, String pubKey, String privKey) {
        Intent i = new Intent(ScanProducerQR.this, QRCodeParametersActivity.class);

        i.putExtra("accAddr", accAddr);
        i.putExtra("pubKey", pubKey);
        i.putExtra("privKey", privKey);

        startActivity(i);
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
            System.out.println("Could not parse response to Json...");
        }
        return null;
    }

    /**
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ScanProducerQR.this, MainActivity.class);
        startActivity(i);
    }

}