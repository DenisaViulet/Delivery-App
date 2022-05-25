package com.example.licenta2.Database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.licenta2.Home;
import com.example.licenta2.Model.Category;
import com.example.licenta2.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategory  extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextImage;
    private EditText editTextName;


    private ProgressBar progressBar;

    private DatabaseReference addCatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category);

        //mAuth = FirebaseAuth.getInstance();
        TextView addCat = (TextView) findViewById(R.id.AddCategory_btn);
        addCat.setOnClickListener(this);
        editTextImage = (EditText) findViewById(R.id.AddCategoryImage);
        editTextName = (EditText) findViewById(R.id.AddCategoryName);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        addCatRef= FirebaseDatabase.getInstance().getReference("Category");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.AddCategory_btn:
                addCategory();
                break;
        }
    }

    private void addCategory() {

        String image = editTextImage.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

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

//        progressBar.setVisibility(View.VISIBLE);
        Category category = new Category(name,image);
        addCatRef.push().setValue(category);
        Toast.makeText(AddCategory.this, "Category inserted", Toast.LENGTH_SHORT).show();
        //Termina activity-ul current si da back
        finish();
    }
}
