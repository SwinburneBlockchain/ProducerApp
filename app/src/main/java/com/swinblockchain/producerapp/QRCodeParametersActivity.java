package com.swinblockchain.producerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class QRCodeParametersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_parameters);

        init();
    }

    private void init() {
        setTitle("Generate QR Code");
    }
}
