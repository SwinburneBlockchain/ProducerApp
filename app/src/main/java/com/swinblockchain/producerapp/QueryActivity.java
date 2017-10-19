package com.swinblockchain.producerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.swinblockchain.producerapp.GenQR.DisplayQRCodeActivity;
import com.swinblockchain.producerapp.ScanQR.Scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The class makes all the requests required for generating a QR code and moving a product
 *
 * @author John Humphrys
 */
public class QueryActivity extends AppCompatActivity {

    private ArrayList<String[]> queryResults = new ArrayList<>();

    private final String NXT_BLOCKCHAIN_REMOTE = "http://192.168.0.33:6876/nxt?";
    private final String NXT_BLOCKCHAIN_LOCAL = "http://192.168.0.33:6876/nxt?";
    private final String CACHING_SERVER = "http://192.168.0.33:3000";
    private final String VERIFICATION_SERVER = "http://192.168.0.33:3000";
    private final String QR_CODE_GEN_SERVER = "http://192.168.0.33:3000";

    private final String PRODUCT_CHAIN_NXT_ADDR = "NXT-HP3G-T95S-6W2D-AEPHE";

    // Gen QR variables
    String accAddr;
    String pubKey;
    String privKey;
    String productName;
    String productID;
    String batchID;
    String genQRtimestamp = String.valueOf(System.currentTimeMillis());
    String data;
    String nonce;

    // Move QR variables;
    Scan scanProducer;
    Scan scanProduct;
    Scan scanNextProducer;
    String polSign;
    String polPubKey;
    String polTimestamp;
    String polHash;

    String unsignedTxBytes;
    String signedTxBytes;

    /**
     * Started when the activity is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_main);

        Bundle extras = getIntent().getExtras();

        if (extras.get("requestType").equals("genQR")) {
            genQR();
        } else if (extras.get("requestType").equals("moveQR")) {
            moveQR();
        }
    }

    /**
     * First function called when generating a QR code
     */
    private void genQR() {
        Bundle extras = getIntent().getExtras();

        accAddr = extras.getString("accAddr");
        pubKey = extras.getString("pubKey");
        privKey = extras.getString("privKey");
        productName = extras.getString("productName");
        productID = extras.getString("productID");
        batchID = extras.getString("batchID");

        timestampEncryptRequest(privKey, genQRtimestamp);
    }

    /**
     * Second function called when generating a QR code
     */
    private void genQR2() {
        String[] timestampEncryptRequestResults = queryResults("timestampEncryptRequest");
        data = timestampEncryptRequestResults[1];
        nonce = timestampEncryptRequestResults[2];

        getQR(accAddr, pubKey, productName, productID, batchID, data, nonce, genQRtimestamp);
    }

    /**
     * Third function called when generating a QR code
     */
    private void genQR3() {
        String[] getQRresults = queryResults("getQR");
        String svgQR = getQRresults[1];

        Intent i = new Intent(QueryActivity.this, DisplayQRCodeActivity.class);
        i.putExtra("svg", svgQR);
        i.putExtra("productName", productName);
        i.putExtra("productID", productID);
        i.putExtra("batchID", batchID);

        startActivity(i);
    }

