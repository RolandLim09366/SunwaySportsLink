package com.example.sunwaysportslink.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.databinding.FragmentMyEventBinding;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.example.sunwaysportslink.ui.search.EventDetailsActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyEventFragment extends Fragment {

    private FragmentMyEventBinding binding;
    private RecyclerView recyclerView;
    private MyEventAdapter myEventAdapter;
    private final ArrayList<Event> myEventsList = new ArrayList<>();
    private TextView tvNoEvents;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyEventBinding.inflate(inflater, container, false);
        // Set up RecyclerView
        recyclerView = binding.rvMyEvents;
        tvNoEvents = binding.tvNoEvents;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myEventAdapter = new MyEventAdapter(myEventsList, position -> {
            Event event = myEventsList.get(position);
            // Navigate to the EventDetailsActivity
            EventDetailsActivity.startIntent(getContext(), event, true);
        });

        recyclerView.setAdapter(myEventAdapter);

        // Load the user's created events
        loadMyEvents();

        // Set up click listener for the "Add Event" button
        binding.fabAddEvent.setOnClickListener(v -> {
            // Navigate to Create Event page
            Intent intent = new Intent(getContext(), CreateEventActivity.class); // Assuming the CreateEvent page is an activity
            startActivity(intent);
        });

        return binding.getRoot();
    }

    private void loadMyEvents() {
        FirebaseService firebaseService = FirebaseService.getInstance();
        FirebaseUser currentUser = firebaseService.getAuth().getCurrentUser();
        String userId = currentUser.getEmail();

        firebaseService.getEventsRef().orderByChild("createdBy").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myEventsList.clear(); // Clear previous data
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    myEventsList.add(event);
                }
                // Check if the event list is empty
                if (myEventsList.isEmpty()) {
                    tvNoEvents.setVisibility(View.VISIBLE); // Show "No Events" text
                } else {
                    tvNoEvents.setVisibility(View.GONE); // Hide "No Events" text
                }
                myEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload the user's created events
        loadMyEvents();
    }

}
