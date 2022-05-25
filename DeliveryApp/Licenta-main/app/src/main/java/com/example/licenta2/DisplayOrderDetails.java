package com.example.licenta2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licenta2.Model.GeocodingCoordinates;
import com.example.licenta2.Model.Order;
import com.example.licenta2.Model.OrderSummary;
import com.example.licenta2.Model.User;
import com.example.licenta2.ViewHolder.OrderDetailViewHolder;
import com.example.licenta2.map.Map;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class DisplayOrderDetails extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView orderDetails;

    private boolean isAdmin;

    private GeocodingCoordinates userCoordinates;
    private GeocodingCoordinates customerCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);

        orderDetails = findViewById(R.id.listOrder);
        Order.OrderStatus status = (Order.OrderStatus) getIntent().getExtras().get("orderStatus");
        userCoordinates = (GeocodingCoordinates) getIntent().getExtras().get("coords");

        if (Order.OrderStatus.ON_THE_WAY.equals(status)) {
            Button followOrderButton = findViewById(R.id.btn_follow_order);
            followOrderButton.setVisibility(View.VISIBLE);
            followOrderButton.setOnClickListener(this);
        }

        isAdmin = getIntent().getExtras().getBoolean("admin");

        if (isAdmin) {
            TextView orderCustomerName = findViewById(R.id.OrderDetailUser);
            orderCustomerName.setVisibility(View.VISIBLE);

            TextView orderCustomerEmail = findViewById(R.id.OrderDetailEmail);
            orderCustomerEmail.setVisibility(View.VISIBLE);

            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(getIntent().getExtras().getString("userRef"));
            firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if (userProfile != null) {
                        customerCoordinates = userProfile.getCoordinates();
                        orderCustomerName.setText(userProfile.name);
                        orderCustomerEmail.setText(userProfile.email);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DisplayOrderDetails.this, "Something went wrong, please try again later!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            TextView orderCustomerName = findViewById(R.id.OrderDetailUser);
            orderCustomerName.setVisibility(View.GONE);

            TextView orderCustomerEmail = findViewById(R.id.OrderDetailEmail);
            orderCustomerEmail.setVisibility(View.GONE);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        orderDetails.setLayoutManager(layoutManager);

        String orderId = getIntent().getExtras().getString("orderId");
        final Query query = FirebaseDatabase.getInstance().getReference("Requests").child(orderId).child("orderItems");
        loadOrderDetails(query);
    }

    private void loadOrderDetails(Query query) {
        FirebaseRecyclerAdapter<OrderSummary, OrderDetailViewHolder> adapter = new FirebaseRecyclerAdapter<OrderSummary, OrderDetailViewHolder>(
                OrderSummary.class,
                R.layout.orders_details_layout,
                OrderDetailViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(OrderDetailViewHolder orderDetailViewHolder, OrderSummary model, int i) {
                orderDetailViewHolder.productName.setText(model.getName());

                Locale locale = new Locale("ro", "RO");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                orderDetailViewHolder.productPrice.setText(fmt.format(Double.parseDouble(model.getPrice())));

                orderDetailViewHolder.productQuantity.setText(new StringBuilder().append("x").append(model.getQuantity()));

                Double total = Double.parseDouble(model.getPrice()) * Double.parseDouble(model.getQuantity());
                orderDetailViewHolder.totalPrice.setText(fmt.format(total));


            }
        };

        orderDetails.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() ==  R.id.btn_follow_order) {
            Intent map = new Intent(DisplayOrderDetails.this, Map.class);
            map.putExtra("userId", getIntent().getExtras().getString("userRef"));
            map.putExtra("deliveryId", getIntent().getExtras().getString("deliveryRef"));
            map.putExtra("admin", isAdmin);
            if (isAdmin) {
                map.putExtra("customerCoords", customerCoordinates);
            } else {
                map.putExtra("customerCoords", userCoordinates);
            }
            startActivity(map);
        }
    }
}
