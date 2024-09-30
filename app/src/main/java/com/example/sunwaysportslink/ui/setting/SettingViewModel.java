package com.example.sunwaysportslink.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingViewModel extends ViewModel {

    private final MutableLiveData<String> _username = new MutableLiveData<>();
    public LiveData<String> username = _username;
    private final MutableLiveData<String> _profileImageUrl = new MutableLiveData<>();
    public LiveData<String> profileImageUrl = _profileImageUrl;
    private final FirebaseAuth mAuth;
    private final DatabaseReference usersRef;

    public SettingViewModel() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersRef = FirebaseDatabase.getInstance("https://sunwaysportslink-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users").child(currentUser.getUid());
            fetchUserDetails();
        } else {
            usersRef = null;
        }
    }

    private void fetchUserDetails() {
        if (mAuth.getCurrentUser() != null) {
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String fetchedUsername = dataSnapshot.child("username").getValue(String.class);
                        String fetchedProfileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                        if (fetchedUsername != null) {
                            _username.setValue(fetchedUsername);
                        }

                        if (fetchedProfileImageUrl != null) {
                            _profileImageUrl.setValue(fetchedProfileImageUrl);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    // Logout function
    public void logout() {
        mAuth.signOut();
    }
}

