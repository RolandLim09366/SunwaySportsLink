package com.example.sunwaysportslink.ui.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.ChatMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {
    private String eventId;
    private DatabaseReference messagesRef;
    private Toolbar toolbar;
    private EditText editTextMessage;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<ChatMessage> messageList;
    private String currentUserId;
    private FirebaseService firebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // Initialize Firebase and current user
        eventId = getIntent().getStringExtra("eventId");
        firebaseService = FirebaseService.getInstance();
        currentUserId = firebaseService.getAuth().getCurrentUser().getUid();
        messagesRef = firebaseService.getEventsRef().child(eventId).child("groupChat").child("messages");

        // Initialize UI components
        toolbar = findViewById(R.id.toolbar);
        editTextMessage = findViewById(R.id.editText_message);
        recyclerViewMessages = findViewById(R.id.recyclerView_messages);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Group Chat");
        }

        // Set up RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Load messages
        loadMessages();

        // Send button click listener
        findViewById(R.id.button_send).setOnClickListener(view -> sendMessage());
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(messageList.size() - 1); // Scroll to last message
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(GroupChatActivity.this, "Failed to load messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String messageId = messagesRef.push().getKey();
        String currentUserId = firebaseService.getAuth().getCurrentUser().getUid();

        if (messageId != null) {
            DatabaseReference userRef = firebaseService.getUserRef(currentUserId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.child("username").getValue(String.class);
                    String profilePictureUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    long timestamp = System.currentTimeMillis();

                    // Create ChatMessage object
                    ChatMessage newMessage = new ChatMessage(messageText, currentUserId, username != null ? username : "Unknown", profilePictureUrl != null ? profilePictureUrl : "", timestamp);

                    messagesRef.child(messageId).setValue(newMessage).addOnSuccessListener(unused -> {
                        editTextMessage.setText(""); // Clear input field
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1); // Scroll to last message

                        DatabaseReference groupChatRef = firebaseService.getEventsRef(eventId).child("groupChat");
                        groupChatRef.child("lastMessage").setValue(messageText);
                        groupChatRef.child("lastMessageTimestamp").setValue(String.valueOf(timestamp));

                    }).addOnFailureListener(e -> {
                        Toast.makeText(GroupChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}