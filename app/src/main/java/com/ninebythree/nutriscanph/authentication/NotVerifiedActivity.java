package com.ninebythree.nutriscanph.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ninebythree.nutriscanph.MainActivity;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.sharedpreference.UserPreference;

public class NotVerifiedActivity extends AppCompatActivity {

    private Button verifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_verified);

        verifyBtn = findViewById(R.id.verifyBtn);

        // Verify Email
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if(currentUser != null) {

                    // EMAIL VERIFICATION
                    currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(NotVerifiedActivity.this,
                                    "Email verification sent, go to your email to check your verified email. Please Re-login for successful verification process.",
                                    Toast.LENGTH_SHORT).show();

                            //TODO LOGOUT
                            mAuth.signOut();
                            UserPreference userPreference = new UserPreference(getApplicationContext());
                            userPreference.clearPreferences();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();
                        }
                    });
                }
            }
        });
    }
}