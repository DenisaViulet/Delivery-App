package com.example.licenta2;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.licenta2.Model.GeocodingCoordinates;
import com.example.licenta2.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Register extends AppCompatActivity implements View.OnClickListener {

    public static final String ADDRESS_SUFFIX = ", Brasov, Romania";

    private GeocodingCoordinates location = new GeocodingCoordinates(44.939893, 26.025409);

    private EditText editTextFullName;
    private EditText editTextEmail;
    private EditText editTextAddress;
    private EditText editTextPassword;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private String email;
    private String password;
    private String name;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        TextView register = (TextView) findViewById(R.id.register_btn);
        register.setOnClickListener(this);
        editTextFullName = (EditText) findViewById(R.id.name);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.passwordRegister);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_btn) {
            if (validateInput()) {
                registerUser();
            }
        }
    }

    private boolean validateInput() {
        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        name = editTextFullName.getText().toString().trim();
        address = editTextAddress.getText().toString().trim();

        if (name.isEmpty()) {
            editTextFullName.setError("Your name is required!");
            editTextFullName.requestFocus();
            return false;
        }
        if (address.isEmpty()) {
            editTextAddress.setError("Your address is required!");
            editTextAddress.requestFocus();
            return false;
        }
        address += ADDRESS_SUFFIX;

        if (email.isEmpty()) {
            editTextEmail.setError("Your email is required!");
            editTextEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide a valid email address!");
            editTextEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Your password is required!");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 5) {
            editTextPassword.setError("The password should not contain less than 5 characters");
            editTextPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser() {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                            try {
                                List<Address> addressList = geocoder.getFromLocationName(address, 1);
                                if (addressList.isEmpty()) {
                                    throw new IOException("No valid address found.");
                                }
                                location = new GeocodingCoordinates(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                            } catch (IOException e) {
                                System.err.println(e.getMessage());
                                FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(command -> {
                                    if (command.isSuccessful()) {
                                        System.out.println("User was deleted due to invalid address.");
                                        editTextAddress.setError("Please input a valid address!");
                                        editTextAddress.requestFocus();
                                    }
                                });
                                return;
                            }

                            User user = new User(name, email, address, false, location);
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdateRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(firebaseUser.getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        firebaseUser.updateProfile(profileUpdateRequest)
                                                .addOnCompleteListener(listener -> {
                                                   if (listener.isSuccessful()) {
                                                       Toast.makeText(Register.this,
                                                               "User displayName successfully updated",
                                                               Toast.LENGTH_LONG).show();
                                                   }
                                                });
                                        Toast.makeText(Register.this,"User has been successfully registered",Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Register.this,"Failed to register!",Toast.LENGTH_LONG).show();
                                    }

                                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                                }
                            });
                        } else {
                            Toast.makeText(Register.this,"Failed to register!",Toast.LENGTH_LONG).show();
                            runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                        }
                    }
                });
    }
}