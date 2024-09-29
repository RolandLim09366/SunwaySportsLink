package com.example.sunwaysportslink.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sunwaysportslink.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingViewModel extends ViewModel {

    private final MutableLiveData<String> _userEmail = new MutableLiveData<>();
    public LiveData<String> userEmail = _userEmail;

    private final FirebaseAuth mAuth;

    public SettingViewModel() {
        mAuth = FirebaseAuth.getInstance();
        fetchUserEmail();
    }

    // Fetch the user's email from Firebase
    private void fetchUserEmail() {
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            if (email != null && email.contains("@")) {
                // Split the email at the "@" and take the first part
                String username = email.split("@")[0];
                _userEmail.setValue(username); // Set the username instead of the full email
            }
        }
    }

    // Logout function
    public void logout() {
        mAuth.signOut();
    }

}

