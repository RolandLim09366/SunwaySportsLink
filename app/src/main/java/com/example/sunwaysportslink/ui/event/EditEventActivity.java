package com.example.sunwaysportslink.ui.event;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
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

public class EditEventActivity extends AppCompatActivity {

    private EditText etParticipantLimit, etDetails;
    private Spinner spinnerEventType, spinnerVenue, timeSlotSpinner;
    private AppCompatButton etEventDate, btnSaveEvent;
    private Event event;  // Event object to store the event being edited
    private ImageView ivSports;
    private FirebaseService firebaseService;
    private final HashMap<String, List<String>> sportVenueMap = new HashMap<String, List<String>>() {{
        put("Basketball", Arrays.asList("Basketball Court Half A", "Basketball Court Half B"));
        put("Football", Collections.singletonList("Football Field"));
        put("Futsal", Collections.singletonList("Multi-sports Court"));
        put("Volleyball", Collections.singletonList("Volleyball Court"));
        put("Tennis", Collections.singletonList("Tennis Court"));
    }};
    private final String[] timeSlots = {"7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"};


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
            getSupportActionBar().setTitle("Edit Sport Event");
        }

        // Initialize Firebase service
        firebaseService = FirebaseService.getInstance();

        // Initialize UI components
        etParticipantLimit = findViewById(R.id.et_participant_limit);
        etDetails = findViewById(R.id.et_details);
        spinnerEventType = findViewById(R.id.spinner_event_type);
        spinnerVenue = findViewById(R.id.spinner_venue);
        etEventDate = findViewById(R.id.et_event_date);
        btnSaveEvent = findViewById(R.id.btn_create_event);
        ivSports = findViewById(R.id.iv_sports);
        timeSlotSpinner = findViewById(R.id.spinner_time_slot);

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

        spinnerVenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAvailableTimeSlots();  // Only call the function if both venue and date are selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
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
            etDetails.setText(event.getDetails() != null && !event.getDetails().isEmpty() ? event.getDetails() : "N/A");
            // Mark date and venue as selected

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

        return true;
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
                ArrayAdapter<String> venueAdapter = new ArrayAdapter<>(EditEventActivity.this, android.R.layout.simple_spinner_item, allowedVenues);
                venueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerVenue.setAdapter(venueAdapter);
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
    }

    private void updateEventDetails() {
        String participantLimit = etParticipantLimit.getText().toString();
        String details = etDetails.getText().toString();
        String date = etEventDate.getText().toString();
        String eventType = spinnerEventType.getSelectedItem().toString();
        String venue = spinnerVenue.getSelectedItem().toString();
        String newTimeSlot = timeSlotSpinner.getSelectedItem().toString();

        // Check if the time slot has changed
        // Reference to the original booked time slot in the database
        DatabaseReference originalSlotRef = firebaseService.getReference("Bookings").child(event.getVenue()).child(event.getDate()).child(event.getStartTime());

        // Remove the original booked time slot
        originalSlotRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update the event object with the new values
                event.setParticipantLimit(participantLimit);
                event.setDetails(details);
                event.setDate(date);
                event.setTitle(eventType);
                event.setVenue(venue);
                event.setStartTime(newTimeSlot);

                // Save the updated event in the database
                DatabaseReference eventRef = firebaseService.getEventsRef().child(event.getEventKey());
                eventRef.setValue(event).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        // Store the new time slot in the database under the selected venue and date
                        DatabaseReference newSlotRef = firebaseService.getReference("Bookings").child(venue).child(date).child(newTimeSlot);
                        newSlotRef.setValue(newTimeSlot);

                        // Notify the user and close the activity
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("updatedEvent", event);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditEventActivity.this, "Failed to update event", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(EditEventActivity.this, "Failed to delete original time slot", Toast.LENGTH_SHORT).show();
            }
        });

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

    // Method to show DatePickerDialog for event date
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            // Format the selected date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.set(year1, monthOfYear, dayOfMonth);
            String formattedDate = sdf.format(calendar.getTime());
            etEventDate.setText(formattedDate);  // Set formatted date to the EditTex
            loadAvailableTimeSlots();
        }, year, month, day);

        datePickerDialog.show();
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

                    // Remove booked slots but keep the current slot if it's already booked
                    if (!bookedSlots.contains(event.getStartTime())) {
                        availableSlots.removeAll(bookedSlots);
                    } else {
                        availableSlots.removeAll(bookedSlots);
                        availableSlots.add(0, "Original Booking Time: " + event.getStartTime()); // Add the current slot at the start
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditEventActivity.this, android.R.layout.simple_spinner_item, availableSlots);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    timeSlotSpinner.setAdapter(adapter);

                    // Set the current time slot as selected
                    timeSlotSpinner.setSelection(getIndexForSpinner(timeSlotSpinner, event.getStartTime()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(EditEventActivity.this, "Error loading time slots", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}