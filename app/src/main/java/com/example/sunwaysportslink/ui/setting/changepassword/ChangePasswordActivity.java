package com.example.sunwaysportslink.ui.setting.changepassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword, etCurrentPassword;
    private Button btnResetPassword;
    private FirebaseService firebaseService;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, com.example.sunwaysportslink.ui.setting.changepassword.ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        firebaseService = FirebaseService.getInstance();

        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnResetPassword = findViewById(R.id.send);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("Create New Password");       // Set the title of the page
        }

        btnResetPassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {

        String password = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call the function to perform password reset via Firebase
        performPasswordReset(password);
    }

    private void performPasswordReset(String newPassword) {

        FirebaseUser user = firebaseService.getAuth().getCurrentUser();

        if (user != null) {
            // Prompt the user to input their current password
            String currentPassword = etCurrentPassword.getText().toString().trim();

            if (TextUtils.isEmpty(currentPassword)) {
                Toast.makeText(this, "Please enter your current password to continue", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the user's email
            String email = user.getEmail();
            if (email == null) {
                Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create an AuthCredential using the email and current password
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

            // Reauthenticate the user
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // If reauthentication is successful, proceed with password update
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                            finish();  // Close the activity after successful password change
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Password reset failed: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Reauthentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the back arrow click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
