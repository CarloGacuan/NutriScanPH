package com.ninebythree.nutriscanph.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.activity.Edit_Profile;
import com.ninebythree.nutriscanph.activity.Profile;
import com.ninebythree.nutriscanph.facade.FDialog;
import com.ninebythree.nutriscanph.object.StillImageActivity;
import com.ninebythree.nutriscanph.sharedpreference.UserPreference;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EditProfile extends Fragment {

    private View view;
    private MaterialButton btnEdit, btnUpdatePhoto;
    private TextInputEditText inputFullName, inputAge, inputHeight, inputWeight;
    private FDialog fDialog;
    private TextView btnSignIn;
    private RadioGroup genderRadioGroup;
    private String selectedGender = ""; // default value

    private ImageView imgProfile;
    private static final int PICK_IMAGE = 1;
    private static final int UPDATE_IMAGE = 2;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100; // Example constant
    UserPreference userPreference;
    SharedPreferences sharedPreferences;
    private Consumer<Nullable> consumer;
    private Uri updatedUri;
    private Uri profileImageUri;

    public EditProfile() {}

    public void setConsumer(Consumer<Nullable> consumer) {
        this.consumer = consumer;
    }

    public static EditProfile newInstance(String param1, String param2) {
        EditProfile fragment = new EditProfile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        inputFullName = view.findViewById(R.id.inputFullName);
        inputAge = view.findViewById(R.id.inputAge);
        inputHeight = view.findViewById(R.id.inputHeight);
        inputWeight = view.findViewById(R.id.inputWeight);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnUpdatePhoto = view.findViewById(R.id.btnUpdatePhoto);
        imgProfile = view.findViewById(R.id.imgProfile);

        btnUpdatePhoto.setOnClickListener(v -> {
            /*
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
            */
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Update profile image"), UPDATE_IMAGE);
        });

        fDialog = new FDialog(view);

        userPreference = new UserPreference(view.getContext());
        inputFullName.setText(userPreference.getFullname());
        inputAge.setText(String.valueOf(userPreference.getAge()));
        inputHeight.setText(String.valueOf(userPreference.getHeight()));
        inputWeight.setText(String.valueOf(userPreference.getWeight()));

        Bitmap originalBitmap = userPreference.getIMAGEKEY();

        if (originalBitmap != null) {
            // Bitmap rotatedBitmap = rotateImageIfRequired(originalBitmap);
            imgProfile.setImageBitmap(originalBitmap);
        } else {
            imgProfile.setImageResource(R.drawable.profile_bg); // Your default image
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
            fDialog.show();
            updateData();
        });

        return view;
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
    }@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UPDATE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fDialog.show();
            updatedUri = data.getData();
            Bitmap updatedBitmap = null;

            try {
                updatedBitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), updatedUri);
                userPreference.setImagekey(updatedBitmap);
                imgProfile.setImageURI(updatedUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Check if the user already has an image stored in Firebase Storage
            if (userPreference.hasImage()) {
                // User has an existing image, update it
                updateImage();
            } else {
                // User doesn't have an image, upload the new one
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        String newImgPath = System.currentTimeMillis() + "";
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        StorageReference updatedRef = FirebaseStorage.getInstance().getReference("profilepics/" + newImgPath + ".jpg");

        if (updatedUri != null) {
            updatedRef.putFile(updatedUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                // Image uploaded successfully
                                // Get the download URL
                                updatedRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Save the download URL to Firebase Authentication
                                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(newImgPath)
                                                .setPhotoUri(uri)
                                                .build();

                                        currentUser.updateProfile(profileUpdate)
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        // Profile image URL updated successfully
                                                    }
                                                });

                                        // Save the image URL or any other necessary information in user preferences
                                        // Assuming setImagePath() saves the image information
                                        userPreference.setImagePath(newImgPath);
                                        fDialog.hide();
                                    }
                                });
                            } else {
                                // Handle failure to upload image
                                fDialog.hide();
                                // Show an error message or handle it appropriately
                            }
                        }
                    });
        }
    }

    private void updateImage() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Get the reference to the existing image in Firebase Storage
        StorageReference existingRef = FirebaseStorage.getInstance().getReference().child(userPreference.getImagePath());

        // Upload the new image to replace the existing one
        existingRef.putFile(updatedUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                // Image uploaded successfully
                // Now, save the download URL to Firebase Authentication
                // Image uploaded successfully
                // Now, save the download URL to Firebase Authentication

                if (task.isSuccessful()) {
                    existingRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.i("UPDATEIMG", "TRIGGER 1: " + userPreference.getImagePath());
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userPreference.getImagePath())
                                    .setPhotoUri(uri)
                                    .build();

                            currentUser.updateProfile(profileUpdate)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            // Profile image URL updated successfully
                                        }
                                    });

                            fDialog.hide();
                        }
                    });
                } else {
                    fDialog.hide();
                }
            }
        });
    }

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), selectedImageUri);
                userPreference.setImagekey(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception
            }
        }
    }
    */

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
                                UserPreference userPreference = new UserPreference(view.getContext());
                                userPreference.setFullname(fullName);
                                userPreference.setAge(age);
                                userPreference.setHeight(height);
                                userPreference.setWeight(weight);
                                userPreference.setGender(selectedGender);

                                Toast.makeText(view.getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                this.consumer.accept(null);
                                fDialog.hide();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(view.getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(view.getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(view.getContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(view.getContext(), "Error fetching document", Toast.LENGTH_SHORT).show();
        });

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