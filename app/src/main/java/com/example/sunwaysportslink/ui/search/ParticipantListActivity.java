package com.example.sunwaysportslink.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.example.sunwaysportslink.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParticipantListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ParticipantListAdapter participantListAdapter;
    private List<User> userList;
    private FirebaseService firebaseService;

    public static void startIntent(Context context, Event event) {
        Intent intent = new Intent(context, ParticipantListActivity.class);
        intent.putExtra("event", event);  // Pass the entire Event object
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_participant_list);

        recyclerView = findViewById(R.id.recyclerViewJoinedUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        participantListAdapter = new ParticipantListAdapter(this, userList);
        recyclerView.setAdapter(participantListAdapter);
        firebaseService = FirebaseService.getInstance();

        Event event = (Event) getIntent().getSerializableExtra("event");
        loadJoinedUsers(event.getEventKey());

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Participant List");
        }
    }

    private void loadJoinedUsers(String eventId) {
        DatabaseReference eventRef = firebaseService.getEventsRef().child(eventId).child("joinedUsers");

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userIdSnap : snapshot.getChildren()) {
                    String userId = userIdSnap.getValue(String.class);

                    DatabaseReference userRef = firebaseService.getUserRef().child(userId);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                userList.add(user);
                                participantListAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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