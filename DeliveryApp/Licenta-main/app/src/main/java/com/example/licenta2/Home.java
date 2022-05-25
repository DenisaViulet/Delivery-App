package com.example.licenta2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta2.Database.AddCategory;
import com.example.licenta2.Database.AddFood;
import com.example.licenta2.Interface.ItemClickListener;
import com.example.licenta2.Model.Category;
import com.example.licenta2.Model.GeocodingCoordinates;
import com.example.licenta2.ViewHolder.MenuViewHolder;
import com.example.licenta2.map.Map;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    private DatabaseReference category;

    private RecyclerView recyclerMenu;

    private Boolean isAdmin;
    private String userId;
    private GeocodingCoordinates userCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Category");
        setSupportActionBar(toolbar);

        isAdmin = getIntent().getExtras().getBoolean("admin");
        userId = getIntent().getExtras().getString("userId");
        userCoordinates = (GeocodingCoordinates) getIntent().getExtras().get("coords");

        //Init Firebase
        category = FirebaseDatabase.getInstance().getReference("Category");

        if(!isAdmin) {
            FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdmin);
            fabAdd.setVisibility(View.GONE);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent cartIntent = new Intent(Home.this, Cart.class);
                    startActivity(cartIntent);
                }
            });
        } else {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.GONE);

            FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdmin);
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addCategoryIntent = new Intent(Home.this, AddCategory.class);
                    startActivity(addCategoryIntent);
                }
            });
        }
        DrawerLayout drawer = (DrawerLayout) findViewById((R.id.drawer_layout));
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set user
        View headerView = navigationView.getHeaderView(0);
        final TextView txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            txtFullName.setText(user.getDisplayName());
            txtFullName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(Home.this, UserProfile.class);
                    startActivity(profileIntent);
                }
            });
        }

        //Load menu
        recyclerMenu = (RecyclerView)findViewById(R.id.recyclerMenu);
        recyclerMenu.setHasFixedSize(true);

        LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);

        loadMenu();
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                category
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category model, int i) {
                menuViewHolder.menuNameTextView.setText(model.getName());
                if (isAdmin) {
                    menuViewHolder.removeItemButton.setVisibility(View.VISIBLE);
                    menuViewHolder.addItemButton.setVisibility(View.VISIBLE);
                } else {
                    menuViewHolder.removeItemButton.setVisibility(View.GONE);
                    menuViewHolder.addItemButton.setVisibility(View.GONE);
                }

                Picasso.get()
                        .load(model.getImage())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(menuViewHolder.imageView, new Callback() {
                            @Override
                            public void onSuccess() { }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(model.getImage())
                                        .into(menuViewHolder.imageView, new Callback() {
                                            @Override
                                            public void onSuccess() { }

                                            @Override
                                            public void onError(Exception e) {
                                                System.out.println("Picasso Could not fetch image for category: " + model.getName());
                                            }
                                        });
                            }
                        });

                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if (view.getId() == R.id.btnRemoveMenu) {
                            category.child(adapter.getRef(position).getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //TODO sterge toate mancarurile care erau in categoria X
                                        Toast.makeText(Home.this, "Menu item successfully removed", Toast.LENGTH_LONG).show();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        } else if (view.getId() == R.id.btnAddFood2) {
                            Intent addFoodList = new Intent(Home.this, AddFood.class);
                            addFoodList.putExtra("categoryId",adapter.getRef(position).getKey());

                            startActivity(addFoodList);
                        }
                        else {
                            //Toast.makeText(Home.this, "" + clickItem.getImage(), Toast.LENGTH_LONG).show();
                            //Link category id with menu id
                            Intent foodList = new Intent(Home.this, FoodList.class);

                            //get the key of the category
                            foodList.putExtra("categoryId", adapter.getRef(position).getKey());
                            foodList.putExtra("admin", isAdmin);
                            startActivity(foodList);
                        }
                    }
                });
            }
        };

        recyclerMenu.setAdapter(adapter);
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem search = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search here ...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent foodList = new Intent(Home.this, FoodList.class);
                foodList.putExtra("search", true);
                foodList.putExtra("searchString", query);
                foodList.putExtra("admin", isAdmin);
                startActivity(foodList);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_menu){
            Intent menu = new Intent(Home.this, Map.class);
            menu.putExtra("coords", userCoordinates);
            menu.putExtra("admin", isAdmin);
            menu.putExtra("userId", userId);
            startActivity(menu);
        } else if(id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);
        } else if(id == R.id.nav_orders){
            Intent showOrders= new Intent(Home.this, DisplayOrders.class);
            showOrders.putExtra("coords", userCoordinates);
            showOrders.putExtra("userId", userId);
            showOrders.putExtra("admin", isAdmin);
            startActivity(showOrders);
        } else if(id == R.id.nav_logOut){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Home.this, MainActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById((R.id.drawer_layout));
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}

