package com.example.sunwaysportslink.ui.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.ui.home.HomeActivity;
import com.example.sunwaysportslink.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    // Set the duration of the splash screen in milliseconds
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        // Use a Handler to post a delayed action for transitioning to the appropriate activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if a user is already logged in
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is already logged in, navigate to HomeActivity
                    HomeActivity.startIntent(SplashScreenActivity.this);
                } else {
                    // No user is logged in, navigate to LoginActivity
                    LoginActivity.startIntent(SplashScreenActivity.this);
                }
                finish(); // Close the splash screen
            }
        }, SPLASH_DURATION);
    }
}
