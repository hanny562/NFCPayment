package com.example.hanny.nfcpayment.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hanny.nfcpayment.R;
import com.example.hanny.nfcpayment.adapter.ItemAdapter;
import com.example.hanny.nfcpayment.model.History;
import com.example.hanny.nfcpayment.model.Item;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Hanny on 2/11/2017.
 */

public class HistoryActivity extends AppCompatActivity{

    private RecyclerView mItemRecyclerView;
    private ItemAdapter mAdapter;
    private ArrayList<History> mItemCollection;
    private double totalPrice = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }

    private void getCartItembyEmail(final String email) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "http://192.168.0.11/populatecartbyemail.php?email=" + email;

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                        try {

                            JSONArray arr = response.getJSONArray("history");
                            for(int i=0; i < arr.length(); i++){
                                JSONObject json_data = arr.getJSONObject(i);
                                //Toast.makeText(getApplicationContext(),json_data.getString("item_id"), Toast.LENGTH_LONG).show();
                                String bill_id = json_data.getString("bill_id");
                                String email = json_data.getString("email");
                                double totalPrice = json_data.getDouble("total_price");
                                String dateString = json_data.getString("payment_date");

                                addIntoRecyclerView(bill_id, email, totalPrice, dateString);
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

    private void addIntoRecyclerView(final String billId, final String email, final double totalPrice, final String payment_date) {
        History history = new History();
        history.sethBillId(billId);
        history.sethEmail(email);
        history.sethTotalPrice(totalPrice);
        history.sethDate(payment_date);

        mItemCollection.add(history);

        mAdapter.notifyItemInserted(0);
    }



}
