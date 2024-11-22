package com.example.sunwaysportslink.ui.splashscreen;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.ui.admin.AdminHomeActivity;
import com.example.sunwaysportslink.ui.home.HomeActivity;
import com.example.sunwaysportslink.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
                    // User is logged in, set last online time

                    // Check if the logged-in user is the admin
                    if (currentUser.getEmail() != null && currentUser.getEmail().equals("admin123@gmail.com")) {
                        // User is admin, navigate to AdminActivity
                        AdminHomeActivity.startIntent(SplashScreenActivity.this);
                    } else {
                        String lastOnlineTime = getCurrentTime(); // Get the current time as a string
                        FirebaseService.getInstance().getUserRef().child(currentUser.getUid()).child("lastOnlineTime").setValue(lastOnlineTime);
                        // User is a regular user, navigate to HomeActivity
                        HomeActivity.startIntent(SplashScreenActivity.this);
                        loadLocale();
                    }
                } else {
                    // No user is logged in, navigate to LoginActivity
                    LoginActivity.startIntent(SplashScreenActivity.this);
                }
                finish(); // Close the splash screen
            }
        }, SPLASH_DURATION);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void loadLocale() {
        // Get SharedPreferences using the current context
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String language = prefs.getString("languageCode", "en"); // Default to English
        setLocale(language);
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}