    /**
     * Makes a POST to check if the encrypted timestamp is valid
     *
     * @param producerSecretPhase The producer secret phase
     * @param timestamp           The timestamp
     * @return
     */
    private void timestampEncryptRequest(final String producerSecretPhase, final String timestamp) {
        RequestQueue queue = Volley.newRequestQueue(QueryActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NXT_BLOCKCHAIN_LOCAL, new Response.Listener<String>() {

            /**
             * Called on response
             *
             * @param response The string response
             */
            @Override
            public void onResponse(String response) {
                try {
                    JsonObject json = stringToJsonObject(response);
                    String data = json.getString("data", "dataError");
                    String nonce = json.getString("nonce", "nonceError");

                    String[] result = new String[3];
                    result[0] = "timestampEncryptRequest";
                    result[1] = data;
                    result[2] = nonce;
                    addQueryResult(result);
                    genQR2();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error.toString());
                startError("Error querying server for QR code\nInvalid QR code?");
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("requestType", "encryptTo"); // The type of request we are sending
                params.put("secretPhrase", producerSecretPhase);
                params.put("recipient", PRODUCT_CHAIN_NXT_ADDR);
                params.put("messageToEncrypt", timestamp);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    /**
     * Sends a POST request to the QR code generation server to generate a new QR code
     *
     * @param timestamp The timestamp to include
     * @return
     */
    private void getQR(final String producerAccAddr, final String producerPubKey, final String productName, final String productID, final String batchID, final String data, final String nonce, final String timestamp) {
        RequestQueue queue = Volley.newRequestQueue(QueryActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, QR_CODE_GEN_SERVER + "/getqr", new Response.Listener<String>() {

            /**
             * Called on response
             *
             * @param response The string response
             */
            @Override
            public void onResponse(String response) {
                try {
                    String svgResponse = response;

                    String[] result = new String[2];
                    result[0] = "getQR";
                    result[1] = svgResponse;
                    addQueryResult(result);
                    genQR3();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error.toString());
                startError("Error querying server for QR code\nInvalid QR code?");
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accAddr", producerAccAddr);
                params.put("pubKey", producerPubKey);
                params.put("productName", productName);
                params.put("productID", productID);
                params.put("batchID", batchID);
                params.put("data", data);
                params.put("nonce", nonce);
                params.put("timestamp", timestamp);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    /**
     * Adds the result to an array
     *
     * @param result
     */
    private void addQueryResult(String[] result) {
        queryResults.add(result);
    }

    /**
     * Finds the result in the array
     *
     * @param type The result to look for
     * @return The result array
     */
    public String[] queryResults(String type) {
        for (String[] s : queryResults) {
            if (s[0].equals(type)) {
                String[] response = s;
                queryResults.remove(s);
                return response;
            }
        }
        return null;
    }

    /**
     * First function called when generating a move QR
     */
    private void moveQR() {
        Bundle extras = getIntent().getExtras();

        scanProducer = getIntent().getParcelableExtra("scanProducer");
        scanProduct = getIntent().getParcelableExtra("scanProduct");
        scanNextProducer = getIntent().getParcelableExtra("scanNextProducer");

        polSign = extras.getString("polSign");
        polPubKey = extras.getString("polPubKey");
        polTimestamp = extras.getString("polTimestamp");
        polHash = extras.getString("polHash");

        checkIfValidated(scanProduct.getAccAddr(), PRODUCT_CHAIN_NXT_ADDR);
    }

    /**
     * Second function called when generating a move QR
     */
    private void moveQR2() {
        generateTransaction(scanProduct.getAccAddr(), scanProduct.getPubKey(), scanProducer.getPubKey(), scanNextProducer.getAccAddr());
    }

    /**
     * Third function called when generating a move QR
     */
    private void moveQR3() {
        String[] getQRresults = queryResults("generateTransaction");
        String unsignedTxBytes = getQRresults[1];

        signTransaction(unsignedTxBytes, scanProducer.getPrivKey());
    }

    /**
     * Fourth function called when generating a move QR
     */
    private void moveQR4() {
        String[] getQRresults = queryResults("signTransaction");
        signedTxBytes = getQRresults[1];
        sendTransaction(signedTxBytes);
    }

    /**
     * Fifth function called when generating a move QR
     */
    private void moveQR5() {
        updateHashInfo(polHash, polPubKey, polSign, polTimestamp);
    }

    /**
     * Checks if the QR code is validated
     *
     * @param productAccAddr      The product account address
     * @param productChainNXTAddr The product NXT addr
     */
    private void checkIfValidated(final String productAccAddr, final String productChainNXTAddr) {
        RequestQueue queue = Volley.newRequestQueue(QueryActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CACHING_SERVER + "/checkIfValid", new Response.Listener<String>() {

            /**
             * Called on response
             *
             * @param response The response string
             */
            @Override
            public void onResponse(String response) {
                try {
                    String check = response;

                    if (Boolean.valueOf(check)) {
                        moveQR2();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                params.put("accAddr", productAccAddr);
                params.put("checkAddr", productChainNXTAddr);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    /**
     * Generates the blockchain transaction offline on the local node
     *
     * @param productAccAddr The product account address
     * @param productPubKey The product pub key
     * @param oldProducerPubKey The previous producer pub key
     * @param newProducerAccAddr The next producers account addr
     */
    private void generateTransaction(final String productAccAddr, final String productPubKey, final String oldProducerPubKey, final String newProducerAccAddr) {
        RequestQueue queue = Volley.newRequestQueue(QueryActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NXT_BLOCKCHAIN_LOCAL, new Response.Listener<String>() {

            /**
             * Called on response
             *
             * @param response The string containing the response
             */
            @Override
            public void onResponse(String response) {
                try {
                    JsonObject json = stringToJsonObject(response);

                    String unsignedTxBytes = json.getString("unsignedTransactionBytes", "unsignedTransactionBytesError");

                    String[] result = new String[2];
                    result[0] = "generateTransaction";
                    result[1] = unsignedTxBytes;
                    addQueryResult(result);
                    moveQR3();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                params.put("requestType", "sendMessage");
                params.put("recipient", productAccAddr);
                params.put("recipientPublicKey", productPubKey);
                params.put("publicKey", oldProducerPubKey);
                params.put("message", "MOVE - " + newProducerAccAddr + " - " + polHash);
                params.put("deadline", "60");
                params.put("feeNQT", "0");
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    /**
     * Signs the generates transaction offline on the local NXT node
     *
     * @param unsignedTxBytes The unsigned tx bytes to sign
     * @param oldProducerSecretPhrase The previous producers secret phrase
     */
    private void signTransaction(final String unsignedTxBytes, final String oldProducerSecretPhrase) {
        RequestQueue queue = Volley.newRequestQueue(QueryActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NXT_BLOCKCHAIN_LOCAL, new Response.Listener<String>() {

            /**
             * Called on response
             *
             * @param response The response string
             */
            @Override
            public void onResponse(String response) {
                try {
                    JsonObject json = stringToJsonObject(response);

                    String transactionBytes = json.getString("transactionBytes", "transactionBytesError");

                    String[] result = new String[2];
                    result[0] = "signTransaction";
                    result[1] = transactionBytes;
                    addQueryResult(result);
                    moveQR4();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                params.put("requestType", "signTransaction");
                params.put("unsignedTransactionBytes", unsignedTxBytes);
                params.put("secretPhrase", oldProducerSecretPhrase);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    /**
     * Sends the signed transaction to the live blockchain
     *
     * @param signedTxBytes The signed tx bytes to send
     */
    private void sendTransaction(final String signedTxBytes) {
        RequestQueue queue = Volley.newRequestQueue(QueryActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NXT_BLOCKCHAIN_REMOTE, new Response.Listener<String>() {

            /**
             * The response
             *
             * @param response The response as a string
             */
            @Override
            public void onResponse(String response) {
                try {
                    moveQR5();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                params.put("requestType", "sendTransaction");
                params.put("transactionBytes", signedTxBytes);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    /**
     * Update the hash info in the caching server
     *
     * @param polHash The proof of location hash
     * @param polPubKey The proof of location producer public key
     * @param polSign The proof of location sign of the timestamp by the producer private key
     * @param polTimestamp The proof of location timestamp
     */
    private void updateHashInfo(final String polHash, final String polPubKey, final String polSign, final String polTimestamp) {
        RequestQueue queue = Volley.newRequestQueue(QueryActivity.this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CACHING_SERVER + "/updateHashInfo", new Response.Listener<String>() {

            /**
             * The response
             *
             * @param response The response as a string
             */
            @Override
            public void onResponse(String response) {
                if (response.equals("true")) {
                    startError("Transaction to product sent");
                }
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
                params.put("hash", polHash);
                params.put("publicKey", polPubKey);
                params.put("locationProof", polSign);
                params.put("timestamp", polTimestamp);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        System.out.print(stringRequest);
        queue.add(stringRequest);
    }

    /**
     * Used to throw the user back to the main screen and display an error message
     *
     * @param errorMessage The error message
     */
    private void startError(String errorMessage) {
        Intent i = new Intent(QueryActivity.this, MainActivity.class);
        i.putExtra("errorMessage", errorMessage);
        startActivity(i);
    }

    private JsonObject stringToJsonObject(String stringToJson) {
        JsonValue jsonResponse;

        try {
            // Parses the string response into a JsonValue
            jsonResponse = Json.parse(stringToJson);
            // Converts the JsonValue into an Object
            JsonObject objectResponse = jsonResponse.asObject();
            // Returns JsonObject
            return objectResponse;
        } catch (Exception e) {
            e.printStackTrace();
            startError("The scanned QR code is not valid.\nError Code: Cannot convert QR code to JSON object");
        }
        return null;
    }

    /**
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(QueryActivity.this, MainActivity.class);
        startActivity(i);
    }
}