package com.example.hanny.nfcpayment.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hanny.nfcpayment.R;
import com.example.hanny.nfcpayment.model.History;

import java.util.ArrayList;

/**
 * Created by Hanny on 2/11/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    private ArrayList<History> mData;
    private Activity mActivity;

    public HistoryAdapter(ArrayList<History> data, Activity activity) {
        this.mData = data;
        this.mActivity = activity;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_layout, parent,false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.HistoryHolder holder, int position) {
        History history = mData.get(position);


    }

    @Override
    public int getItemCount() {
        if (mData == null)
        {
            return 0;
        }
        return mData.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder{
        TextView tvBillId;
        TextView tvDate;
        TextView tvTotalPrice;


        public HistoryHolder(View historyView)
        {
            super(historyView);

            tvBillId = (TextView) historyView.findViewById(R.id.tvhBillId);
            tvDate = (TextView) historyView.findViewById(R.id.tvhDate);
        }

        public void setTvBillId(String billId) {
            tvBillId.setText(billId);
        }

        public void setTvDate(String Date) {
            tvDate.setText(Date);
        }

        public void setTvTotalPrice(double totalPrice) {
            //tvTotalPrice.setText(totalPrice);
        }

    }
}
