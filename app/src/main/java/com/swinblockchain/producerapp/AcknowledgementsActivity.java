package com.swinblockchain.producerapp;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
/*
  Swinburne Capstone Project - ICT90004
  Aidan Beale & John Humphrys
  https://github.com/SwinburneBlockchain
*/
  
/**
 * Displays all acknowledgements in an activity
 *
 * @author John Humphrys
 */
public class AcknowledgementsActivity extends AppCompatActivity {

    ArrayList<Ack> ackList = new ArrayList<>();
    private final String ACK_FILE = "ack.csv";
    private TableLayout mainTableLayout;

    private final String AUTHOR = "Producer Application for ProductChain\n\nhttps://github.com/SwinburneBlockchain/";
    private final Uri github = Uri.parse("https://github.com/SwinburneBlockchain/");

    /**
     * Called with activity created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ack);

        init();
    }

    /**
     * Initialise variables
     */
    private void init() {
        mainTableLayout = (TableLayout) findViewById(R.id.mainTableLayout);
        TextView author = (TextView) findViewById(R.id.author);
        author.setText(AUTHOR);

        loadAcks();
    }

    /**
     * Load acks from file
     */
    public void loadAcks() {
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(ACK_FILE);

            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));
            String[] nextLine;

            while ((nextLine = csvReader.readNext()) != null) {
                String title = nextLine[0];
                String url = nextLine[1];
                ackList.add(new Ack(title, url));
            }
            csvReader.close();

            displayAckInformation(ackList);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Display link to github repo when ack pressed
     *
     * @param view
     */
    public void displayGithub(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW, github);
        startActivity(i);
    }

    /**
     * Display the acks
     * @param ackData
     */
    private void displayAckInformation(ArrayList<Ack> ackData) {

        // Draw a new column for each location
        for (Ack a : ackList) {
            createTableRow(a);
            // Clean up bottom of table
            createTableRowFinal();
        }
    }

    /**
     * Creates the gap between the rows
     */
    private void createTableRowFinal() {
        final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.tablerowgap, null);
        mainTableLayout.addView(tableRow);
    }

    /**
     * Create a table row with an ack
     *
     * @param a The ack to display information about
     */
    private void createTableRow(Ack a) {
        final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.tablerowack, null);
        final TextView tv;

        tv = (TextView) tableRow.findViewById(R.id.informationCell);
        tv.setText(a.getTitle());

        tableRow.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                Uri ackUri = findAckUri(tv.getText().toString().substring(2));

                Intent i = new Intent(Intent.ACTION_VIEW, ackUri);

                startActivity(i);
            }
        });

        mainTableLayout.addView(tableRow);
    }

    /**
     * Finds the url of the ack
     *
     * @param ackTitle The ack to look up
     * @return The URI of the ack
     */
    private Uri findAckUri(String ackTitle) {
        for (Ack a : ackList) {
            if (a.getTitle().equals(ackTitle)) {
                return Uri.parse(a.getUrl());
            }
        }
        return null;
    }

    /**
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(AcknowledgementsActivity.this, MainActivity.class);
        startActivity(i);
    }


}
