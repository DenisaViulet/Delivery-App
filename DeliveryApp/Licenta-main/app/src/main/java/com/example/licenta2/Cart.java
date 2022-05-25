package com.example.licenta2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta2.Database.Database;
import com.example.licenta2.Model.Item;
import com.example.licenta2.Model.Order;
import com.example.licenta2.Model.OrderSummary;
import com.example.licenta2.ViewHolder.Adapter.CartAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class Cart extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;

    private DatabaseReference request;

    private Database localDatabase;

    private Button btnPlaceOrder;

    private TextView txtTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        request = FirebaseDatabase.getInstance().getReference("Requests");
        localDatabase = new Database(this);

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setOnClickListener(this);

        LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = findViewById(R.id.total);

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(this);

        Button btnClearCart = findViewById(R.id.btnClearCart);
        btnClearCart.setOnClickListener(this);

        loadList();
    }

    private void loadList(){
        List<Item> cart = localDatabase.getCarts();
        CartAdapter adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        //pretul trebuie sa poata reprezenta nr reale pozitive
        double total = getTotalPrice(localDatabase.getCarts());

        Locale locale = new Locale("ro","RO");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));

        // sa nu poata fii folosit butonul daca cosul este gol
        btnPlaceOrder.setClickable(total > 0);
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlaceOrder:
                processOrder();
                finish();
                break;
            case R.id.btnClearCart:
                clear();
                Toast.makeText(this, "Cart cleared", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Refolosesc codul si de aceea l-am extras aici
     */
    private double getTotalPrice(List<Item> cart) {
        double total = 0;
        for (Item item : cart)
            total += (Double.parseDouble(item.getPrice())) * (Double.parseDouble(item.getQuantity()));

        return total;
    }

    /**
     * Sterge db local si creaza un entry in firebase db cu detaliile unei comenzi (total, id user, lista produse)
     * Poate fi extinsa daca vrei sa acoperi si procesare de plati (card / ramburs)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processOrder() {
        Order order = createOrder(localDatabase.getCarts());
        String key = request.push().getKey();
        request.child(key).setValue(order);
        clear();
        Toast.makeText(this,"Order placed successfully!",Toast.LENGTH_LONG).show();
    }

    /**
     * Daca nu intelegi liniile 127-129 sau nu vrei sa folosesti functii lambda o pot rescries
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Order createOrder(List<Item> cart) {
        Order order = new Order();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        order.setUserRef(firebaseUser.getUid());
        order.setTotal(String.valueOf(getTotalPrice(cart)));
        order.setOrderItems(cart.stream()
                .map(item -> new OrderSummary(item.getProductName(), item.getQuantity(), item.getPrice(), item.getFirebaseRef()))
                .collect(Collectors.toList()));

        return order;
    }

    private void clear() {
        localDatabase.clearCart();
        loadList();
    }
}