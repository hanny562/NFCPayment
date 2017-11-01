package com.example.hanny.nfcpayment.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hanny.nfcpayment.R;
import com.example.hanny.nfcpayment.adapter.ItemAdapter;
import com.example.hanny.nfcpayment.helper.SQLController;
import com.example.hanny.nfcpayment.helper.SessionController;
import com.example.hanny.nfcpayment.model.Item;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    private TextView txtName, txtEmail;
    private Button btnLogout;
    private SQLController sqlController;
    private SessionController sessionController;
    private NfcAdapter nfcAdapter;
    private RecyclerView mItemRecyclerView;
    private ItemAdapter mAdapter;
    private ArrayList<Item> mItemCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        sqlController = new SQLController(getApplicationContext());

        sessionController = new SessionController(getApplicationContext());

        init();

        nfcAdapter = nfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC available", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NFC not available", Toast.LENGTH_LONG).show();
        }

        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent nfcPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);

        IntentFilter tagIntentFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            tagIntentFilter.addDataType("text/plain");
            IntentFilter[] intentFiltersArray = new IntentFilter[]{tagIntentFilter};
        } catch (Throwable e) {
            e.printStackTrace();
        }
        HashMap<String, String> user = sqlController.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        txtName.setText(name);
        txtEmail.setText(email);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void init(){
        mItemRecyclerView = (RecyclerView) findViewById(R.id.rvItem);
        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mItemRecyclerView.setHasFixedSize(true);
        mItemCollection = new ArrayList<>();
        mAdapter = new ItemAdapter(mItemCollection, this);
        mItemRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NFC Intent Received", Toast.LENGTH_LONG).show();
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (parcelables != null && parcelables.length > 0) {
                readTextFromMessage((NdefMessage) parcelables[0]);
            } else {
                Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //NdefMessage ndefMessage = create
        }

        super.onNewIntent(intent);
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            Toast.makeText(this, tagContent, Toast.LENGTH_LONG).show();
            dialogboxYesNo(tagContent);
        } else {
            Toast.makeText(this, "No NDef Recoards found!", Toast.LENGTH_LONG).show();
        }
    }

    private void dialogboxYesNo(final String tagContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm add item into cart?");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                dialog.dismiss();
                httpRequestGET(tagContent);
                //addItemIntoCart(tagContent);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addItemIntoCart(final String tagContent) {
        Toast.makeText(this, tagContent + " item added", Toast.LENGTH_SHORT).show();
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    private void httpRequestGET(final String item_id) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://192.168.0.11/item.php?item_id=" + item_id;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                        try {
                            response = response.getJSONObject("item");
                            String item_id = response.getString("item_id");
                            String item_name = response.getString("item_name");
                            String price = response.getString("price");
                            int quantity = response.getInt("quantity");

                            Toast.makeText(getApplicationContext(), "Item Id : " + item_id + "\n" + "Item Name : " + item_name + "\n" + "Price : RM " + price, Toast.LENGTH_LONG).show();
                            //tvRes.setText("Item Id : " + item_id + "\n" + "Item Name : " + item_name + "\n" + "Price : RM " + price);

                            addIntoRecyclerView(item_name,item_id,price, quantity);
                        } catch (Exception e) {
                            Log.d("Response", e.getMessage());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }

        );
        queue.add(getRequest);
    }

    private void addIntoRecyclerView(final String ItemName, final String itemId, final String itemPrice, final int itemQuantity){
        Item item = new Item();
        item.setItemName(ItemName);
        item.setItemId(itemId);
        item.setItemPrice(itemPrice);
        item.setItemQuantity(itemQuantity);

        mItemCollection.add(item);
    }

    private void logoutUser() {

        sessionController.setLogin(false);

        sqlController.deleteUsers();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.enableForegroundDispatch(MainActivity.this, pendingIntent, intentFilters, null);
        }

//        nfcAdapter.enableForegroundDispatch(this,nfcPendingIntent,intentFiltersArray,null);
//        handleIntent(getIntent());
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.disableForegroundDispatch(this);
        }

        super.onPause();
    }

//    private void handleIntent(Intent intent) {
//
//        Log.d("NFC", "Intent [" + intent + "]");
//
//        //getTag(intent);
//    }


}
