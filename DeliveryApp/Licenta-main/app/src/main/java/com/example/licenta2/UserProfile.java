package com.example.licenta2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.licenta2.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Button logout = (Button) findViewById(R.id.signOut);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserProfile.this, MainActivity.class));
                finish();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final TextView fullNameTextView = (TextView) findViewById(R.id.fullNameLabel);
        final TextView fullAddressTextView = (TextView) findViewById(R.id.fullAddressLabel);
        final TextView emailTextView = (TextView) findViewById(R.id.emailLabel);
        final TextView welcomeTextView = (TextView) findViewById(R.id.welcome);

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    String fullAddress = userProfile.fullAddress;
                    String fullName = userProfile.name;
                    String email = userProfile.email;
                    welcomeTextView.setText("Welcome back, " + fullName + "!");
                    fullNameTextView.setText(fullName);
                    fullAddressTextView.setText(fullAddress);
                    emailTextView.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, "Something went wrong, please try again later!", Toast.LENGTH_LONG).show();
            }
        });
    }
}