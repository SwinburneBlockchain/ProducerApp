package com.swinblockchain.producerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.swinblockchain.producerapp.GenQR.DisplayQRCodeActivity;
import com.swinblockchain.producerapp.GenQR.QueryForQRActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryActivity extends AppCompatActivity {

    private ArrayList<String[]> queryResults = new ArrayList<>();

    String svgResponse;
    private final String NXTBlockchainRemote = "http://ec2-52-64-224-239.ap-southeast-2.compute.amazonaws.com:6876/nxt?";
    private final String NXTBlockchainLocal = "http://ec2-52-64-224-239.ap-southeast-2.compute.amazonaws.com:6876/nxt?";
    private final String cachingServer = "http://ec2-54-153-202-123.ap-southeast-2.compute.amazonaws.com:3000";
    private final String verificationServer = "http://ec2-54-153-202-123.ap-southeast-2.compute.amazonaws.com:3000";
    private final String qrCodeGenServer = "http://ec2-54-153-202-123.ap-southeast-2.compute.amazonaws.com:3000";

    private final String productChainNXTAddr = "NXT-HP3G-T95S-6W2D-AEPHE";

    // Gen QR variables
    String accAddr;
    String pubKey;
    String privKey;
    String productName;
    String productID;
    String batchID;
    String genQRtimestamp = String.valueOf(System.currentTimeMillis());
    String data;
    String nonce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_main);

        Bundle extras = getIntent().getExtras();

        if (extras.get("requestType").equals("genQR")) {
            genQR();
        } else if (extras.get("requestType").equals("moveQR")) {
            // TODO this
            //moveQR();
        }
    }

    private void genQR() {
        Bundle extras = getIntent().getExtras();

        accAddr = extras.getString("accAddr");
        pubKey = extras.getString("pubKey");
        privKey = extras.getString("privKey");
        productName = extras.getString("productName");
        productID = extras.getString("productID");
        batchID = extras.getString("batchID");

        timestampEncryptRequest(privKey, genQRtimestamp);
    }

    private void genQR2() {
        String[] timestampEncryptRequestResults = queryResults("timestampEncryptRequest");
        data = timestampEncryptRequestResults[1];
        nonce = timestampEncryptRequestResults[2];

        getQR(accAddr, pubKey, productName, productID, batchID, data, nonce, genQRtimestamp);
    }

    private void genQR3() {
        String[] getQRresults = queryResults("getQR");
        String svgQR = getQRresults[1];

        Intent i = new Intent(QueryActivity.this, DisplayQRCodeActivity.class);
        i.putExtra("svg", svgQR);
        i.putExtra("productName", productName);
        i.putExtra("productID", productID);
        i.putExtra("batchID", batchID);

        startActivity(i);
    }

    /**
     *
     * @param producerSecretPhase
     * @param timestamp
     * @return
     */
    private void timestampEncryptRequest(final String producerSecretPhase, final String timestamp) {
        RequestQueue queue = Volley.newRequestQueue(QueryActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NXTBlockchainLocal, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // TODO try  catch
                JsonObject json = stringToJsonObject(response);
                String data = json.getString("data", "dataError");
                String nonce = json.getString("nonce", "nonceError");

                String[] result = new String[3];
                result[0] = "timestampEncryptRequest";
                result[1] = data;
                result[2] = nonce;
                addQueryResult(result);
                genQR2();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error.toString());
                startError("Error querying server for QR code\nError Code: " + error.toString());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("requestType", "encryptTo"); // The type of request we are sending
                params.put("secretPhrase", producerSecretPhase);
                params.put("recipient", productChainNXTAddr);
                params.put("messageToEncrypt", timestamp);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    /**
     *
     * @param timestamp
     * @return
     */
    private void getQR (final String producerAccAddr, final String producerPubKey, final String productName, final String productID, final String batchID, final String data, final String nonce, final String timestamp) {
        RequestQueue queue = Volley.newRequestQueue(QueryActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, qrCodeGenServer + "/getqr", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // TODO try  catch
                String svgResponse = response;

                String[] result = new String[2];
                result[0] = "getQR";
                result[1] = svgResponse;
                addQueryResult(result);
                genQR3();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error.toString());
                startError("Error querying server for QR code\nError Code: " + error.toString());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accAddr", producerAccAddr);
                params.put("pubKey", producerPubKey);
                params.put("productName", productName);
                params.put("productID", productID);
                params.put("batchID", batchID);
                params.put("data", data);
                params.put("nonce", nonce);
                params.put("timestamp", timestamp);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    private void addQueryResult(String[] result) {
        queryResults.add(result);
    }

    public String[] queryResults(String type) {
        for (String[] s : queryResults) {
            if (s[0].equals(type)) {
                String[] response = s;
                queryResults.remove(s);
                return response;
            }
        }
        return null;
    }

    /**
     * Used to throw the user back to the main screen and display an error message
     *
     * @param errorMessage The error message
     */
    private void startError(String errorMessage) {
        Intent i = new Intent(QueryActivity.this, MainActivity.class);
        i.putExtra("errorMessage", errorMessage);
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
            e.printStackTrace();
            startError("The scanned QR code is not valid.\nError Code: Cannot convert QR code to JSON object");
        }
        return null;
    }

}


