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
import com.example.sunwaysportslink.model.User;
import com.example.sunwaysportslink.ui.event.EditEventActivity;
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

    private TextView tvParticipant, tvEventName, tvEventDate, tvEventVenue, tvTime, tvOrganizer, tvDetails;
    private FirebaseService firebaseService;
    private AppCompatButton btnJoinEvent, btnQuitEvent, btnDeleteEvent;
    private ImageView ivEventImage, editIcon;

    public static void startIntent(Context context, Event event, boolean isCreator) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra("event", event);  // Pass the entire Event object
        intent.putExtra("isCreator", isCreator);  // Pass the flag to indicate if the user is the creator
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sport Event Details");
        }

        // Retrieve the event object from the Intent
        Event event = (Event) getIntent().getSerializableExtra("event");
        boolean isCreator = getIntent().getBooleanExtra("isCreator", false);

        // Set them to your TextViews
        tvEventName = findViewById(R.id.tv_event_title);
        tvEventDate = findViewById(R.id.tv_date);
        tvEventVenue = findViewById(R.id.tv_venue);
        tvTime = findViewById(R.id.tv_time);
        tvParticipant = findViewById(R.id.tv_participant);
        tvOrganizer = findViewById(R.id.created_by);
        tvDetails = findViewById(R.id.tv_details);

        tvEventName.setText(event.getTitle() + " Casual Play");

        String formattedDate = getFormattedDate(event.getDate());
        tvEventDate.setText(formattedDate);

        tvEventVenue.setText(event.getVenue());
        tvTime.setText(getOneHourTimeRange(event.getStartTime()));  // Set time range
        tvParticipant.setText(event.getCurrentParticipants() + "/" + event.getParticipantLimit());
        tvOrganizer.setText(event.getCreatedBy());
        tvDetails.setText(event.getDetails() != null && !event.getDetails().isEmpty() ? event.getDetails() : "N/A");

        ivEventImage = findViewById(R.id.iv_sports_banner);
        editIcon = findViewById(R.id.iv_edit);
        btnDeleteEvent = findViewById(R.id.btn_cancel_event);

        // Show the edit icon only if the user is the creator
        if (isCreator) {
            editIcon.setVisibility(View.VISIBLE);
            btnDeleteEvent.setVisibility(View.VISIBLE);  // Show delete button for the organizer
            btnDeleteEvent.setOnClickListener(v -> deleteEvent(event));  // Handle delete action
            editIcon.setOnClickListener(v -> {
                Intent intent = new Intent(EventDetailsActivity.this, EditEventActivity.class);
                intent.putExtra("event", event); // Pass event to the edit activity
                startActivityForResult(intent, 1); // Use startActivityForResult
            });
        } else {
            btnDeleteEvent.setVisibility(View.GONE);  // Hide the delete button for non-creators
            editIcon.setVisibility(View.GONE); // Hide the edit icon for non-creators
        }

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
                ivEventImage.setImageResource(R.drawable.iv_sports);
                break;
        }

        firebaseService = FirebaseService.getInstance();

        btnJoinEvent = findViewById(R.id.btn_join_event);
        btnQuitEvent = findViewById(R.id.btn_quit_event);

        FirebaseUser currentUser = firebaseService.getAuth().getCurrentUser();
        String userId = currentUser.getUid();
        // Fetch user's name or email from the Firebase Database
        firebaseService.getUserRef(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);
                if (user != null) {
                    if (event.getCreatedBy().equals(user.getUsername())) {
                        btnQuitEvent.setText("Organizer");
                        btnQuitEvent.setEnabled(false);  // Disable the join button for the organizer
                    }
                    if (event.isExpired()) {
                        btnJoinEvent.setText("Expired");
                        btnJoinEvent.setEnabled(false);
                        btnQuitEvent.setVisibility(View.GONE);  // Hide quit button since the event is expired
                    } else {
                        btnJoinEvent.setOnClickListener(v -> joinEvent(event));
                        btnQuitEvent.setOnClickListener(v -> quitEvent(event));
                    }
                }
            }
        });

        String eventId = event.getEventKey();
        DatabaseReference eventRef = firebaseService.getEventsRef().child(eventId);
        eventRef.child("joinedUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<String> joinedUsers = (ArrayList<String>) snapshot.getValue();

                    // Check if the user's ID is in the list
                    if (joinedUsers != null && joinedUsers.contains(userId)) {
                        btnJoinEvent.setVisibility(View.GONE);
                        btnQuitEvent.setVisibility(View.VISIBLE);
                        btnQuitEvent.setEnabled(true);
                    } else {
                        btnJoinEvent.setVisibility(View.VISIBLE);
                        btnQuitEvent.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void deleteEvent(Event event) {
        firebaseService.getEventsRef().child(event.getEventKey()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EventDetailsActivity.this, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                finish();  // Navigate back to the previous screen
            } else {
                Toast.makeText(EventDetailsActivity.this, "Failed to delete the event. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getOneHourTimeRange(String startTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // Using hh:mm a for AM/PM
        try {
            Date startDate = timeFormat.parse(startTime);
            if (startDate != null) {
                // Add one hour to the start time
                Date endDate = new Date(startDate.getTime() + 3600000); // 1 hour in milliseconds
                return timeFormat.format(startDate) + " - " + timeFormat.format(endDate); // Format both start and end time with AM/PM
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startTime; // Return original start time if parsing fails
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
                            // Update the currentParticipants in Firebase and UI
                            int updatedParticipantCount = Integer.parseInt(event.getCurrentParticipants()) - 1;
                            eventRef.child("currentParticipants").setValue(String.valueOf(updatedParticipantCount)).addOnCompleteListener(participantTask -> {
                                if (participantTask.isSuccessful()) {
                                    Toast.makeText(EventDetailsActivity.this, "You have left the event.", Toast.LENGTH_SHORT).show();

                                    // Update UI
                                    btnJoinEvent.setVisibility(View.VISIBLE);
                                    btnQuitEvent.setVisibility(View.GONE);

                                    // Update the participants count in the local event object and UI
                                    event.setCurrentParticipants(String.valueOf(updatedParticipantCount));
                                    tvParticipant.setText(updatedParticipantCount + "/" + event.getParticipantLimit());
                                } else {
                                    Toast.makeText(EventDetailsActivity.this, "Failed to update participants. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Retrieve the updated event from the result
            Event updatedEvent = (Event) data.getSerializableExtra("updatedEvent");

            if (updatedEvent != null) {
                // Update the UI with the new event details
                tvEventName.setText(updatedEvent.getTitle() + " Casual Play");
                tvEventDate.setText(getFormattedDate(updatedEvent.getDate()));
                tvEventVenue.setText(updatedEvent.getVenue());
                tvTime.setText(getOneHourTimeRange(updatedEvent.getStartTime()));  // Set time range
                tvParticipant.setText(updatedEvent.getCurrentParticipants() + "/" + updatedEvent.getParticipantLimit());
                tvOrganizer.setText(updatedEvent.getCreatedBy());
                tvDetails.setText(updatedEvent.getDetails());
            }
            // Update the image based on the updated event title
            switch (updatedEvent.getTitle().toLowerCase()) {
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
                    ivEventImage.setImageResource(R.drawable.iv_sports);
                    break;
            }
        }
    }
}