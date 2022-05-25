package com.example.licenta2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta2.Interface.ItemClickListener;
import com.example.licenta2.Model.GeocodingCoordinates;
import com.example.licenta2.Model.Order;
import com.example.licenta2.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;

public class DisplayOrders extends AppCompatActivity {
    private RecyclerView orderDate;

    private FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;

    private boolean isAdmin;

    private String userId;

    private GeocodingCoordinates userCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.display_orders);

        isAdmin = getIntent().getExtras().getBoolean("admin");
        userId = getIntent().getExtras().getString("userId");
        userCoordinates = (GeocodingCoordinates) getIntent().getExtras().get("coords");

        //Init Firebase
        //Folosesc un query pentru a constrange valorile aduse din firebase db pe baza categoryId
        Query query;
        if (isAdmin) {
            query = FirebaseDatabase.getInstance().getReference("Requests");
        } else {
            query = FirebaseDatabase.getInstance().getReference("Requests").orderByChild("userRef").equalTo(userId);
        }

        orderDate = (RecyclerView) findViewById(R.id.recyclerOrders);
        orderDate.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        orderDate.setLayoutManager(layoutManager);

        loadOrders(query);
    }

    private void loadOrders(Query query) {
        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(
                Order.class,
                R.layout.order_item,
                OrderViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Order order, int i) {
                orderViewHolder.orderDate.setText((new Date(order.getTimestampLong())).toString());
                orderViewHolder.orderStatus.setText(order.getStatus().toString());
                if (isAdmin && order.getStatus().ordinal() < Order.OrderStatus.DELIVERED.ordinal()) {
                    orderViewHolder.nextStatus.setVisibility(View.VISIBLE);
                    orderViewHolder.nextStatus.setClickable(true);
                } else {
                    orderViewHolder.nextStatus.setVisibility(View.GONE);
                }
                if (!(order.getStatus().ordinal() < Order.OrderStatus.ON_THE_WAY.ordinal()) ||
                        (!isAdmin && !Order.OrderStatus.QUEUED.equals(order.getStatus()))) {
                    orderViewHolder.cancelOrder.setVisibility(View.GONE);
                }

                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if (view.getId() == R.id.btnNextStatus) {
                            Order.OrderStatus nextStatus = Order.OrderStatus.values()[order.getStatus().ordinal() + 1];
                            if (Order.OrderStatus.ON_THE_WAY.equals(nextStatus)) {
                                order.setDeliveryRef(userId);
                            }
                            order.setStatus(nextStatus);
                            query.getRef().child(adapter.getRef(position).getKey()).setValue(order).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(DisplayOrders.this, "Order status successfully promoted", Toast.LENGTH_LONG).show();
                                }
                            });

                        } else if (view.getId() == R.id.btnCancel) {
                            order.setStatus(Order.OrderStatus.CANCELED);
                            query.getRef().child(adapter.getRef(position).getKey()).setValue(order).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(DisplayOrders.this, "Order canceled", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Intent orderDetails = new Intent(DisplayOrders.this, DisplayOrderDetails.class);
                            orderDetails.putExtra("orderId", adapter.getRef(position).getKey());
                            orderDetails.putExtra("orderStatus", order.getStatus());
                            orderDetails.putExtra("admin", isAdmin);
                            orderDetails.putExtra("userRef", order.getUserRef());
                            orderDetails.putExtra("deliveryRef", order.getDeliveryRef());
                            orderDetails.putExtra("coords", userCoordinates);
                            startActivity(orderDetails);
                        }
                    }
                });
            }
        };
        orderDate.setAdapter(adapter);
    }
}
