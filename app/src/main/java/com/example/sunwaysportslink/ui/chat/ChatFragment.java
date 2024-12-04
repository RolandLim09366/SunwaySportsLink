package com.example.sunwaysportslink.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.GroupChat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView groupChatRecyclerView;
    private GroupChatAdapter groupChatAdapter;
    private List<GroupChat> groupChatList;
    private DatabaseReference eventsRef;
    private String currentUserId;
    private FirebaseService firebaseService;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize UI components
        groupChatRecyclerView = view.findViewById(R.id.groupChatRecyclerView);
        groupChatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadGroupChats(); // Re-fetch sports news
            swipeRefreshLayout.setRefreshing(false);
        });

        // Initialize Firebase references
        firebaseService = FirebaseService.getInstance();
        currentUserId = firebaseService.getAuth().getCurrentUser().getUid();
        eventsRef = firebaseService.getEventsRef();

        // Initialize group chat list
        groupChatList = new ArrayList<>();
        groupChatAdapter = new GroupChatAdapter(groupChatList, groupChat -> {
            // Handle group chat click
            Intent intent = new Intent(getContext(), GroupChatActivity.class);
            intent.putExtra("eventId", groupChat.getGroupId()); // Pass event ID as group ID
            startActivity(intent);
        });
        groupChatRecyclerView.setAdapter(groupChatAdapter);

        // Load group chats from Firebase
        loadGroupChats();

        return view;
    }

    private void loadGroupChats() {
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatList.clear();

                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    // Get event ID and joined users
                    String eventId = eventSnapshot.getKey();
                    List<String> joinedUsers = new ArrayList<>();
                    for (DataSnapshot userSnapshot : eventSnapshot.child("joinedUsers").getChildren()) {
                        String userId = userSnapshot.getValue(String.class);
                        if (userId != null) {
                            joinedUsers.add(userId);
                        }
                    }

                    // Check if current user is in the joined users list
                    if (joinedUsers.contains(currentUserId)) {
                        // Get group chat details
                        String eventName = eventSnapshot.child("title").getValue(String.class);
                        String eventHost = eventSnapshot.child("createdBy").getValue(String.class);
                        String lastMessage = eventSnapshot.child("groupChat").child("lastMessage").getValue(String.class);
                        String timestampString = eventSnapshot.child("groupChat").child("lastMessageTimestamp").getValue(String.class);

                        // Format the timestamp
                        String formattedTimestamp = "Just now";
                        if (timestampString != null) {
                            try {
                                long timestamp = Long.parseLong(timestampString);
                                formattedTimestamp = formatTimestampToTime(timestamp);
                            } catch (NumberFormatException e) {
                                Log.e("TimestampError", "Invalid timestamp: " + timestampString);
                            }
                        }

                        // Add to group chat list
                        groupChatList.add(new GroupChat(eventId, eventName != null ? eventName + " (Host:" + eventHost + ")" : "Unnamed Event", lastMessage != null ? lastMessage : "No messages yet", formattedTimestamp != null ? formattedTimestamp : "Just now"));
                    }
                }

                // Notify adapter of data changes
                groupChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch events: " + error.getMessage());
            }
        });
    }

    // Helper function to format the timestamp
    private String formatTimestampToTime(long timestamp) {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("h:mm a");
        return dateFormat.format(new java.util.Date(timestamp));
    }
}
