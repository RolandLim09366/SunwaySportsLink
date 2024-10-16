package com.example.sunwaysportslink.ui.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.ui.admin.AdminHomeActivity;
import com.example.sunwaysportslink.ui.home.HomeActivity;
import com.example.sunwaysportslink.ui.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private TextView enterTextView, signUpTextView, forgotPasswordTextView;
    private ProgressBar progressbar;
    private FirebaseService firebaseService;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent); // Start the LoginActivity directly
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseService singleton
        firebaseService = FirebaseService.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        enterTextView = findViewById(R.id.btn_login);
        progressbar = findViewById(R.id.progressBar);
        signUpTextView = findViewById(R.id.tv_sign_up);
        forgotPasswordTextView = findViewById(R.id.forgot_password);


        // Set on Click Listener on Sign-in button
        enterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to LoginActivity
                RegisterActivity.startIntent(LoginActivity.this);
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordActivity.startIntent(LoginActivity.this);
            }
        });
    }

    private void loginUserAccount() {

        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter all of the information!", Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }

        // Log in the user with Firebase Auth
        firebaseService.getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Check if the email is verified
//                    if (firebaseService.getAuth().getCurrentUser().isEmailVerified()) {
                    // Proceed to the main activity or home screen
                    if (email.equals("admin123@gmail.com") && password.equals("123456")) {
                        // Admin login
                        Toast.makeText(getApplicationContext(), "Admin login successful!", Toast.LENGTH_LONG).show();
                        AdminHomeActivity.startIntent(LoginActivity.this);
                    } else {
                        // Normal user login
                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                        HomeActivity.startIntent(LoginActivity.this);
                    }
                    // Navigate to the main activity (e.g., MainActivity.startIntent(LoginActivity.this))
//                    } else {
//                        // Sign out the user if email is not verified
//                        showToastAndHideProgress("Please verify your email before logging in.");
//                    }
                } else {
                    showToastAndHideProgress("Login failed! Please check your credentials.");
                }
            }

        });
    }

    private void showToastAndHideProgress(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        progressbar.setVisibility(View.GONE);
    }
}

