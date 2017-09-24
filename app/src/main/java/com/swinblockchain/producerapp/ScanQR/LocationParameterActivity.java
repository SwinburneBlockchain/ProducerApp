package com.swinblockchain.producerapp.ScanQR;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinblockchain.producerapp.GenQR.QueryForQRActivity;
import com.swinblockchain.producerapp.MainActivity;
import com.swinblockchain.producerapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationParameterActivity extends AppCompatActivity {

    ArrayList<Scan> scanList = new ArrayList<>();
    EditText nxtAddr;
    Scan scanProducer;
    Scan scanProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_parameter);
        init();
    }

    private void init() {
        nxtAddr = (EditText) findViewById(R.id.nxtAddr);

        Bundle extras = getIntent().getExtras();

        scanProducer = new Scan(extras.get("scanProducerType").toString(), extras.get("scanProducerAddr").toString(), extras.get("scanProducerPub").toString(), extras.get("scanProducerPriv").toString());
        scanProduct = new Scan(extras.get("scanProductType").toString(), extras.get("scanProductAddr").toString(), extras.get("scanProductPub").toString(), extras.get("scanProductPriv").toString());

        for (Scan s : scanList) {
            System.out.println(s.getType());
        }
    }

    public void sendTransaction(View view) {
        // TODO make new class and query the server from there
        sendTransactionQuery();
    }

    private void sendTransactionQuery() {
        String URL = "http://ec2-54-153-202-123.ap-southeast-2.compute.amazonaws.com:3000/moveqr";

        RequestQueue queue = Volley.newRequestQueue(LocationParameterActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

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
                params.put("pubKey", scanProducer.getPubKey());
                params.put("privKey", scanProducer.getPrivKey());
                params.put("prodAddr", scanProduct.getAccAddr());
                params.put("prodPubKey", scanProduct.getPubKey());
                params.put("destination", nxtAddr.getText().toString());
                System.out.println("Parameters: " + params);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }



    //scanList = extras.getParcelableArrayList("scanList");


    //scanList.add((Scan) extras.getSerializable("scanProducer"));
    //scanList.add((Scan) extras.getSerializable("scanProduct"));

    ///Scan scanProducer = (Scan) getIntent().getSerializableExtra("scanProducer");
    //Scan scanProduct = (Scan) getIntent().getSerializableExtra("scanProduct");




    private void startError(String errorMessage) {
        Intent i = new Intent(LocationParameterActivity.this, MainActivity.class);
        i.putExtra("errorMessage", errorMessage);
        startActivity(i);
    }


}
