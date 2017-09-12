package com.swinblockchain.producerapp.GenQR;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.swinblockchain.producerapp.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Used to query the database, blockchain and location servers
 */
public class QueryForQRActivity extends AppCompatActivity {

    String accAddr;
    String pubKey;
    String privKey;
    String productName;
    String productID;
    String batchID;

    String URL = "http://ec2-13-210-84-83.ap-southeast-2.compute.amazonaws.com:3000/getqr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        init();

        // Start new thread

        makeRequest();
    }

    private void init() {
        Bundle extras = getIntent().getExtras();

        accAddr = extras.getString("accAddr");
        pubKey = extras.getString("pubKey");
        privKey = extras.getString("privKey");
        productName = extras.getString("productName");
        productID = extras.getString("productID");
        batchID = extras.getString("batchID");
    }

    private void makeRequest() {
        RequestQueue queue = Volley.newRequestQueue(QueryForQRActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override

            public void onResponse(String response) {
                // Display the response string.
                System.out.print(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error.toString());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accAddr", accAddr.toString());
                params.put("pubKey", pubKey.toString());
                params.put("productName", productName.toString());
                params.put("productID", productID.toString());
                params.put("batchID", batchID.toString());
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
                }


}