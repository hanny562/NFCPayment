package com.example.hanny.nfcpayment.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hanny.nfcpayment.R;
import com.example.hanny.nfcpayment.model.Item;

import java.util.ArrayList;

/**
 * Created by Hanny on 1/11/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder>{

    private ArrayList<Item> mData;
    private Activity mActivity;

    public ItemAdapter(ArrayList<Item> data, Activity activity) {
        this.mData = data;
        this.mActivity = activity;
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Item item = mData.get(position);

        holder.setItemName(item.getItemName());
        holder.setItemId(item.getItemId());
        holder.setItemPrice("RM " + item.getItemPrice());
        holder.setTvItemQuantity(item.getItemQuantity());

    }

    @Override
    public int getItemCount() {
        if (mData == null)
        {
            return 0;
        }
        return mData.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{

        TextView tvItemName;
        TextView tvItemId;
        TextView tvItemPrice;
        TextView tvItemQuantity;

        public ItemHolder(View itemView) {
            super(itemView);

            tvItemName = (TextView) itemView.findViewById(R.id.tvItemName);
            tvItemId = (TextView) itemView.findViewById(R.id.tvItemId);
            tvItemPrice = (TextView) itemView.findViewById(R.id.tvItemPrice);
            tvItemQuantity = (TextView) itemView.findViewById(R.id.tvItemQuantity);
        }

        public void setItemName (String itemName)
        {
            tvItemName.setText(itemName);
        }

        public void setItemId (String itemId)
        {
            tvItemId.setText(itemId);
        }

        public void setItemPrice (String itemPrice)
        {
            tvItemPrice.setText(itemPrice);
        }

        public void setTvItemQuantity (int itemQuantity)
        {
            tvItemQuantity.setText(itemQuantity);
        }

    }

}
