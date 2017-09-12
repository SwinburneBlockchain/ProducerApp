package com.swinblockchain.producerapp.GenQR;

import android.app.Service;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.swinblockchain.producerapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QRCodeParametersActivity extends AppCompatActivity {

    EditText productNameText;
    EditText productIDText;
    EditText batchIDText;

    String accAddr;
    String pubKey;
    String privKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_parameters);

        init();
    }

    private void init() {
        productNameText = (EditText) findViewById(R.id.productNameText);
        productIDText = (EditText) findViewById(R.id.productIDText);
        batchIDText = (EditText) findViewById(R.id.batchIDText);

        Bundle extras = getIntent().getExtras();

        accAddr = extras.getString("accAddr");
        pubKey = extras.getString("pubKey");
        privKey = extras.getString("privKey");
    }

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

            Intent i = new Intent(QRCodeParametersActivity.this, QueryForQRActivity.class);
            i.putExtra("accAddr", accAddr);
            i.putExtra("pubKey", pubKey);
            //i.putExtra("privKey", privKey);
            i.putExtra("productName", productName);
            i.putExtra("productID", productID);
            i.putExtra("batchID", batchID);

            startActivity(i);
        }
    }
}
