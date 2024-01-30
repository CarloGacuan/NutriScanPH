package com.ninebythree.nutriscanph.FirebaseTask;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FirebaseSignUp {

    Map<String, Object> userData;
    AuthenticationInterface authenticationInterface;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public FirebaseSignUp(Map<String, Object> userData, AuthenticationInterface authenticationInterface) {
        this.userData = userData;
        this.authenticationInterface = authenticationInterface;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getEmail()).set(userData)
                                .addOnCompleteListener(task1 -> {
                                    if (!task1.isSuccessful()) {
                                        // If Firestore operation fails, delete the created user
                                        firebaseAuth.getCurrentUser().delete().addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                Log.d("FirebaseSignUp", "User creation rolled back due to Firestore failure");
                                            } else {
                                                Log.d("FirebaseSignUp", "Rollback failed: " + task2.getException().getMessage());
                                            }
                                        });
                                        authenticationInterface.onResult(false, task1.getException().getMessage());
                                    } else {
                                        authenticationInterface.onResult(true, "Successfully Registered");
                                    }
                                });
                    } else {
                        Log.d("FirebaseSignUp", "onComplete: " + task.getException().getMessage());
                        authenticationInterface.onResult(false, task.getException().getMessage());
                    }
                });
    }





}
