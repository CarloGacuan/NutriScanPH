package com.ninebythree.nutriscanph.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ninebythree.nutriscanph.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feedback extends Fragment {
    View view;
    private int star = 0;
    private MaterialButton btnSubmit;
    private EditText inputFeedback;
    private RatingBar ratingBar;
    private TextView txtThankyou;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_feedback, container, false);
        txtThankyou = view.findViewById(R.id.txtThankyou);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // You can now use the 'rating' variable which will be 3.0 for 3 stars, etc.
                // Here you can do something with the selected rating.
                star = (int) rating;
            }
        });

        btnSubmit = view.findViewById(R.id.btnSubmit);
        inputFeedback = view.findViewById(R.id.inputFeedback);

        btnSubmit.setOnClickListener(view1 -> {

            if (star == 0) {
                Toast.makeText(getContext(), "Minimum 1 star", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputFeedback.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please input your Feedbacl", Toast.LENGTH_SHORT).show();
                return;
            }

            feedback();
        });

        return view;
    }

    private void feedback() {
        Toast.makeText(getContext(), "Thank you for your feedback", Toast.LENGTH_SHORT).show();

        HashMap<String, Object> feedback = new HashMap<>();
        feedback.put("star", star);
        feedback.put("feedback", inputFeedback.getText().toString());

        HashMap<String, Object> data = new HashMap<>();
        data.put("feedback", feedback);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentEmail = firebaseAuth.getCurrentUser().getEmail();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

       DocumentReference documentReference =  firestore.collection("feedback").document(currentEmail);

       documentReference.set(data);

    }




    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference documentReference = firestore.collection("feedback").document(currentEmail);


        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {


                        Map<String, Object> feedbackMap = (Map<String, Object>) document.get("feedback");

                        if (feedbackMap != null) {
                            // Extract the feedback text and star rating
                            String feedbackText = (String) feedbackMap.get("feedback");
                            Long starRating = (Long) feedbackMap.get("star");
                            // Use the retrieved values as needed
                            
                            txtThankyou.setVisibility(View.VISIBLE);
                            inputFeedback.setText(feedbackText);
                            ratingBar.setRating(starRating);
                            btnSubmit.setEnabled(false);
                            btnSubmit.setVisibility(View.GONE);
                            inputFeedback.setEnabled(false);
                        }


                        // Now you can use these values as needed in your app
                    } else {
                        Toast.makeText(getContext(), "You can give a feedback", Toast.LENGTH_SHORT).show();
                        btnSubmit.setEnabled(true);
                        inputFeedback.setEnabled(true);
                        txtThankyou.setVisibility(View.GONE);
                    }
                } else {
                    // Handle the error
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "You can give a feedback", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                inputFeedback.setEnabled(true);
                txtThankyou.setVisibility(View.GONE);
            }
        });



    }

}