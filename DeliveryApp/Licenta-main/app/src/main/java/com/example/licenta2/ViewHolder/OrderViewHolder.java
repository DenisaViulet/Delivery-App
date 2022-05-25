package com.example.licenta2.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta2.Interface.ItemClickListener;
import com.example.licenta2.R;

import org.jetbrains.annotations.NotNull;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView orderDate;
    public TextView orderStatus;
    public ImageButton nextStatus;
    public ImageButton cancelOrder;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        orderDate = (TextView)itemView.findViewById(R.id.orderDate);
        orderStatus = (TextView)itemView.findViewById(R.id.statusTextView);
        nextStatus = itemView.findViewById(R.id.btnNextStatus);
        cancelOrder = itemView.findViewById(R.id.btnCancel);

        itemView.setOnClickListener(this);
        nextStatus.setOnClickListener(this);
        cancelOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
