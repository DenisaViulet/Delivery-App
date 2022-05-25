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

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView menuNameTextView;
    public ImageView imageView;
    public Button removeItemButton;
    public Button addItemButton;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NotNull View itemView) {
        super(itemView);

        menuNameTextView = (TextView) itemView.findViewById(R.id.menuName);
        imageView = (ImageView) itemView.findViewById(R.id.menuImage);
        removeItemButton = itemView.findViewById(R.id.btnRemoveMenu);
        addItemButton = itemView.findViewById(R.id.btnAddFood2);

        itemView.setOnClickListener(this);
        removeItemButton.setOnClickListener(this);
        addItemButton.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener=itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
