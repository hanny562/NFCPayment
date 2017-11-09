package com.example.hanny.nfcpayment.adapter;

import android.app.Activity;
import android.content.Context;
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
    private Context mContext;

    public ItemAdapter(ArrayList<Item> data, Context context) {
        this.mData = data;
        this.mContext = context;
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
        holder.setItemPrice(item.getItemPrice());
        holder.setTvItemQuantity(item.getItemQuantity());
        holder.setTvItemAddedDate(item.getItemAddedDate());
    }

    @Override
    public int getItemCount() {
        if (mData == null)
        {
            return 0;
        }
        return mData.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvItemName;
        TextView tvItemId;
        TextView tvItemPrice;
        TextView tvItemQuantity;
        TextView tvItemAddedDate;

        public ItemHolder(View itemView) {
            super(itemView);

            tvItemName = (TextView) itemView.findViewById(R.id.tvItemName);
            tvItemId = (TextView) itemView.findViewById(R.id.tvItemId);
            tvItemPrice = (TextView) itemView.findViewById(R.id.tvItemPrice);
            tvItemQuantity = (TextView) itemView.findViewById(R.id.tvItemQuantity);
            tvItemAddedDate = (TextView) itemView.findViewById(R.id.tvItemAddedDate);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        }

        public void setItemName (String itemName)
        {
            tvItemName.setText(itemName);
        }

        public void setItemId (String itemId)
        {
            tvItemId.setText("Item ID : " + itemId);
        }

        public void setItemPrice (double itemPrice)
        {
            tvItemPrice.setText("RM " + Double.toString(itemPrice));
        }

        public void setTvItemQuantity (String itemQuantity)
        {
            tvItemQuantity.setText(itemQuantity);
        }

        public void setTvItemAddedDate (String itemAddedDate)
        {
            tvItemAddedDate.setText("Added on : " + itemAddedDate);
        }


        @Override
        public void onClick(View v) {

        }
    }

}
