package com.example.licenta2.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta2.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtCartName;
    public TextView txtPrice;

    public ImageView imgCartCount;
    public ImageView imgCartRemove;

    public CartViewHolder(View itemView) {
        super(itemView);

        txtCartName = itemView.findViewById(R.id.cart_item_name);
        txtPrice = itemView.findViewById(R.id.cart_item_price);
        imgCartCount = itemView.findViewById(R.id.cart_item_count);
        imgCartRemove = itemView.findViewById(R.id.cart_item_remove);
    }

    @Override
    public void onClick(View v) {
    }
}