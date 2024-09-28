package com.example.sunwaysportslink.ui.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sunwaysportslink.MainActivity;
import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.ui.loginactivity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private TextView signInTextView;
    private Button Btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    public static Intent startIntent(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        Btn = findViewById(R.id.btnregister);
        progressbar = findViewById(R.id.progressbar);
        signInTextView = findViewById(R.id.tv_sign_in);

        // Set on Click Listener on Registration button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to LoginActivity
                LoginActivity.startIntent(RegisterActivity.this);
            }
        });
    }

    private void registerNewUser() {

        // show the visibility of progress bar to show loading
//        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        // create new user or register new user
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                    // hide the progress bar
                    progressbar.setVisibility(View.GONE);

                    // if the user created intent to login activity
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {

                    // Registration failed
                    Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();

                    // hide the progress bar
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }

}
