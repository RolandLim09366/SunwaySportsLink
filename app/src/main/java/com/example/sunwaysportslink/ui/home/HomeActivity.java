package com.example.sunwaysportslink.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.example.sunwaysportslink.ui.chat.ChatFragment;
import com.example.sunwaysportslink.ui.event.MyEventFragment;
import com.example.sunwaysportslink.ui.search.SearchFragment;
import com.example.sunwaysportslink.ui.setting.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent); // Start the LoginActivity directly
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set listener for item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Default fragment selection
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Ensure the layout adjusts to insets (optional, based on your UI needs)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Fragments to be used
    HomeFragment firstFragment = new HomeFragment();
    SearchFragment secondFragment = new SearchFragment();
    ChatFragment thirdFragment = new ChatFragment();
    MyEventFragment fourthFragment = new MyEventFragment();

    SettingFragment fifthFragment = new SettingFragment();


    // Switch case for fragment transaction based on selected item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.home:
                selectedFragment = firstFragment;
                break;
            case R.id.event:
                selectedFragment = secondFragment;
                break;
            case R.id.chat:
                selectedFragment = thirdFragment;
                break;
            case R.id.create:
                selectedFragment = fourthFragment;
                break;
            case R.id.setting:
                selectedFragment = fifthFragment;
                break;
        }

        // Replace the fragment in the FrameLayout if a fragment is selected
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment).commit();
            return true;
        }

        return false;
    }
}
