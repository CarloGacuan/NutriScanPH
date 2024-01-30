package com.ninebythree.nutriscanph.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.adapter.MyInterface;
import com.ninebythree.nutriscanph.adapter.NotificationAdapter;
import com.ninebythree.nutriscanph.model.NotificationModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Notification extends Fragment implements MyInterface {

    View view;

    private List<NotificationModel> notificationModels = new ArrayList<>();
    private NotificationAdapter notificationAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_notification, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        notificationAdapter = new NotificationAdapter(getContext(), notificationModels, this);

        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchData();

        return view;


    }


    private void fetchData() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userEmail = firebaseAuth.getCurrentUser().getEmail();
        DocumentReference userRef = firebaseFirestore.collection("logs").document(userEmail);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> meals = (List<Map<String, Object>>) documentSnapshot.get("foods");
                        // Sort the meals list by date in descending order (latest first)
                        meals.sort((meal1, meal2) -> {
                            Timestamp timestamp1 = (Timestamp) meal1.get("date");
                            Timestamp timestamp2 = (Timestamp) meal2.get("date");
                            return timestamp2.compareTo(timestamp1); // Reverse order for latest first
                        });

                        // Loop through the sorted meals
                        for (Map<String, Object> meal : meals) {
                            String food = (String) meal.get("food");
                            long calories = ((Number) meal.get("calories")).longValue();
                            float carbs = ((Number) meal.get("carbs")).floatValue();
                            float fats = ((Number) meal.get("fats")).floatValue();
                            float protein = ((Number) meal.get("protein")).floatValue();
                            Timestamp timestamp = (Timestamp) meal.get("date");

                            // Convert Firebase Timestamp to readable time
                            Date date = timestamp.toDate(); // Convert to Date object
                            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                            String formattedTime = timeFormat.format(date); // Format to time string like "6:00 PM"

                            // Add to notification models
                            notificationModels.add(new NotificationModel("The food you take: " + food + "\nTime: " + formattedTime  + "\nTotal Kcal: " + calories + " calories"));

                        }

                        notificationAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "onFailure: " + e.getMessage());
                });
    }


    @Override
    public void onItemClick(int pos, String categories) {

    }
}