package com.example.sunwaysportslink.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.example.sunwaysportslink.ui.event.CreateEventActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ManageEventsAdapter eventAdapter;
    private List<Event> eventList;
    private AppCompatButton btnAddEvents;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, ManageEventsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        recyclerView = findViewById(R.id.recycler_view_events);
        btnAddEvents = findViewById(R.id.btn_add_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        eventAdapter = new ManageEventsAdapter(eventList, this, (event, position) -> {
            deleteEvent(event, position);
        });
        recyclerView.setAdapter(eventAdapter);

        loadEventsFromDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("Manage Events");       // Set the title of the page
        }

        btnAddEvents.setOnClickListener(v -> {
            // Start activity to add news
            CreateEventActivity.startIntent(ManageEventsActivity.this);
        });
    }

    private void loadEventsFromDatabase() {
        FirebaseService.getInstance().getEventsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void deleteEvent(Event event, int position) {
        // Remove from Firebase database using the event's key
        FirebaseService.getInstance().getEventsRef().child(event.getEventKey()).removeValue().addOnSuccessListener(aVoid -> {
            // After successful deletion, reload the events from Firebase
            loadEventsFromDatabase();
        }).addOnFailureListener(e -> {
            // Handle failure to delete from Firebase
        });
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