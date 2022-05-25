package com.example.licenta2.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta2.R;

public class OrderDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView productName;
    public TextView productPrice;
    public TextView totalPrice;
    public TextView productQuantity;

    public OrderDetailViewHolder(View itemView) {
        super(itemView);

        productName = itemView.findViewById(R.id.ProductName);
        productPrice = itemView.findViewById(R.id.ProductPrice);
        productQuantity = itemView.findViewById(R.id.ProductQuantity);
        totalPrice=itemView.findViewById(R.id.totalProductPrice);
    }

    @Override
    public void onClick(View v) {
    }
}