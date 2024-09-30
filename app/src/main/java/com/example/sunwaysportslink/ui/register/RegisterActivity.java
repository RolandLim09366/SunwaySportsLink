package com.example.sunwaysportslink.ui.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.User;
import com.example.sunwaysportslink.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private TextView signInTextView;
    private Button Btn;
    private ProgressBar progressbar;

    private FirebaseService firebaseService;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize FirebaseService singleton
        firebaseService = FirebaseService.getInstance();

        // Initialize UI elements
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        Btn = findViewById(R.id.btnregister);
        progressbar = findViewById(R.id.progressbar);
        signInTextView = findViewById(R.id.tv_sign_in);

        // Set onClickListener for registration button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        // Set onClickListener for sign-in text
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startIntent(RegisterActivity.this);
            }
        });
    }

    private void registerNewUser() {
        progressbar.setVisibility(View.VISIBLE);

        // Get email and password input
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        // Validate input
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToastAndHideProgress("Please enter a valid email address!");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            showToastAndHideProgress("Password must be at least 6 characters long!");
            return;
        }

        // Register new user with Firebase Auth
        firebaseService.getAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Get user ID and save user details to the database
                    String userId = firebaseService.getAuth().getCurrentUser().getUid();
                    User newUser = new User(email);
                    firebaseService.getUserRef(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                LoginActivity.startIntent(RegisterActivity.this);
                            } else {
                                showToastAndHideProgress("Error: " + task.getException().getMessage());
                            }
                        }
                    });
                } else {
                    showToastAndHideProgress("Registration failed! Please try again later");
                }
            }
        });
    }

    private void showToastAndHideProgress(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        progressbar.setVisibility(View.GONE);
    }
}
