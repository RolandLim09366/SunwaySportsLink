package com.example.sunwaysportslink.ui.event;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etParticipantLimit, etDetails;
    private Button btnCreateEvent, etEventDate;
    private Spinner spinnerEventType, spinnerVenue, timeSlotSpinner;
    private final String[] timeSlots = {"7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"};
    private boolean isVenueSelected = false;
    private boolean isDateSelected = false;
    private FirebaseService firebaseService;
    private final HashMap<String, List<String>> sportVenueMap = new HashMap<String, List<String>>() {{
        put("Basketball", Arrays.asList("Basketball Court Half A", "Basketball Court Half B"));
        put("Football", Collections.singletonList("Football Field"));
        put("Futsal", Collections.singletonList("Multi-sports Court"));
        put("Volleyball", Collections.singletonList("Volleyball Court"));
        put("Tennis", Collections.singletonList("Tennis Court"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);  // Set the layout file for this activity

        // Initialize all UI elements
        etEventDate = findViewById(R.id.et_event_date);
        etParticipantLimit = findViewById(R.id.et_participant_limit);
        etDetails = findViewById(R.id.et_details);
        btnCreateEvent = findViewById(R.id.btn_create_event);
        spinnerEventType = findViewById(R.id.spinner_event_type);
        spinnerVenue = findViewById(R.id.spinner_venue);
        timeSlotSpinner = findViewById(R.id.spinner_time_slot);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("Create Sport Event");
        }

        firebaseService = FirebaseService.getInstance();

        // Set up spinners
        setUpSpinners();

        // Set up click listeners for date and time pickers
        etEventDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        // Set up create event button click listener
        btnCreateEvent.setOnClickListener(v -> {
            // Validate the input fields first
            if (validateInput()) {
                createEvent();
            } else {
                Toast.makeText(CreateEventActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
        });
        spinnerVenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isVenueSelected = true;
                checkAndLoadAvailableTimeSlots();  // Only call the function if both venue and date are selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Helper function to check both selections and load time slots
    private void checkAndLoadAvailableTimeSlots() {
        if (isVenueSelected && isDateSelected) {
            loadAvailableTimeSlots();
        }
    }

    private void loadAvailableTimeSlots() {
        String selectedVenue = spinnerVenue.getSelectedItem().toString();
        String selectedDate = etEventDate.getText().toString();

        // Ensure that both the venue and date are selected before proceeding
        if (!selectedDate.isEmpty() && !selectedVenue.isEmpty()) {
            DatabaseReference bookingsRef = firebaseService.getReference("Bookings").child(selectedVenue).child(selectedDate);

            bookingsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> bookedSlots = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String bookedSlot = snapshot.getValue(String.class);
                        if (bookedSlot != null) {
                            bookedSlots.add(bookedSlot);
                        }
                    }

                    // Create a new list from the original timeSlots array to avoid modifying the original array
                    List<String> availableSlots = new ArrayList<>(Arrays.asList(timeSlots));

                    // Remove the booked slots
                    availableSlots.removeAll(bookedSlots);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateEventActivity.this, android.R.layout.simple_spinner_item, availableSlots);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    timeSlotSpinner.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(CreateEventActivity.this, "Error loading time slots", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setUpSpinners() {
        String[] eventTypes = {"Basketball", "Football", "Futsal", "Volleyball", "Tennis"};

        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(eventTypeAdapter);

        // Set up event type selection listener
        spinnerEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSportType = spinnerEventType.getSelectedItem().toString();
                List<String> allowedVenues = sportVenueMap.getOrDefault(selectedSportType, new ArrayList<>());

                // Update the venue spinner based on the selected sport type
                ArrayAdapter<String> venueAdapter = new ArrayAdapter<>(CreateEventActivity.this, android.R.layout.simple_spinner_item, allowedVenues);
                venueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerVenue.setAdapter(venueAdapter);

                isVenueSelected = false;  // Reset venue selection state
                checkAndLoadAvailableTimeSlots();  // Reload time slots if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up initial venues based on the first sport type
        List<String> initialVenues = sportVenueMap.get(eventTypes[0]);
        ArrayAdapter<String> initialVenueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, initialVenues);
        initialVenueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVenue.setAdapter(initialVenueAdapter);

        // Populate timeSlotSpinner initially
        ArrayAdapter<String> timeSlotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlots);
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotSpinner.setAdapter(timeSlotAdapter);
    }

    private void createEvent() {
        // Get the entered details from EditTexts
        String eventTitle = spinnerEventType.getSelectedItem().toString();
        String eventDate = etEventDate.getText().toString();
        String eventVenue = spinnerVenue.getSelectedItem().toString();
        String participantLimit = etParticipantLimit.getText().toString();
        String eventDetails = etDetails.getText().toString();
        String selectedTimeSlot = timeSlotSpinner.getSelectedItem().toString();

        // Get the current user (organizer)
        FirebaseUser currentUser = firebaseService.getAuth().getCurrentUser();
        String userId = currentUser.getUid();

        DatabaseReference bookingRef = firebaseService.getReference("Bookings").child(eventVenue).child(eventDate);
        bookingRef.child(selectedTimeSlot).setValue(selectedTimeSlot);

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
                    Event event = new Event(eventTitle, eventDate, eventVenue, selectedTimeSlot, participantLimit, eventDetails, organizerName);
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
        etParticipantLimit.setText("");
        etDetails.setText("");
    }

    private boolean validateInput() {
        // Check if any field is empty
        return spinnerEventType.getSelectedItemPosition() != -1 && !etEventDate.getText().toString().isEmpty() && spinnerVenue.getSelectedItemPosition() != -1 && !etParticipantLimit.getText().toString().isEmpty();
    }

    // Method to show DatePickerDialog for event date
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // Format the selected date to yyyy-MM-dd
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.set(year1, month1, dayOfMonth);
            String formattedDate = sdf.format(calendar.getTime());
            etEventDate.setText(formattedDate);  // Set formatted date to the EditText
            isDateSelected = true;
            loadAvailableTimeSlots();  // Load slots based on the selected date
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