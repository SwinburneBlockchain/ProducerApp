package com.swinblockchain.producerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        //getSupportActionBar().hide();
    }

    public void scanQRCode(View view) {
        Intent i = new Intent(MainActivity.this, ScanActivity.class);
        startActivity(i);
    }

    public void requestQRCode(View view) {

    }
}
