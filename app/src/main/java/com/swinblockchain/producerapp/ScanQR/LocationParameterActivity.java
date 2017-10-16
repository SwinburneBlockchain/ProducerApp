package com.swinblockchain.producerapp.ScanQR;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinblockchain.producerapp.MainActivity;
import com.swinblockchain.producerapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.swinblockchain.producerapp.R.id.mainTableLayout;
import static com.swinblockchain.producerapp.R.id.producerNew;
import static com.swinblockchain.producerapp.R.id.scanProducer;

public class LocationParameterActivity extends AppCompatActivity {

    ArrayList<Scan> scanList = new ArrayList<>();
    Scan scanProducer;
    Scan scanProduct;
    Scan scanNextProducer;

    TextView producerNew;
    TextView product;
    TextView producerOld;

    String polSign;
    String polPubKey;
    String polTimestamp;
    String polHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_parameter);
        init();
    }

    private void init() {

        Bundle extras = getIntent().getExtras();

        scanProducer = getIntent().getParcelableExtra("scanProducer");
        scanProduct = getIntent().getParcelableExtra("scanProduct");
        scanNextProducer = getIntent().getParcelableExtra("scanNextProducer");

        producerNew = (TextView)findViewById(R.id.producerNew);
        product = (TextView)findViewById(R.id.product);
        producerOld = (TextView)findViewById(R.id.producerOld);

        producerNew.setText("New Producer: " + scanProducer.getAccAddr());
        product.setText("Product: " + scanProduct.getAccAddr());
        producerOld.setText("Old Producer: " + scanNextProducer.getAccAddr());

         polSign = extras.getString("polSign");
         polPubKey = extras.getString("polPubKey");
         polTimestamp = extras.getString("polTimestamp");
         polHash = extras.getString("polHash");

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
                startError("Product successfully moved");
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
                params.put("destination", scanNextProducer.getAccAddr());
                System.out.println("Parameters: " + params);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    private void startError(String errorMessage) {
        Intent i = new Intent(LocationParameterActivity.this, MainActivity.class);
        i.putExtra("errorMessage", errorMessage);
        startActivity(i);
    }

    /**
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(LocationParameterActivity.this, MainActivity.class);
        startActivity(i);
    }

}
