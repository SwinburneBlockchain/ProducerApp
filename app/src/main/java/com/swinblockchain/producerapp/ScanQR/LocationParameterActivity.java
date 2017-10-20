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
import com.swinblockchain.producerapp.QueryActivity;
import com.swinblockchain.producerapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.swinblockchain.producerapp.R.id.mainTableLayout;
import static com.swinblockchain.producerapp.R.id.producerNew;
import static com.swinblockchain.producerapp.R.id.scanProducer;
/*
  Swinburne Capstone Project - ICT90004
  Aidan Beale & John Humphrys
  https://github.com/SwinburneBlockchain
*/
  
/**
 * The Location parameter activity displays the transfer about to take place and prompts the user to confirm
 *
 * @author John Humphrys
 */
public class LocationParameterActivity extends AppCompatActivity {

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

    /**
     * Called on creation
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_parameter);
        init();
    }

    /**
     * Initialise the variables
     */
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

    /**
     * Send variables to query activity
     *
     * @param view
     */
    public void sendTransaction(View view) {
        Intent i = new Intent(LocationParameterActivity.this, QueryActivity.class);

        i.putExtra("polSign", polSign);
        i.putExtra("polPubKey", polPubKey);
        i.putExtra("polTimestamp", polTimestamp);
        i.putExtra("polHash", polHash);

        i.putExtra("scanProducer", scanProducer);
        i.putExtra("scanProduct", scanProduct);
        i.putExtra("scanNextProducer", scanNextProducer);

        i.putExtra("requestType", "moveQR");

        startActivity(i);
    }

    /**
     * Displays an error message to the user and returns them to the main menu
     *
     * @param errorMessage The message to display
     */
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
