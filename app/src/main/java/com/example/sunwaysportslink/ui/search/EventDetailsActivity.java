package com.example.sunwaysportslink.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView tvParticipant;
    private FirebaseService firebaseService;
    private AppCompatButton btnJoinEvent, btnQuitEvent;

    public static void startIntent(Context context, Event event) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra("event", event);  // Pass the entire Event object
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("Event Details");       // Set the title of the page
        }

        // Retrieve the event object from the Intent
        Event event = (Event) getIntent().getSerializableExtra("event");

        // Set them to your TextViews
        TextView tvEventName = findViewById(R.id.tv_event_title);
        TextView tvEventDate = findViewById(R.id.tv_date);
        TextView tvEventVenue = findViewById(R.id.tv_venue);
        TextView tvTime = findViewById(R.id.tv_time);
        tvParticipant = findViewById(R.id.tv_participant);
        TextView tvOrganizer = findViewById(R.id.created_by);


        tvEventName.setText(event.getTitle() + " Casual Play");

        // Get the formatted date with the day of the week
        String formattedDate = getFormattedDate(event.getDate());
        tvEventDate.setText(formattedDate);

        tvEventVenue.setText(event.getVenue());
        tvTime.setText(event.getStartTime() + "-" + event.getEndTime());
        tvParticipant.setText(event.getCurrentParticipants() + "/" + event.getParticipantLimit());
        tvOrganizer.setText(event.getCreatedBy());

        ImageView ivEventImage = findViewById(R.id.iv_sports_banner);
        switch (event.getTitle().toLowerCase()) {
            case "basketball":
                ivEventImage.setImageResource(R.drawable.iv_basketball);
                break;
            case "football":
                ivEventImage.setImageResource(R.drawable.iv_football);
                break;
            case "tennis":
                ivEventImage.setImageResource(R.drawable.iv_tennis);
                break;
            case "futsal":
                ivEventImage.setImageResource(R.drawable.iv_futsal);
                break;
            case "volleyball":
                ivEventImage.setImageResource(R.drawable.iv_volleyball);
                break;
            default:
                ivEventImage.setImageResource(R.drawable.iv_sports);  // Default image
                break;
        }
        firebaseService = FirebaseService.getInstance();

        btnJoinEvent = findViewById(R.id.btn_join_event);
        btnQuitEvent = findViewById(R.id.btn_quit_event);
        btnJoinEvent.setOnClickListener(v -> joinEvent(event));
        btnQuitEvent.setOnClickListener(v -> quitEvent(event));

        FirebaseService firebaseService = FirebaseService.getInstance();
        FirebaseUser currentUser = firebaseService.getAuth().getCurrentUser();
        String userId = currentUser.getUid();
        String eventId = event.getEventKey();// the ID of the event you're displaying
        DatabaseReference eventRef = firebaseService.getEventsRef().child(eventId);
        eventRef.child("joinedUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the joinedUsers list as an ArrayList<String>
                    ArrayList<String> joinedUsers = (ArrayList<String>) snapshot.getValue();

                    // Get the current user ID

                    // Check if the user's ID is in the list
                    if (joinedUsers != null && joinedUsers.contains(userId)) {
                        // User has already joined the event
                        btnJoinEvent.setVisibility(View.GONE);
                        btnQuitEvent.setVisibility(View.VISIBLE);  // Show Quit Event button
                        btnQuitEvent.setEnabled(true);
                    } else {
                        // User has not joined the event
                        btnJoinEvent.setVisibility(View.VISIBLE);
                        btnQuitEvent.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void quitEvent(Event event) {
        FirebaseUser currentUser = firebaseService.getAuth().getCurrentUser();
        String userId = currentUser.getUid();

        DatabaseReference eventRef = firebaseService.getEventsRef().child(event.getEventKey());
        eventRef.child("joinedUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> joinedUsers = (ArrayList<String>) snapshot.getValue();
                if (joinedUsers != null && joinedUsers.contains(userId)) {
                    joinedUsers.remove(userId);
                    eventRef.child("joinedUsers").setValue(joinedUsers).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EventDetailsActivity.this, "You have left the event.", Toast.LENGTH_SHORT).show();
                            // Update UI
                            btnJoinEvent.setVisibility(View.VISIBLE);
                            btnQuitEvent.setVisibility(View.GONE);

                            // Update the participants count
                            String updatedParticipants = String.valueOf(Integer.parseInt(event.getCurrentParticipants()) - 1);
                            event.setCurrentParticipants(updatedParticipants);
                            tvParticipant.setText(updatedParticipants + "/" + event.getParticipantLimit());
                        } else {
                            Toast.makeText(EventDetailsActivity.this, "Failed to quit the event. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    // Method to handle user joining an event
    private void joinEvent(Event event) {
        // Check if the event has already reached the participant limit
        if (Integer.parseInt(event.getCurrentParticipants()) < Integer.parseInt(event.getParticipantLimit())) {
            // Get the current user ID
            FirebaseUser currentUser = firebaseService.getAuth().getCurrentUser();
            String userId = currentUser.getUid();

            DatabaseReference eventRef = firebaseService.getEventsRef().child(event.getEventKey());

            // Add the user's ID to the joinedUsers list
            eventRef.child("joinedUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> joinedUsers = (ArrayList<String>) snapshot.getValue();

                    if (joinedUsers == null) {
                        joinedUsers = new ArrayList<>();
                    }

                    // Add the user ID to the list
                    if (!joinedUsers.contains(userId)) {
                        joinedUsers.add(userId);
                        eventRef.child("joinedUsers").setValue(joinedUsers).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Update current participants
                                String updatedParticipants = String.valueOf(Integer.parseInt(event.getCurrentParticipants()) + 1);
                                eventRef.child("currentParticipants").setValue(updatedParticipants).addOnCompleteListener(participantTask -> {
                                    if (participantTask.isSuccessful()) {
                                        // Update the local event object and UI
                                        event.setCurrentParticipants(updatedParticipants);
                                        tvParticipant.setText(updatedParticipants + "/" + event.getParticipantLimit());
                                        Toast.makeText(EventDetailsActivity.this, "Successfully joined the event!", Toast.LENGTH_SHORT).show();
                                        btnJoinEvent.setVisibility(View.GONE);
                                        btnQuitEvent.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                Toast.makeText(EventDetailsActivity.this, "Failed to join the event. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", error.getMessage());
                }
            });
        } else {
            // Show a message if the event is full
            Toast.makeText(EventDetailsActivity.this, "Sorry, this event is already full!", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to format the date and get the day of the week
    private String getFormattedDate(String eventDate) {
        // Assuming the date is in the format "dd/MM/yyyy"
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(eventDate);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return eventDate; // Return the original date in case of parsing error
    }

    // Handle the back arrow click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to the previous screen (LoginActivity)
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}