package com.swinblockchain.producerapp.ScanQR;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.swinblockchain.producerapp.MainActivity;
import com.swinblockchain.producerapp.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

// TODO refactor and clean up entire class

/**
 * Class is used to query the blockchain and return the response.
 */
public class QueryBlockchainActivity extends AppCompatActivity {


    private final String USER_AGENT = "Mozilla/5.0";
    private final String NODE_URL = "http://ec2-52-63-253-206.ap-southeast-2.compute.amazonaws.com:6876/nxt?";
    private String accNo; // TODO Final?
    private String batchID; // TODO Final?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_blockchain);

        init();

        String returnedJsonString = testQueryBlockchain(accNo);

        JsonArray returnedJsonArray = stringToJsonArray(returnedJsonString);

        ArrayList<ProductLocation> plList = createTransactions(returnedJsonArray, batchID);



        sendToInformation(plList);
    }

    private void init() {
        Intent i = getIntent();

        accNo = i.getStringExtra("accNo");
        batchID = i.getStringExtra("batchID");
    }

    private void sendToInformation(ArrayList<ProductLocation> plList) {
        Intent i = new Intent(QueryBlockchainActivity.this, InformationActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST", (Serializable)plList);
        i.putExtra("BUNDLE", args);
        startActivity(i);
    }

    /**
     * testScanProduct will emulate data from a QR code by sending a string containing values to the
     * blockchain
     *
     * @param accNo The account number of the QR code to query
     * TODO Remove test function
     */
    public String testQueryBlockchain(String accNo) {

        System.out.println("Testing 1 - Send Http GET request");
        try {

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            // Check if the Android SDK supports the Threadpolicy
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                // Query blockchain
                return getFromBlockchain();

            } else {
                System.out.println("Error: SDK_INT < 8");
                return "Error"; // TODO // FIXME: 5/9/17
            }

        } catch (Exception e) { // TODO // FIXME: 5/9/17
            e.printStackTrace();
        }
        return "Error"; // TODO // FIXME: 5/9/17
    }

    /**
     * Used to query the blockchain and returns the JSON object
     *
     * @return String containing the respose JSON
     * @throws Exception If url issues
     */
    private String getFromBlockchain() throws Exception {
        String queryURL = NODE_URL + "&requestType=getBlockchainTransactions&account=" + accNo;

        URL obj = new URL(queryURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + queryURL);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println("Response: " + response.toString());
        return response.toString();
    }

    private JsonArray stringToJsonArray(String stringResponse) {
        JsonValue jsonResponse;

        try {
            // Parses the string response into a JsonValue
            jsonResponse = Json.parse(stringResponse);
            // Converts the JsonValue into an Object
            JsonObject objectResponse = jsonResponse.asObject();
            // Gets the JsonArray at item 'transactions' within JsonObject
            JsonArray arrayResponse = objectResponse.get("transactions").asArray();
            // Returns JsonArray
            return arrayResponse;
        } catch (Exception e) {
            System.out.println("Could not parse response to JsonArray...");
        }
        return null;
    }

    private ArrayList<ProductLocation> createTransactions(JsonArray responseArray, String batchID) { // TODO This function belongs in InformationActivity
        ArrayList<ProductLocation> plList = new ArrayList<>();
        try {
            ///for (JsonValue value : responseArray) {
            //Bit complicated... Converting the JsonValue from JsonArray into a JsonObject
            //Then getting the nested "attachment" value as an object
            //JsonObject object = value.asObject().get("attachment").asObject();

            //String timestamp = value.asObject().get("timestamp").toString();

            //From this response, we're getting the "message" value
            //String message = object.getString("message", "unavailable");

            //JsonObject messageObj = null;

            try {
                    /*
                    //Converts the string 'message' which is like a json within a json to JsonObject
                    messageObj = Json.parse(message).asObject();

                    //Checking if the 'batchID' in the JsonObject is the one we're looking for
                    if (messageObj.getString("batchID", "unavailable").equals(batchID)) {

                        String productName = messageObj.getString("productName", "error");
                        String batchIDForObject = messageObj.getString("batchID", "error");
                        String date = messageObj.getString("date", "error");
                        String regBy = messageObj.getString("regBy", "error");
                        String location = messageObj.getString("location", "error");

                        ProductLocation pl = new ProductLocation(productName, batchIDForObject, date, regBy, location);
                        plList.add(pl);
                        */
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "10/11/17", "IGA North Ringwood", "North Ringwood, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "08/11/17", "Donut sorting facility", "Geelong, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "06/11/17", "Mr Donut Factory", "Melbourne, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "10/11/17", "IGA North Ringwood", "North Ringwood, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "08/11/17", "Donut sorting facility", "Geelong, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "06/11/17", "Mr Donut Factory", "Melbourne, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "10/11/17", "IGA North Ringwood", "North Ringwood, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "08/11/17", "Donut sorting facility", "Geelong, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "06/11/17", "Mr Donut Factory", "Melbourne, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "10/11/17", "IGA North Ringwood", "North Ringwood, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "08/11/17", "Donut sorting facility", "Geelong, Victoria"));
                plList.add(new ProductLocation("Mr Donut Donuts", "001", "06/11/17", "Mr Donut Factory", "Melbourne, Victoria"));

            } catch (Exception pe) {
                pe.printStackTrace();
                //results = "Error, not a valid QR code";
            }
            ///}
            //return results;
        } catch (Exception e) {
            //return "Error, not a valid QR code";
        }
        // Create
        return plList;
    }


    /**
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(QueryBlockchainActivity.this, MainActivity.class);
        startActivity(i);
    }

}
