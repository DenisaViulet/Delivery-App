package com.example.licenta2.Database;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.licenta2.FoodDetails;
import com.example.licenta2.FoodList;
import com.example.licenta2.Home;
import com.example.licenta2.Model.Food;
import com.example.licenta2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AddFood  extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextAddDescription;
    private EditText editTextDiscount;
    private EditText editTextImage;
    private EditText editTextName;
    private EditText editTextPrice;

    private String categoryId;


    private ProgressBar progressBar;

    private DatabaseReference addFoodRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        //mAuth = FirebaseAuth.getInstance();
        TextView addBtn = (TextView) findViewById(R.id.AddFood_btn);
        addBtn.setOnClickListener(this);
        editTextAddDescription = (EditText) findViewById(R.id.AddFoodDescription);
        editTextDiscount = (EditText) findViewById(R.id.AddFoodDiscount);
        editTextImage = (EditText) findViewById(R.id.AddFoodImage);
        editTextName = (EditText) findViewById(R.id.AddFoodName);
        editTextPrice = (EditText) findViewById(R.id.AddFoodPrice);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        addFoodRef= FirebaseDatabase.getInstance().getReference("Food");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.AddFood_btn:
                addFood();
                break;
        }
    }

    private void addFood() {
        String description = editTextAddDescription.getText().toString().trim();
        String discount = editTextDiscount.getText().toString().trim();
        String image = editTextImage.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();

        if (description.isEmpty()) {
            editTextAddDescription.setError("Description is required!");
            editTextAddDescription.requestFocus();
            return;
        }
        if (discount.isEmpty()) {
            editTextDiscount.setError("Discount is required!");
            editTextDiscount.requestFocus();
            return;
        }

        if (image.isEmpty()) {
            editTextImage.setError("An image is required!");
            editTextImage.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
            return;
        }

        if (price.isEmpty()) {
            editTextPrice.setError("Price is required!");
            editTextPrice.requestFocus();
            return;
        }
        if (!isNumeric(discount)) {
            editTextDiscount.setError("Discount should be numeric!");
            editTextDiscount.requestFocus();
            return;
        }

        if (!isNumeric(price)) {
            editTextPrice.setError("Price should be numeric!");
            editTextPrice.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("categoryId");
        }
        if (categoryId != null) {
            Food food = new Food(name, image, description, price, discount, categoryId);
            addFoodRef.push().setValue(food);
            Toast.makeText(AddFood.this, "Food inserted", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility((View.GONE));
            finish();
        }
    }
public boolean isNumeric(String price)
{
    if (price == null) {
        return false;
    }
    try {
        double d = Double.parseDouble(price);
    } catch (NumberFormatException nfe) {
        return false;
    }
    return true;
}
}
