package com.example.licenta2.ViewHolder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta2.FoodDetails;
import com.example.licenta2.FoodList;
import com.example.licenta2.Interface.ItemClickListener;
import com.example.licenta2.Model.Food;
import com.example.licenta2.R;
import com.example.licenta2.ViewHolder.FoodViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchFoodAdapter extends RecyclerView.Adapter<FoodViewHolder> {

    private final List<Food> listFood;
    private final Context context;

    private ItemClickListener itemClickListener;

    public SearchFoodAdapter(List<Food> listFood, Context context) {
        this.listFood = listFood;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        holder.foodNameTextView.setText(listFood.get(position).getName());
        holder.removeItemButton.setVisibility(View.GONE);
        Picasso.get().load(listFood.get(position).getImage()).into(holder.foodImageView);
        holder.setItemClickListener(itemClickListener);
    }

    @Override
    public int getItemCount() {
        return listFood.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
