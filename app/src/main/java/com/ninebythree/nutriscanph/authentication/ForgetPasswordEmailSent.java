package com.ninebythree.nutriscanph.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ninebythree.nutriscanph.R;


public class ForgetPasswordEmailSent extends AppCompatActivity {


    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_email_sent);


        // Find the ImageView for Back and set an OnClickListener
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(view -> {
            // Intent to WelcomeActivity
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

    }
}