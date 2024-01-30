package com.ninebythree.nutriscanph.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.ninebythree.nutriscanph.R;

public class ForgotPassword extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnSubmit;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        inputEmail = findViewById(R.id.inputEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        // Set OnClickListener for the submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitClicked();
            }
        });

        // Find the ImageView for Back and set an OnClickListener
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(view -> {
            // Intent to WelcomeActivity
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

    }

    private void submitClicked() {
        // Get user input
        String email = inputEmail.getText().toString().trim();

        // Check if email is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Use Firebase Authentication to send a password reset email
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Password reset email sent successfully
                            Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();

                            // Intent to ForgetPasswordEmailSentActivity
                            startActivity(new Intent(getApplicationContext(), ForgetPasswordEmailSent.class));

                            // Finish the current activity
                            finish();
                        } else {
                            // Password reset email failed
                            Toast.makeText(getApplicationContext(), "Failed to send password reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}