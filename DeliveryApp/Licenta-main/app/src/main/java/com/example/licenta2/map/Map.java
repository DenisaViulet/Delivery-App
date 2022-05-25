package com.example.licenta2.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.example.licenta2.Model.GeocodingCoordinates;
import com.example.licenta2.Model.User;
import com.example.licenta2.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.math3.util.Precision;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    public static final int LOCATION_UPDATE_INTERVAL = 10 * 1000;
    public static final int ZOOM_PADDING_VALUE = 100;
    private final LatLng restaurantLocation = new LatLng(45.641084, 25.5859614);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private LatLng targetLocation;
    private LatLng deliveryLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private GoogleMap map;

    private GeocodingCoordinates customerCoordinates;

    private LocationCallback locationCallback;

    private boolean requestingLocationUpdates;
    private boolean isAdmin;

    private DatabaseReference query;

    private String userId;
    private String deliveryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isAdmin = getIntent().getExtras().getBoolean("admin");
        requestingLocationUpdates = isAdmin;
        userId = getIntent().getExtras().getString("userId");
        deliveryId = getIntent().getExtras().getString("deliveryId");
        query = FirebaseDatabase.getInstance().getReference("Users");
        customerCoordinates = (GeocodingCoordinates) getIntent().getExtras().get("customerCoords");

        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (Precision.compareTo(targetLocation.latitude, location.getLatitude(), 0.00001) != 0 &&
                        Precision.compareTo(targetLocation.longitude, location.getLongitude(), 0.00001) != 0) {
                        deliveryLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        updateFirebaseLocation();
                        updateLocationUI();
                    }
                }
            }
        };

        getLocationPermission();
    }

    private void fetchFirebaseLocation() {
        if (!isAdmin) {
            if (deliveryLocation == null) {
                query.child(deliveryId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        if (user != null) {
                            deliveryLocation = new LatLng(user.getCoordinates().getLatitude(), user.getCoordinates().getLongitude());
                            placeMarkers();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }

            query.child(deliveryId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    GeocodingCoordinates geocodingCoordinates = snapshot.getValue(GeocodingCoordinates.class);

                    if (geocodingCoordinates != null) {
                        deliveryLocation = new LatLng(geocodingCoordinates.getLatitude(), geocodingCoordinates.getLongitude());
                        placeMarkers();
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    private void updateFirebaseLocation() {
        query.child(deliveryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    user.setCoordinates(new GeocodingCoordinates(deliveryLocation.latitude, deliveryLocation.longitude));
                }

                query.child(deliveryId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Updated admin location.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Map.this, "Something went wrong while updating delivery person's location!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.map = map;
        updateLocationUI();
    }

    public void placeMarkers() {
        map.clear();

        LatLng restaurantLatLng = new LatLng(restaurantLocation.latitude, restaurantLocation.longitude);
        MarkerOptions restaurantOptions = new MarkerOptions().position(restaurantLatLng).title("Restaurant");
        map.addMarker(restaurantOptions);

        if (targetLocation == null) {
            targetLocation = new LatLng(customerCoordinates.getLatitude(), customerCoordinates.getLongitude());
        }

        if (!isAdmin && deliveryLocation != null) {
            MarkerOptions deliveryOptions = new MarkerOptions()
                    .position(deliveryLocation)
                    .title("Delivery Person")
                    .icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(this, R.drawable.ic_baseline_pedal_bike_24)));
            map.addMarker(deliveryOptions);
        }

        MarkerOptions options = new MarkerOptions()
                .position(targetLocation)
                .title("Customer's Home")
                .icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(this, R.drawable.ic_baseline_house_24)));
        LatLngBounds latLngBounds = LatLngBounds.builder().include(restaurantLatLng).include(targetLocation).build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, ZOOM_PADDING_VALUE));
        map.addMarker(options);
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(isAdmin);
                map.getUiSettings().setMyLocationButtonEnabled(isAdmin);
                if (requestingLocationUpdates) {
                    startLocationUpdates();
                }
                fetchFirebaseLocation();
                placeMarkers();
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                targetLocation = null;
            }
        } catch (SecurityException e) {
            System.out.println(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (locationPermissionGranted) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Nullable
    public static Bitmap drawableToBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bmp;
        }

        return null;
    }
}

