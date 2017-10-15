package com.swinblockchain.producerapp.GenQR;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinblockchain.producerapp.MainActivity;
import com.swinblockchain.producerapp.R;
import com.swinblockchain.producerapp.ScanQR.ScanActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to query the database, blockchain and location servers.
 */
public class QueryForQRActivity extends AppCompatActivity {

    String accAddr;
    String pubKey;
    String privKey;
    String productName;
    String productID;
    String batchID;

    String svgResponse;
    String URL = "http://ec2-54-153-202-123.ap-southeast-2.compute.amazonaws.com:3000/getqr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        init();

        // Start new thread
        makeRequest();
    }

    /**
     * Initialise the variables
     */
    private void init() {
        Bundle extras = getIntent().getExtras();

        accAddr = extras.getString("accAddr");
        pubKey = extras.getString("pubKey");
        privKey = extras.getString("privKey");
        productName = extras.getString("productName");
        productID = extras.getString("productID");
        batchID = extras.getString("batchID");
    }

    /**
     * Create request to send to the QR code generating web server
     */
    private void makeRequest() {
        RequestQueue queue = Volley.newRequestQueue(QueryForQRActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                svgResponse = response;
                displaySvg(svgResponse);

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
                params.put("accAddr", accAddr);
                params.put("pubKey", pubKey);
                params.put("productName", productName);
                params.put("productID", productID);
                params.put("batchID", batchID);
                System.out.println("Parameters: " + params);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    /**
     * Display the QR code in the imageView
     *
     * @param svgResponse The svf string of the QR code
     */
    public void displaySvg(String svgResponse) {
        Intent i = new Intent(QueryForQRActivity.this, DisplayQRCodeActivity.class);

        i.putExtra("svgResponse", svgResponse);
        i.putExtra("productName", productName);
        i.putExtra("productID", productID);
        i.putExtra("batchID", batchID);
        startActivity(i);
    }

    /**
     * Used to throw the user back to the main screen and display an error message
     *
     * @param errorMessage The error message
     */
    private void startError(String errorMessage) {
        Intent i = new Intent(QueryForQRActivity.this, MainActivity.class);
        i.putExtra("errorMessage", errorMessage);
        startActivity(i);
    }


    /**
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(QueryForQRActivity.this, MainActivity.class);
        startActivity(i);
    }
}