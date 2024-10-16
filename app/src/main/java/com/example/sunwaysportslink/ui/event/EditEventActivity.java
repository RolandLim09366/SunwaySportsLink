package com.example.sunwaysportslink.ui.event;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity {

    private EditText etParticipantLimit, etDetails;
    private Spinner spinnerEventType, spinnerVenue;
    private AppCompatButton etEventDate, etEventStartTime, etEventEndTime, btnSaveEvent;
    private Event event;  // Event object to store the event being edited
    private ImageView ivSports;
    private FirebaseService firebaseService;

    public static void startIntent(Context context, Event event) {
        Intent intent = new Intent(context, EditEventActivity.class);
        intent.putExtra("event", event);  // Pass the entire Event object
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Event");
        }

        // Initialize Firebase service
        firebaseService = FirebaseService.getInstance();

        // Initialize UI components
        etParticipantLimit = findViewById(R.id.et_participant_limit);
        etDetails = findViewById(R.id.et_details);
        spinnerEventType = findViewById(R.id.spinner_event_type);
        spinnerVenue = findViewById(R.id.spinner_venue);
        etEventDate = findViewById(R.id.et_event_date);
        etEventStartTime = findViewById(R.id.et_event_start_time);
        etEventEndTime = findViewById(R.id.et_event_end_time);
        btnSaveEvent = findViewById(R.id.btn_create_event);
        ivSports = findViewById(R.id.iv_sports);

        setUpSpinners();
        event = (Event) getIntent().getSerializableExtra("event");
        String eventKey = event.getEventKey();

//        // Load the event details into the form
//        loadEventDetails();

        if (eventKey != null) {
            loadEventDetailsFromFirebase(eventKey);
        }

        // Set save button listener
        btnSaveEvent.setOnClickListener(v -> {
            // Update event details in Firebase
            if (validateInput()) {
                updateEventDetails();
            }
        });

        // Set up click listeners for date and time pickers
        etEventDate.setOnClickListener(v -> showDatePickerDialog());
        etEventStartTime.setOnClickListener(v -> showTimePickerDialog(etEventStartTime));
        etEventEndTime.setOnClickListener(v -> showTimePickerDialog(etEventEndTime));
    }

    private void loadEventDetailsFromFirebase(String eventKey) {
        DatabaseReference eventRef = firebaseService.getEventsRef().child(eventKey);

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                event = snapshot.getValue(Event.class);

                if (event != null) {
                    // Load the event details into the form
                    loadEventDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditEventActivity.this, "Failed to load event details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Method to load the current event details into the UI fields
    private void loadEventDetails() {
        if (event != null) {
            etParticipantLimit.setText(event.getParticipantLimit());
            etDetails.setText(event.getDetails());
            etEventDate.setText(event.getDate());
            etEventStartTime.setText(event.getStartTime());
            etEventEndTime.setText(event.getEndTime());

            // Set selected values in the spinners (replace getIndex with your method)
            spinnerEventType.setSelection(getIndexForSpinner(spinnerEventType, event.getTitle()));  // Assuming 'title' refers to event type
            spinnerVenue.setSelection(getIndexForSpinner(spinnerVenue, event.getVenue()));

            switch (event.getTitle().toLowerCase()) {
                case "basketball":
                    ivSports.setImageResource(R.drawable.iv_basketball);
                    break;
                case "football":
                    ivSports.setImageResource(R.drawable.iv_football);
                    break;
                case "tennis":
                    ivSports.setImageResource(R.drawable.iv_tennis);
                    break;
                case "futsal":
                    ivSports.setImageResource(R.drawable.iv_futsal);
                    break;
                case "volleyball":
                    ivSports.setImageResource(R.drawable.iv_volleyball);
                    break;
                default:
                    ivSports.setImageResource(R.drawable.iv_sports);  // Default image
                    break;
            }
        }
    }

    // Method to validate input fields
    private boolean validateInput() {
        if (TextUtils.isEmpty(etParticipantLimit.getText())) {
            Toast.makeText(this, "Participant limit is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etEventDate.getText())) {
            Toast.makeText(this, "Event date is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etEventStartTime.getText())) {
            Toast.makeText(this, "Start time is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etEventEndTime.getText())) {
            Toast.makeText(this, "End time is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setUpSpinners() {
        String[] eventTypes = {"Basketball", "Football", "Futsal", "Volleyball", "Tennis"};
        String[] venueOptions = {"Basketball Court Half A", "Basketball Court Half B", "Football Field", "Multi-sports Court", "Volleyball Court", "Tennis Court"};

        spinnerEventType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes));
        spinnerVenue.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, venueOptions));
    }

    // Method to update event details in Firebase
    private void updateEventDetails() {
        String participantLimit = etParticipantLimit.getText().toString();
        String details = etDetails.getText().toString();
        String date = etEventDate.getText().toString();
        String startTime = etEventStartTime.getText().toString();
        String endTime = etEventEndTime.getText().toString();
        String eventType = spinnerEventType.getSelectedItem().toString();
        String venue = spinnerVenue.getSelectedItem().toString();

        // Update the event object with new values
        event.setParticipantLimit(participantLimit);
        event.setDetails(details);
        event.setDate(date);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setTitle(eventType);
        event.setVenue(venue);

        // Save updated event to Firebase
        DatabaseReference eventRef = firebaseService.getEventsRef().child(event.getEventKey());
        eventRef.setValue(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sendPushNotificationToJoinedUsers(event);
                // Return the updated event to EventDetailsActivity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("updatedEvent", event);
                setResult(RESULT_OK, returnIntent);
                finish();  // Close EditEventActivity
                Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditEventActivity.this, "Failed to update event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPushNotificationToJoinedUsers(Event event) {
        // Assuming you store FCM tokens in joined users
        List<String> tokens = getJoinedUserTokensAsList(event.getJoinedUsersTokens());

        if (tokens != null && !tokens.isEmpty()) {
            for (String token : tokens) {
                // Simulate sending a notification to users who joined the event
                // In a real case, you would send a message to the FCM service
                showLocalNotification(event, token);
            }
        }
    }

    private List<String> getJoinedUserTokensAsList(Map<String, String> joinedUsersTokens) {
        return new ArrayList<>(joinedUsersTokens.values());
    }

    private void showLocalNotification(Event event, String token) {
        // Build a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default").setSmallIcon(R.drawable.ic_notification) // Use an appropriate notification icon
                .setContentTitle("Event Updated: " + event.getTitle()).setContentText("New Date: " + event.getDate() + " Time: " + event.getStartTime()).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);

        // Get the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android Oreo and above, set up the notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Event Updates", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Issue the notification (use token or some other unique ID for the notification ID)
        notificationManager.notify(token.hashCode(), builder.build());
    }


    // Helper method to get the index of a spinner item (assuming values are strings)
    private int getIndexForSpinner(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;  // Default to first item if not found
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to the previous screen (LoginActivity)
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTimePickerDialog(Button timeEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            // Format the selected time
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            timeEditText.setText(selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    // Method to show DatePickerDialog for event date
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            // Format the selected date
            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
            etEventDate.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }
}
