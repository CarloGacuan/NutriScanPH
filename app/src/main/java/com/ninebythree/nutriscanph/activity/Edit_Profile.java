package com.ninebythree.nutriscanph.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.sharedpreference.UserPreference;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Edit_Profile extends AppCompatActivity {
    private MaterialButton btnEdit, btnUpdatePhoto;
    private TextInputEditText inputFullName, inputAge, inputHeight, inputWeight;
    private TextView btnSignIn;
    private RadioGroup genderRadioGroup;
    private String selectedGender = ""; // default value

    private ImageView imgProfile;
    private Dialog processingDialog;
    private static final int PICK_IMAGE = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100; // Example constant
    UserPreference userPreference;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        inputFullName = findViewById(R.id.inputFullName);
        inputAge = findViewById(R.id.inputAge);
        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);
        btnEdit = findViewById(R.id.btnEdit);
        btnUpdatePhoto = findViewById(R.id.btnUpdatePhoto);
        imgProfile = findViewById(R.id.imgProfile);

        btnUpdatePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        processingDialog = new Dialog(this);
        processingDialog.setContentView(R.layout.dialog_processing);
        processingDialog.setCancelable(false); // prevent the dialog from being dismissed

        userPreference = new UserPreference(this);

        inputFullName.setText(userPreference.getFullname());
        inputAge.setText(String.valueOf(userPreference.getAge()));
        inputHeight.setText(String.valueOf(userPreference.getHeight()));
        inputWeight.setText(String.valueOf(userPreference.getWeight()));

        Bitmap originalBitmap = userPreference.getIMAGEKEY();


        if (originalBitmap != null) {
            Bitmap rotatedBitmap = rotateImageIfRequired(originalBitmap);
            imgProfile.setImageBitmap(rotatedBitmap);
        } else {
            imgProfile.setImageResource(R.drawable.profile); // Your default image
        }

        selectedGender = userPreference.getGender();

        if(selectedGender.equals("male")){
            genderRadioGroup.check(R.id.maleRadioButton);
        }else if(selectedGender.equals("female")){
            genderRadioGroup.check(R.id.femaleRadioButton);
        } else if(selectedGender.equals("other")){
            genderRadioGroup.check(R.id.otherRadioButton);
        }

        genderSelected();
        btnEdit.setOnClickListener(v -> {
            showProcessingDialog();
            updateData();
        });
    }

    // Apply a fixed rotation if you know all images need it, for example, 90 degrees
    private Bitmap rotateImageIfRequired(Bitmap img) {
        Matrix matrix = new Matrix();
        // Replace 90 with the degree of rotation you need to correct the image
        matrix.postRotate(90);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
            } else {
                // Permission was denied
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                userPreference.setImagekey(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception
            }
        }
    }

    private void updateData() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userEmail = firebaseAuth.getCurrentUser().getEmail(); // Get the logged-in user's email

        // Get the values from the input fields
        String fullName = inputFullName.getText().toString().trim();
        int age = Integer.parseInt(inputAge.getText().toString().trim());
        int height = Integer.parseInt(inputHeight.getText().toString().trim());
        int weight = Integer.parseInt(inputWeight.getText().toString().trim());

        // Reference to the document
        DocumentReference userRef = firebaseFirestore.collection("users").document(userEmail);

        // Fetch the current document
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Assuming userData is an array and we're updating the first item
                List<Map<String, Object>> userDataList = (List<Map<String, Object>>) documentSnapshot.get("userData");
                if (userDataList != null && !userDataList.isEmpty()) {

                    // Update the first item of the array
                    Map<String, Object> userData = userDataList.get(0);
                    userData.put("fullname", fullName);
                    userData.put("age", age);
                    userData.put("height", height);
                    userData.put("weight", weight);
                    userData.put("gender", selectedGender);

                    // Update the document with the new array
                    userRef.update("userData", userDataList)
                            .addOnSuccessListener(aVoid -> {
                                UserPreference userPreference = new UserPreference(this);
                                userPreference.setFullname(fullName);
                                userPreference.setAge(age);
                                userPreference.setHeight(height);
                                userPreference.setWeight(weight);
                                userPreference.setGender(selectedGender);

                                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Edit_Profile.this, Profile.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Error fetching document", Toast.LENGTH_SHORT).show();
        });

    }

    private void showProcessingDialog() {
        processingDialog.show();
    }

    private void hideProcessingDialog() {
        if (processingDialog != null && processingDialog.isShowing()) {
            processingDialog.dismiss();
        }
    }

    private void genderSelected() {
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRadioButton) {
                    selectedGender = "male";
                } else if (checkedId == R.id.femaleRadioButton) {
                    selectedGender = "female";
                } else if (checkedId == R.id.otherRadioButton) {
                    selectedGender = "other";
                }
            }
        });
    }

}