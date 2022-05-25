package com.example.licenta2.ViewHolder.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.licenta2.Database.Database;
import com.example.licenta2.Home;
import com.example.licenta2.Interface.ItemClickListener;
import com.example.licenta2.Model.Item;
import com.example.licenta2.R;
import com.example.licenta2.ViewHolder.CartViewHolder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder>{
    private final List<Item> listFood;
    private final Context context;

    public CartAdapter(List<Item> listFood, Context context) {
        this.listFood=listFood;
        this.context=context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        TextDrawable drawable = TextDrawable.builder()
                                            .beginConfig()
                                            .textColor(Color.BLACK)
                                            .endConfig()
                .buildRound("" + listFood.get(position).getQuantity(), Color.TRANSPARENT);
        holder.imgCartCount.setImageDrawable(drawable);
        holder.imgCartRemove.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                Database localDatabase = new Database(context);
                String itemName = holder.txtCartName.getText().toString();
                Item itemInCart = listFood.stream()
                        .filter(item -> itemName.equals(item.getProductName()))
                        .findAny()
                        .orElse(null);

                localDatabase.removeOrderItem(itemName);
                int index = listFood.indexOf(itemInCart);
                listFood.remove(itemInCart);
                notifyItemRemoved(index);
                TextView txtTotalPrice = holder.itemView.getRootView().findViewById(R.id.total);
                double total = listFood.stream()
                        .map(item -> (Double.parseDouble(item.getPrice())) * (Double.parseDouble(item.getQuantity())))
                        .reduce(0.0, Double::sum);
                Locale locale = new Locale("ro","RO");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                txtTotalPrice.setText(fmt.format(total));
                Toast.makeText(view.getContext(), "Cart item successfully removed", Toast.LENGTH_LONG).show();
            }
        });

        Locale locale = new Locale("ro","RO");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        double price = (Double.parseDouble(listFood.get(position).getPrice())) * (Double.parseDouble(listFood.get(position).getQuantity()));

        holder.txtPrice.setText(fmt.format(price));
        holder.txtCartName.setText(listFood.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listFood.size();
    }
}
