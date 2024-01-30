package com.ninebythree.nutriscanph.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.facade.BMI;
import com.ninebythree.nutriscanph.sharedpreference.UserPreference;

import java.util.Locale;

public class Profile extends AppCompatActivity {

    TextView txtName, txtEmail, txtHeight, txtWeight, txtBMI;
    MaterialButton btnEdit;
    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtHeight = findViewById(R.id.txtHeight);
        txtWeight = findViewById(R.id.txtWeight);
        txtBMI = findViewById(R.id.txtBMI);
        btnEdit = findViewById(R.id.btnEdit);
        imgProfile = findViewById(R.id.imgProfile);

        UserPreference userPreference = new UserPreference(this);
        double weight = userPreference.getWeight();
        double height = userPreference.getHeight();

        txtName.setText(userPreference.getFullname());
        txtEmail.setText(userPreference.getEmail());
        txtHeight.setText(height + " cm");
        txtWeight.setText(weight + " kg");

        Bitmap originalBitmap = userPreference.getIMAGEKEY();

        if (originalBitmap != null) {
            Bitmap rotatedBitmap = rotateImageIfRequired(originalBitmap);
            imgProfile.setImageBitmap(rotatedBitmap);
        } else {
            imgProfile.setImageResource(R.drawable.profile); // Your default image
        }

        // calculate BMI
        double bmi = BMI.calculate(weight, height);

        String bmiText = String.format(Locale.getDefault(), "%.2f", bmi);
        txtBMI.setText(bmiText);

        btnEdit.setOnClickListener(view -> {
            // Intent to WelcomeActivity
            startActivity(new Intent(getApplicationContext(), Edit_Profile.class));
            finish();
        });
    }

    private Bitmap rotateImageIfRequired(Bitmap img) {
        Matrix matrix = new Matrix();
        // Replace 90 with the degree of rotation you need to correct the image
        matrix.postRotate(90);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

}