package com.example.licenta2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.licenta2.Database.Database;
import com.example.licenta2.Model.Food;
import com.example.licenta2.Model.Item;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {

    private TextView foodName;
    private TextView foodPrice;
    private TextView foodDescription;

    private ImageView foodImage;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ElegantNumberButton numberButton;

    private String foodId;

    private DatabaseReference foods;

    private Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        //Database
        foods = FirebaseDatabase.getInstance().getReference("Food");

        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        FloatingActionButton cartBtn = (FloatingActionButton) findViewById(R.id.btnCart);

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Item(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        foodId
                ));
                Toast.makeText(FoodDetails.this,"Added to cart",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        foodDescription = (TextView) findViewById(R.id.foodDescription);
        foodName = (TextView) findViewById(R.id.nameDetails);
        foodPrice = (TextView) findViewById(R.id.priceDetails);
        foodImage = (ImageView) findViewById(R.id.imgDetails);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);

        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("foodId");
        }

        if (!foodId.isEmpty()) {
            getDetails(foodId);
        }
    }

    private void getDetails(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood = snapshot.getValue(Food.class);

                //Img
                Picasso.get().load(currentFood.getImage()).into(foodImage);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                foodPrice.setText(currentFood.getPrice());
                foodName.setText(currentFood.getName());
                foodDescription.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}