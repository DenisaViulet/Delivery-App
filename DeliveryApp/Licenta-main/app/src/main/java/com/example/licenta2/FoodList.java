package com.example.licenta2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.licenta2.Interface.ItemClickListener;
import com.example.licenta2.Model.Food;
import com.example.licenta2.ViewHolder.Adapter.SearchFoodAdapter;
import com.example.licenta2.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.util.JsonMapper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FoodList extends AppCompatActivity {

    private RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    private boolean isAdmin;
    private boolean isSearch;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        isAdmin = getIntent().getExtras().getBoolean("admin");
        isSearch = getIntent().getExtras().getBoolean("search");
        searchQuery = getIntent().getExtras().getString("searchString");

        //Init Firebase
        //Folosesc un query pentru a constrange valorile aduse din firebase db pe baza categoryId

        recyclerView = (RecyclerView)findViewById(R.id.recyclerFood);
        recyclerView.setHasFixedSize(true);

        LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final Query query;
        final String categoryId = getIntent().getStringExtra("categoryId");
        if (isSearch) {
            query = FirebaseDatabase.getInstance().getReference("Food").orderByChild("menuId");
            loadFoodSearch(query);
        } else {
            query = FirebaseDatabase.getInstance().getReference("Food").orderByChild("menuId").equalTo(categoryId);
            loadFood(query);
        }
    }

    private void loadFood(Query query) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food model, int i) {
                foodViewHolder.foodNameTextView.setText(model.getName());
                if (isAdmin) {
                    foodViewHolder.removeItemButton.setVisibility(View.VISIBLE);
                } else {
                    foodViewHolder.removeItemButton.setVisibility(View.GONE);
                }
                Picasso.get().load(model.getImage()).into(foodViewHolder.foodImageView);

                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if (view.getId() == R.id.btnRemoveFood) {
                            query.getRef().child(adapter.getRef(position).getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(FoodList.this, "Food item successfully removed", Toast.LENGTH_LONG).show();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        } else {
                            Intent foodDetails = new Intent(FoodList.this, FoodDetails.class);
                            foodDetails.putExtra("foodId", adapter.getRef(position).getKey());
                            foodDetails.putExtra("admin", isAdmin);
                            startActivity(foodDetails);
                        }
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadFoodSearch(Query query) {
        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap foodMap = ((HashMap<String, Object>) task.getResult().getValue());
                    List<Object> foodMapList = (List<Object>) foodMap.values().stream().collect(Collectors.toList());
                    Gson gson = new Gson();
                    List<Food> foodList = foodMapList.stream()
                            .map(item -> {
                                Food food = gson.fromJson(gson.toJsonTree(item), Food.class);
                                food.setFoodId((String) foodMap.keySet().toArray()[foodMapList.indexOf(item)]);
                                return food; })
                            .filter(item -> item.getName() != null)
                            .filter(item -> item.getName().toLowerCase().contains(searchQuery.toLowerCase()))
                            .collect(Collectors.toList());
                    SearchFoodAdapter adapter = new SearchFoodAdapter(foodList, FoodList.this);
                    adapter.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent foodDetails = new Intent(FoodList.this, FoodDetails.class);
                            foodDetails.putExtra("foodId", foodList.get(position).getFoodId());
                            foodDetails.putExtra("admin", isAdmin);
                            startActivity(foodDetails);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }
}