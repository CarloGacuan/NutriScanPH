package com.ninebythree.nutriscanph.FirebaseTask;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ninebythree.nutriscanph.model.UserDetailsModel;

import java.util.List;
import java.util.Map;

public class FirebaseLogin {
    AuthenticationInterface authenticationInterface;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public FirebaseLogin(AuthenticationInterface authenticationInterface) {
        this.authenticationInterface = authenticationInterface;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }



    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getDataList(email);
                    } else {
                        authenticationInterface.onResult(false, task.getException().getMessage());
                    }
                });
    }

    private void getDataList(String email) {
        DocumentReference docRef = firebaseFirestore.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Assuming 'userData' is an array of Maps
                        List<Map<String, Object>> userData = (List<Map<String, Object>>) document.get("userData");
                        if (userData != null && !userData.isEmpty()) {
                            // Fetch the first item of the array
                            Map<String, Object> firstItem = userData.get(0);
                            //  access individual fields, for example:

                            //String fullname, String email, String gender, int age, int height, int weight
                            authenticationInterface.userDatalist.add(
                                    new UserDetailsModel(
                                            (String) firstItem.get("fullname"),
                                            (String) firstItem.get("email"),
                                            (String) firstItem.get("gender"),
                                            ((Long) firstItem.get("age")).intValue(),
                                            ((Long) firstItem.get("height")).intValue(),
                                            ((Long) firstItem.get("weight")).intValue()));
                            Log.d("login", "DocumentSnapshot data: " + authenticationInterface.userDatalist.get(0).getFullname());
                            authenticationInterface.onResult(true, "Successfully Logged In");
                        } else {
                            Log.d("Document", "No such document");
                            firebaseAuth.signOut();
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                        firebaseAuth.signOut();
                    }
                }
            }
        });

}

}
