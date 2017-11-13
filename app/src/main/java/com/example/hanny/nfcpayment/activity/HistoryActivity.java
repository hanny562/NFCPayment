package com.example.hanny.nfcpayment.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hanny.nfcpayment.R;
import com.example.hanny.nfcpayment.adapter.HistoryAdapter;
import com.example.hanny.nfcpayment.app.AppConfig;
import com.example.hanny.nfcpayment.helper.SQLController;
import com.example.hanny.nfcpayment.model.History;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hanny on 2/11/2017.
 */

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mItemRecyclerView;
    private HistoryAdapter mAdapter;
    private ArrayList<History> mItemCollection;
    private double totalPrice = 0;
    private SQLController sqlController;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        init();
        sqlController = new SQLController(getApplicationContext());
        HashMap<String, String> user = sqlController.getUserDetails();

        String email = user.get("email");
        //Toast.makeText(getApplicationContext(), email + "in historyView", Toast.LENGTH_LONG).show();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_history_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sqlController = new SQLController(getApplicationContext());
                HashMap<String, String> user = sqlController.getUserDetails();

                String email = user.get("email");
                getHistory(email);
            }
        });

        getHistory(email);
    }

    private void init() {
        mItemRecyclerView = (RecyclerView) findViewById(R.id.rvHistory);
        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mItemRecyclerView.setHasFixedSize(true);
        mItemCollection = new ArrayList<>();
        mAdapter = new HistoryAdapter(mItemCollection, this);
        mItemRecyclerView.setAdapter(mAdapter);

    }

    private void getHistory(final String email) {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        final String url = AppConfig.URL_POPULATEHISTORY + email;

        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                        clear();
                        try {

                            JSONArray arr = response.getJSONArray("history");
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject json_data = arr.getJSONObject(i);
                                //Toast.makeText(getApplicationContext(),json_data.getString("item_id"), Toast.LENGTH_LONG).show();
                                String bill_id = json_data.getString("bill_id");
                                //String email = json_data.getString("email");
                                double totalPrice = json_data.getDouble("total_price");
                                String dateString = json_data.getString("payment_date");

                                addIntoRecyclerView(bill_id, totalPrice, dateString);
                                mSwipeRefreshLayout.setRefreshing(false);
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

    private void addIntoRecyclerView(final String billId, final double totalPrice, final String payment_date) {
        History history = new History();
        history.sethBillId(billId);
        //history.sethEmail(email);
        history.sethTotalPrice(totalPrice);
        history.sethDate(payment_date);

        mItemCollection.add(history);

        mAdapter.notifyItemInserted(0);
    }

    public void clear() {
        mItemCollection.clear();
        mAdapter.notifyDataSetChanged();
    }

}
