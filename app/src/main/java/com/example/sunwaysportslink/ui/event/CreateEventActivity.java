package com.example.sunwaysportslink.ui.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.example.sunwaysportslink.model.User;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etParticipantLimit, etDetails;
    private Button btnCreateEvent, etStartTime, etEndTime, etEventDate;
    private Spinner spinnerEventType, spinnerVenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);  // Set the layout file for this activity

        // Initialize all UI elements
        etEventDate = findViewById(R.id.et_event_date);
        etStartTime = findViewById(R.id.et_event_start_time);
        etEndTime = findViewById(R.id.et_event_end_time);
        etParticipantLimit = findViewById(R.id.et_participant_limit);
        etDetails = findViewById(R.id.et_details);
        btnCreateEvent = findViewById(R.id.btn_create_event);
        spinnerEventType = findViewById(R.id.spinner_event_type);
        spinnerVenue = findViewById(R.id.spinner_venue);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("Create Event");
        }

        // Set up spinners
        setUpSpinners();

        // Set up click listeners for date and time pickers
        etEventDate.setOnClickListener(v -> showDatePickerDialog());
        etStartTime.setOnClickListener(v -> showTimePickerDialog(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePickerDialog(etEndTime));

        // Set up create event button click listener
        btnCreateEvent.setOnClickListener(v -> {
            // Validate the input fields first
            if (validateInput()) {
                createEvent();
            } else {
                Toast.makeText(CreateEventActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpSpinners() {
        String[] eventTypes = {"Basketball", "Football", "Futsal", "Volleyball", "Tennis"};
        String[] venueOptions = {"Basketball Court Half A", "Basketball Court Half B", "Football Field", "Multi-sports Court", "Volleyball Court", "Tennis Court"};

        spinnerEventType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes));
        spinnerVenue.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, venueOptions));
    }

    private void createEvent() {
        // Get the entered details from EditTexts
        String eventTitle = spinnerEventType.getSelectedItem().toString();
        String eventDate = etEventDate.getText().toString();
        String eventStartTime = etStartTime.getText().toString();
        String eventEndTime = etEndTime.getText().toString();
        String eventVenue = spinnerVenue.getSelectedItem().toString();
        String participantLimit = etParticipantLimit.getText().toString();
        String eventDetails = etDetails.getText().toString();

        // Get the FirebaseService instance
        FirebaseService firebaseService = FirebaseService.getInstance();

        // Get the current user (organizer)
        FirebaseUser currentUser = firebaseService.getAuth().getCurrentUser();
        String userId = currentUser.getUid();

        // Retrieve the username from Firebase based on the userId
        firebaseService.getUserRef(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Fetch the User object from the database
                String organizerName;
                User user = task.getResult().getValue(User.class);
                if (user != null) {
                    if (user.getUsername() != null) {
                        organizerName = user.getUsername(); // Use the username instead of email
                    } else {
                        organizerName = user.getEmail();
                    }

                    // Create an Event object
                    Event event = new Event(eventTitle, eventDate, eventVenue, eventStartTime, eventEndTime, participantLimit, eventDetails, organizerName);
                    event.addJoinedUser(userId); // Use the user ID instead of email

                    Log.d("CreateEventActivity", "Joined Users: " + event.getJoinedUsers().toString()); // Check if the list contains the user

                    // Push the event data to the Firebase database
                    String eventKey = firebaseService.getEventsRef().push().getKey();  // Retrieve the unique event key from Firebase
                    event.setEventKey(eventKey);

                    firebaseService.getEventsRef().child(eventKey).setValue(event).addOnCompleteListener(eventTask -> {
                        if (eventTask.isSuccessful()) {

                            // Success message
                            Toast.makeText(CreateEventActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                            resetInputFields(); // Clear all fields after successful event creation
                        } else {
                            // Error message
                            Toast.makeText(CreateEventActivity.this, "Failed to create event. Try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CreateEventActivity.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle failure to retrieve user data
                Toast.makeText(CreateEventActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetInputFields() {
        // Clear all input fields
        spinnerEventType.setSelection(0);
        spinnerVenue.setSelection(0);
        etEventDate.setText("");
        etStartTime.setText("");
        etEndTime.setText("");
        etParticipantLimit.setText("");
        etDetails.setText("");
    }

    private boolean validateInput() {
        // Check if any field is empty
        return spinnerEventType.getSelectedItemPosition() != -1 && !etEventDate.getText().toString().isEmpty() && !etStartTime.getText().toString().isEmpty() && !etEndTime.getText().toString().isEmpty() && spinnerVenue.getSelectedItemPosition() != -1 && !etParticipantLimit.getText().toString().isEmpty();
    }

    // Method to show TimePickerDialog for start time or end time
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
