package com.example.sunwaysportslink.ui.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.ui.register.RegisterActivity;

public class SplashScreenActivity extends AppCompatActivity {
    // Set the duration of the splash screen in milliseconds
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Use a Handler to post a delayed action for transitioning to the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity after the splash screen duration
                // Start the RegisterActivity
                Intent intent = RegisterActivity.startIntent(getApplicationContext());
                startActivity(intent);
            }
        }, SPLASH_DURATION);
    }
}