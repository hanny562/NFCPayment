package com.example.hanny.nfcpayment.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hanny.nfcpayment.R;
import com.example.hanny.nfcpayment.adapter.ItemAdapter;
import com.example.hanny.nfcpayment.app.AppConfig;
import com.example.hanny.nfcpayment.helper.SQLController;
import com.example.hanny.nfcpayment.helper.SessionController;
import com.example.hanny.nfcpayment.model.Item;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends Activity {

    private TextView txtName, txtDay, txtDate, txtTotalPrice;
    private FloatingActionButton fab;
    private Button btnLogout, btnHistory;
    private SQLController sqlController;
    private SessionController sessionController;
    private NfcAdapter nfcAdapter;
    private RecyclerView mItemRecyclerView;
    private ItemAdapter mAdapter;
    private ArrayList<Item> mItemCollection;
    private double totalPrice = 0;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (TextView) findViewById(R.id.tvInfoName);
        txtDay = (TextView) findViewById(R.id.tvInfoDay);
        txtDate = (TextView) findViewById(R.id.tvInfoDate);
        txtTotalPrice = (TextView) findViewById(R.id.tvInfoTotalPrice);
        btnLogout = (Button) findViewById(R.id.btnInfoLogout);
        btnHistory = (Button) findViewById(R.id.btnInfoHistory);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        sqlController = new SQLController(getApplicationContext());

        sessionController = new SessionController(getApplicationContext());

        //code for Day of week
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

        //code for today's date
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        txtDay.setText(dayOfTheWeek);
        txtDate.setText(date);

        init();


        HashMap<String, String> user = sqlController.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        txtName.setText(name);

        getCartItembyEmail(email);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHistoryActivity();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentDialogBox(Double.toString(totalPrice));
            }
        });
    }

    private void init() {
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
            Toast.makeText(this, "No NFC Intent", Toast.LENGTH_LONG).show();
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

    private void showPaymentDialogBox(final String totalprice){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.diag_payment);
        dialog.show();


        TextView promptTotalPrice = (TextView) dialog .findViewById(R.id.promptTotalPrice);

        promptTotalPrice.setText("TotalPrice : " + totalprice);

        Button btnPayment = (Button)dialog.findViewById(R.id.btnPromptPayment);
        Button btnCancel = (Button)dialog.findViewById(R.id.btnPromptCancel);

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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

    private void getCartItembyEmail(final String email) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        final String url = AppConfig.URL_POPULATECART + email;

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                        try {

                            JSONArray arr = response.getJSONArray("cart_item");
                            for(int i=0; i < arr.length(); i++){
                                JSONObject json_data = arr.getJSONObject(i);
                                //Toast.makeText(getApplicationContext(),json_data.getString("item_id"), Toast.LENGTH_LONG).show();
                                String item_id = json_data.getString("item_id");
                                String item_name = json_data.getString("item_name");
                                double price = json_data.getDouble("price");
                                String quantity = json_data.getString("quantity");
                                String added_date = json_data.getString("added_date");

                                calculateTotalPrice(price);
                                addIntoRecyclerView(item_name, item_id, price, quantity, added_date);
                            }

                        } catch (Exception e) {
                            Log.d("Response", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }

        );
        queue.add(getRequest);
    }

    private void httpRequestGET(final String item_id) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        final String url = AppConfig.URL_GETITEM + item_id;

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
                            double price = response.getDouble("price");
                            String quantity = response.getString("quantity");

                            long date = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a");
                            String dateString = sdf.format(date);
                            calculateTotalPrice(price);
                            addIntoRecyclerView(item_name, item_id, price, quantity, dateString);
                            postIntoCartItem(item_id, quantity, dateString);
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

    private void addIntoRecyclerView(final String ItemName, final String itemId, final double itemPrice, final String itemQuantity, final String dateString) {
        Item item = new Item();
        item.setItemName(ItemName);
        item.setItemId(itemId);
        item.setItemPrice(itemPrice);
        item.setItemQuantity(itemQuantity);

        item.setItemAddedDate(dateString);

        mItemCollection.add(item);

        mAdapter.notifyItemInserted(0);
    }


    private void calculateTotalPrice(final double itemPrice) {
        totalPrice = totalPrice + itemPrice;
        txtTotalPrice.setText("RM " + Double.toString(totalPrice));
    }

    private void startHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    private void postIntoCartItem(final String item_id, final String quantity, final String added_date) {
        RequestQueue queue = Volley.newRequestQueue(this);
        sqlController = new SQLController(getApplicationContext());
        HashMap<String, String> user = sqlController.getUserDetails();
        final String email = user.get("email");
        final String url = AppConfig.URL_ADDITEMINTOCART;
        Toast.makeText(getApplicationContext(), email + "\n" + item_id+ "\n" + quantity+ "\n" + added_date, Toast.LENGTH_LONG).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", email);
                params.put("item_id", item_id);
                params.put("quantity", quantity);
                params.put("added_date", added_date);

                return params;
            }
        };
        queue.add(postRequest);

    }

    public static class SequentialNumber
    {
        private static int _currentNumber=0;
        public static String GetNextNumber()
        {
            _currentNumber++;
            return "PG"+ Integer.toString(_currentNumber);
        }
    }

    private void postItemIntoBill(final String item_id, final String quantity, final String payment_date) {
        RequestQueue queue = Volley.newRequestQueue(this);
        sqlController = new SQLController(getApplicationContext());
        HashMap<String, String> user = sqlController.getUserDetails();
        final String email = user.get("email");
        final String billNo = SequentialNumber.GetNextNumber();
        final String url = AppConfig.URL_ADDITEMINTOCART;
        //Toast.makeText(getApplicationContext(), email + "\n" + item_id+ "\n" + quantity+ "\n" + added_date, Toast.LENGTH_LONG).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", email);
                params.put("bill_id", billNo);
                params.put("totalPrice", Double.toString(totalPrice));
                params.put("payment_date", payment_date);

                return params;
            }
        };
        queue.add(postRequest);

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
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.disableForegroundDispatch(this);
        }

        super.onPause();
    }

}
