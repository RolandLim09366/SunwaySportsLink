package com.example.sunwaysportslink.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseService {

    private static FirebaseService instance;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private FirebaseService() {
        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance("https://sunwaysportslink-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    }

    // Get a singleton instance of FirebaseService
    public static synchronized FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    // Expose FirebaseAuth
    public FirebaseAuth getAuth() {
        return mAuth;
    }

    // Reinitialize rootRef after the user is authenticated
//    public void initializeRootRef() {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            rootRef = FirebaseDatabase.getInstance("https://sunwaysportslink-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
//        }
//    }

    // Expose a general DatabaseReference (root)
    public DatabaseReference getRootRef() {
        return rootRef;
    }

    // Method to get reference to any node, e.g., "users"
    public DatabaseReference getReference(String refPath) {
        if (rootRef != null) {
            return rootRef.child(refPath);
        }
        return null;
    }

    // Example: Get specific user reference by userId
    public DatabaseReference getUserRef(String userId) {
        return getReference("users").child(userId);
    }

    public DatabaseReference getEventsRef() {
        return getReference("events");
    }
}
