package com.example.sunwaysportslink.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.ui.login.LoginActivity;

public class AdminHomeActivity extends AppCompatActivity {

    private FirebaseService firebaseService;
    public static void startIntent(Context context) {
        Intent intent = new Intent(context, AdminHomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        firebaseService = FirebaseService.getInstance();

        // Find the views
        CardView manageEventsCard = findViewById(R.id.card_manage_events);
        CardView manageUsersCard = findViewById(R.id.card_manage_users);
        CardView logOutCard = findViewById(R.id.log_out);

        // Set click listeners for the cards
        manageEventsCard.setOnClickListener(v -> {
            // Navigate to the event management screen
            ManageEventsActivity.startIntent(AdminHomeActivity.this);
        });

        manageUsersCard.setOnClickListener(v -> {
//             Navigate to the user management screen
            ManageUsersActivity.startIntent(AdminHomeActivity.this);
        });

        logOutCard.setOnClickListener(v -> {
            firebaseService.getAuth().signOut();
            LoginActivity.startIntent(AdminHomeActivity.this);
            Toast.makeText(AdminHomeActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            AdminHomeActivity.this.finish(); // Close the current activity
        });
    }
}
