package com.example.sunwaysportslink.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookingScheduleActivity extends AppCompatActivity {

    private RecyclerView rvBookingSchedule;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList; // This will hold booking data, e.g., slots or event details.
    private Toolbar toolbar;
    private FirebaseService firebaseService;
    private final List<String> allTimeSlots = Arrays.asList("7:00 AM - 8:00 AM", "8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM", "12:00 PM - 1:00 PM", "1:00 PM - 2:00 PM", "2:00 PM - 3:00 PM", "3:00 PM - 4:00 PM", "4:00 PM - 5:00 PM", "5:00 PM - 6:00 PM", "6:00 PM - 7:00 PM", "7:00 PM - 8:00 PM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking_slots);

        // Initialize the RecyclerView
        rvBookingSchedule = findViewById(R.id.rv_booking_schedule);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("Booking Schedule List");
        }

        // Initialize booking list
        bookingList = new ArrayList<>();

        // Fetch data from the intent (venue and date), or use mock data for now.
        Intent intent = getIntent();
        String selectedVenue = intent.getStringExtra("selectedVenue");
        String selectedDate = intent.getStringExtra("selectedDate");

        if (selectedVenue != null && selectedDate != null) {
            // Assuming you fetch booking slots from a database or API based on selectedVenue and selectedDate
            loadBookings(selectedVenue, selectedDate);
        } else {
            Toast.makeText(this, "No venue or date selected", Toast.LENGTH_SHORT).show();
        }

        // Set up RecyclerView
        bookingAdapter = new BookingAdapter(bookingList);
        rvBookingSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvBookingSchedule.setAdapter(bookingAdapter);
    }

    private void initializeBookingList() {
        bookingList.clear();
        for (String timeSlot : allTimeSlots) {
            bookingList.add(new Booking(timeSlot, null)); // Organizer is null for unbooked slots
        }
    }

    private void loadBookings(String venue, String date) {
        firebaseService = FirebaseService.getInstance();

        // Initialize booking list with all slots
        initializeBookingList();

        // Reference to the bookings database
        DatabaseReference bookingsRef = firebaseService.getReference("Bookings").child(venue).child(date);

        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> bookedTimeSlots = new ArrayList<>();
                    for (DataSnapshot timeSlotSnapshot : snapshot.getChildren()) {
                        String bookedTimeSlot = timeSlotSnapshot.getKey(); // Retrieve booked time slot keys
                        if (bookedTimeSlot != null) {
                            bookedTimeSlots.add(bookedTimeSlot);
                        }
                    }

                    // Fetch event details and match with booked slots
                    fetchEventsAndOrganizers(venue, date, bookedTimeSlots);
                } else {
                    // Notify adapter to display all time slots as unbooked
                    bookingAdapter.notifyDataSetChanged();
                    Toast.makeText(BookingScheduleActivity.this, "No bookings yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(BookingScheduleActivity.this, "Failed to load bookings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEventsAndOrganizers(String venue, String date, List<String> bookedTimeSlots) {
        DatabaseReference eventsRef = firebaseService.getReference("events");

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                        String eventVenue = eventSnapshot.child("venue").getValue(String.class);
                        String eventDate = eventSnapshot.child("date").getValue(String.class);
                        String eventTimeSlot = eventSnapshot.child("startTime").getValue(String.class);
                        String organizer = eventSnapshot.child("createdBy").getValue(String.class);

                        // Check if this event matches the venue, date, and a booked time slot
                        if (venue.equals(eventVenue) && date.equals(eventDate) && bookedTimeSlots.contains(eventTimeSlot)) {
                            for (Booking booking : bookingList) {
                                String rangeStartTime = booking.getTimeSlot().split(" - ")[0];
                                if (rangeStartTime.equals(eventTimeSlot)) {
                                    booking.setBookedBy(organizer != null ? organizer : "Unknown Organizer");
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(BookingScheduleActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                }
                bookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(BookingScheduleActivity.this, "Failed to load events: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Override the back button action
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