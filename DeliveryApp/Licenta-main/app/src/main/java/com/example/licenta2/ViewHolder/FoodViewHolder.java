package com.example.licenta2.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta2.Interface.ItemClickListener;
import com.example.licenta2.R;

import org.jetbrains.annotations.NotNull;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView foodNameTextView;
    public ImageView foodImageView;
    public Button removeItemButton;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        foodNameTextView = (TextView)itemView.findViewById(R.id.foodName);
        foodImageView = (ImageView)itemView.findViewById(R.id.foodImage);
        removeItemButton = itemView.findViewById(R.id.btnRemoveFood);

        itemView.setOnClickListener(this);
        removeItemButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
