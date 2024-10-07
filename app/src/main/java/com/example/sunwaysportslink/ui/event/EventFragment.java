package com.example.sunwaysportslink.ui.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.sunwaysportslink.databinding.FragmentEventBinding;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.example.sunwaysportslink.ui.home.HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText etParticipantLimit, etDetails;
    private Button btnCreateEvent, etStartTime, etEndTime, etEventDate;
    private Spinner spinnerEventType, spinnerVenue;
    private FragmentEventBinding binding;

    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static com.example.sunwaysportslink.ui.home.HomeFragment newInstance(String param1, String param2) {
        com.example.sunwaysportslink.ui.home.HomeFragment fragment = new com.example.sunwaysportslink.ui.home.HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventBinding.inflate(inflater, container, false);

        // Initialize all UI elements
        etEventDate = binding.etEventDate;
        etStartTime = binding.etEventStartTime;
        etEndTime = binding.etEventEndTime;
        etParticipantLimit = binding.etParticipantLimit;
        etDetails = binding.etDetails;
        btnCreateEvent = binding.btnCreateEvent;
        spinnerEventType = binding.spinnerEventType;
        spinnerVenue = binding.spinnerVenue;

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
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void setUpSpinners() {
        String[] eventTypes = {"Basketball", "Football", "Futsal", "Volleyball", "Tennis"};
        String[] venueOptions = {"Basketball Court Half A", "Basketball Court Half B", "Football Field", "Multi-sports Court", "Volleyball Court", "Tennis Court"};

        spinnerEventType.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, eventTypes));
        spinnerVenue.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, venueOptions));
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
        String organizerName = currentUser.getEmail();

        // Create an Event object
        Event event = new Event(eventTitle, eventDate, eventVenue, eventStartTime, eventEndTime, participantLimit, eventDetails, organizerName);

        event.addJoinedUser(currentUser.getUid()); // Use the user ID instead of email
        Log.d("EventFragment", "Joined Users: " + event.getJoinedUsers().toString()); // Check if the list contains the user


//         Push the event data to the Firebase database
        String eventKey = firebaseService.getEventsRef().push().getKey();  // Retrieve the unique event key from Firebase
        event.setEventKey(eventKey);

        firebaseService.getEventsRef().child(eventKey).setValue(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Success message
                Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                resetInputFields(); // Clear all fields after successful event creation
            } else {
                // Error message
                Toast.makeText(getContext(), "Failed to create event. Try again!", Toast.LENGTH_SHORT).show();
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            // Format the selected date
            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
            etEventDate.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }
}