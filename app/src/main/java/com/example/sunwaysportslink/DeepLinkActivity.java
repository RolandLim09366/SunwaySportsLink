package com.example.sunwaysportslink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.example.sunwaysportslink.ui.search.EventDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class DeepLinkActivity extends AppCompatActivity {
    private static final String TAG = "DeepLinkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String intentData = intent.getDataString();
        Log.d(TAG, "Intent received: " + intentData);

        if (intent.getData() != null) {
            String eventKey = intent.getData().getQueryParameter("eventKey");
            if (eventKey != null) {
                loadAndLaunchEvent(eventKey);
                return;
            }
        }

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(this, pendingDynamicLinkData -> {
            if (pendingDynamicLinkData != null) {
                Uri deepLink = pendingDynamicLinkData.getLink();
                Log.d(TAG, "Deep link received: " + deepLink);

                if (deepLink != null) {
                    String eventKey = deepLink.getQueryParameter("eventKey");
                    if (eventKey != null) {
                        loadAndLaunchEvent(eventKey);
                    }
                }
            } else {
                Log.e(TAG, "No dynamic link found");
                finish();
            }
        }).addOnFailureListener(this, e -> {
            Log.e(TAG, "Error retrieving dynamic link", e);
            finish();
        });
    }

    private void loadAndLaunchEvent(String eventKey) {
        FirebaseService firebaseService;
        firebaseService = FirebaseService.getInstance();

        firebaseService.getEventsRef().child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Event event = dataSnapshot.getValue(Event.class);
                    if (event != null) {
                        launchEventDetails(event);
                    } else {
                        Log.e(TAG, "Failed to parse event data");
                        finish();
                    }
                } else {
                    Log.e(TAG, "Event not found: " + eventKey);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error loading event", databaseError.toException());
                finish();
            }
        });
    }

    private void launchEventDetails(Event event) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("event", event);  // Make sure Event class implements Serializable
        startActivity(intent);
        finish();
    }
}