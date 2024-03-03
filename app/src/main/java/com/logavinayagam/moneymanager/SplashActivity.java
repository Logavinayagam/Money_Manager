package com.logavinayagam.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is authenticated
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // User is already authenticated, open the main activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // User is not authenticated, open the login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Close the splash activity to prevent going back to it
        finish();
    }
}
