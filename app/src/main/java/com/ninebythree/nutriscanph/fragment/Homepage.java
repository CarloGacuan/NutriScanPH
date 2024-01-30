package com.ninebythree.nutriscanph.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.adapter.MealsAdapter;
import com.ninebythree.nutriscanph.adapter.MyInterface;
import com.ninebythree.nutriscanph.detection.DetectorActivity;
import com.ninebythree.nutriscanph.facade.CaloryClassification;
import com.ninebythree.nutriscanph.facade.FDialog;
import com.ninebythree.nutriscanph.model.MealModel;
import com.ninebythree.nutriscanph.model.MealModel2;
import com.ninebythree.nutriscanph.object.StillImageActivity;
import com.ninebythree.nutriscanph.sharedpreference.UserPreference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Homepage extends Fragment implements MyInterface {
    public static Homepage mainActivity;
    private static final String calorieInfoFile = "calorie_info.txt";
    private static final String carbsInfoFile = "carbs_info.txt";
    private static final String fatInfoFile = "fat_info.txt";
    private static final String proteinInfoFile = "protein_info.txt";
    private static HashMap<String, Integer> calorieInfo = new HashMap<>();
    private static HashMap<String, Float> carbsInfo = new HashMap<>();
    private static HashMap<String, Float> fatInfo = new HashMap<>();
    private static HashMap<String, Float> proteinInfo = new HashMap<>();
    private FloatingActionButton upload;
    private RecyclerView recycler_meals;
    private MealsAdapter exerciseAdapter;
    private List<MealModel> mealsModel = new ArrayList<>();
    private TextView total, txtPercentage, progressBar_total, totalCarbs, totalProtein, totalFats, txtProgress;
    private ProgressBar progressBar;
    private View view;
    private FDialog fDialog;
    private CaloryClassification caloryClassification;
    private int goalCalories;

    // INTENT
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_homepage, container, false);

        recycler_meals = view.findViewById(R.id.recycler_meals);

        // initiate
        total = (TextView) view.findViewById(R.id.total);
        progressBar_total = (TextView) view.findViewById(R.id.progressBar_total);
        totalCarbs = (TextView) view.findViewById(R.id.txtTotalCarbs);
        totalProtein = (TextView) view.findViewById(R.id.txtTotalProtein);
        totalFats = (TextView) view.findViewById(R.id.txtTotalFats);
        txtProgress = (TextView) view.findViewById(R.id.txtProgress3);
        txtPercentage = (TextView) view.findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar3);
        upload  =  view.findViewById(R.id.upload);
        fDialog = new FDialog(view);
        fDialog.show();

        // fetchData();
        UserPreference userPreference = new UserPreference(view.getContext());
        String gender = userPreference.getGender();
        int age = userPreference.getAge();
        caloryClassification = new CaloryClassification(gender, age);

        goalCalories = caloryClassification.classify();
        txtProgress.setText(String.valueOf(goalCalories));
        progressBar.setMax(goalCalories);
        mainActivity = this;

        try {
            calorieInfo = loadCalorieInfo(calorieInfoFile);
            carbsInfo = loadCarbsInfo(carbsInfoFile);
            fatInfo = loadCarbsInfo(fatInfoFile);
            proteinInfo = loadCarbsInfo(proteinInfoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DetectorActivity.class));
            }
        });

        upload.setOnClickListener(v->
            startActivity(new Intent(getContext(), StillImageActivity.class))
        );

        exerciseAdapter = new MealsAdapter(getContext(), mealsModel, this);
        recycler_meals.setAdapter(exerciseAdapter);
        recycler_meals.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public void filipinoFood() {

        this.intent = Objects.requireNonNull(mainActivity.getActivity()).getIntent();
        if(intent == null) return;

        String food = intent.getStringExtra("food");
        if(food == null) return;

        int calories = 0;
        float carbs = 0;
        float fats = 0;
        float protein = 0;

        /*
        String[] classes = {
                "Adobong Manok",
                "Ginisang sayote",
                "Menudo", "Sinigang",
                "Mushroom Soup",
                "Pandesal",
                "SunnySide Up Egg",
                "White Cooked Rice"
        };
         */

        if(food.equals("Adobong Manok")){
            fats =  10;
            protein =  10;
            carbs =  20;
            calories =  200;
        } else if (food.equals("Ginisang sayote")) {
            fats =  0.5f;
            protein =  10;
            carbs =  15;
            calories =  50;
        } else if (food.equals("Menudo")) {
            fats =  10;
            protein =  5;
            carbs =  25;
            calories =  250;
        } else if (food.equals("Sinigang")) {
            fats =  47;
            protein =  34;
            carbs =  9;
            calories =  290;
        } else if (food.equals("Mushroom Soup")) {
            fats =  2;
            protein =  2;
            carbs =  10;
            calories = 100;
        } else if (food.equals("Pandesal")) {
            fats =  3;
            protein =  2;
            carbs =  20;
            calories =  73;
        } else if (food.equals("SunnySide Up Egg")) {
            fats =  6;
            protein =  6;
            carbs =  0.5f;
            calories =  68;
        } else if (food.equals("White Cooked Rice")) {
            fats =  0.5f;
            protein =  4;
            carbs =  45;
            calories =  200;
        }

        // getting total calories
        int cur = Integer.parseInt(total.getText().toString());
        cur += calories;
        total.setText(cur + "");

        // getting total carbs
        float curCarbs = Float.parseFloat(totalCarbs.getText().toString());
        curCarbs += carbs;
        totalCarbs.setText(String.format("%.0f", curCarbs));

        // getting total fats
        float curFats = Float.parseFloat(totalFats.getText().toString());
        curFats += fats;
        totalFats.setText(String.format("%.0f", curFats));

        // getting total protein
        float curProteins = Float.parseFloat(totalProtein.getText().toString());
        curProteins += protein;
        totalProtein.setText(String.format("%.0f", curProteins));
        progressBar_total.setText("Current calories: " + cur + "");
        txtPercentage.setText(String.format("Intake food at %.1f%%", (float) cur / goalCalories * 100));
        progressBar.setProgress(cur);

        long tsLong = (long) (System.currentTimeMillis() / 1000);
        java.util.Date d = new java.util.Date(tsLong * 1000L);
        String ts = new SimpleDateFormat("h:mm a").format(d);

        mealsModel.add(new MealModel(food, String.valueOf(calories), "", "", "", ts));
        savedToFireStore(food, calories, carbs, fats, protein, ts);
        exerciseAdapter.notifyDataSetChanged();

        this.intent = null;
        mainActivity.getActivity().setIntent(null);
    }

    public void fetchData() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // Get the current user's email as the document key
        String userEmail = firebaseAuth.getCurrentUser().getEmail();
        DocumentReference userRef = firebaseFirestore.collection("logs").document(userEmail);

        // Get today's date at midnight
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the list of meals
                        List<Map<String, Object>> meals = (List<Map<String, Object>>) documentSnapshot.get("foods");

                        long totalCaloriesValue = 0;
                        float totalCarbsValue = 0;
                        float totalFatsValue = 0;
                        float totalProteinValue = 0;

                        mealsModel.clear();

                        // Loop through the meals
                        for (Map<String, Object> meal : meals) {
                            Timestamp timestamp = (Timestamp) meal.get("date");

                            // Check if the timestamp is from today
                            if (timestamp.toDate().after(today) && timestamp.toDate().before(new Date())) {
                                String food = (String) meal.get("food");
                                int calories = ((Number) meal.get("calories")).intValue();
                                float carbs = ((Number) meal.get("carbs")).floatValue();
                                float fats = ((Number) meal.get("fats")).floatValue();
                                float protein = ((Number) meal.get("protein")).floatValue();

                                totalCaloriesValue += calories;
                                totalCarbsValue += carbs;
                                totalFatsValue += fats;
                                totalProteinValue += protein;

                                // Create a MealModel2 object
                                mealsModel.add(new MealModel(food, String.valueOf(calories), String.valueOf(carbs), String.valueOf(fats), String.valueOf(protein), String.valueOf(timestamp)));
                            }
                        }

                        txtPercentage.setText(String.format("Intake food at %.1f%%", (float)  totalCaloriesValue / goalCalories * 100));
                        progressBar.setProgress((int) totalCaloriesValue);
                        total.setText(String.valueOf(totalCaloriesValue));
                        totalCarbs.setText(String.format("%.0f", totalCarbsValue));
                        totalFats.setText(String.format("%.0f", totalFatsValue));
                        totalProtein.setText(String.format("%.0f", totalProteinValue));

                        // Notify the adapter that the data has changed
                        exerciseAdapter.notifyDataSetChanged();

                        // This intent will only use once StillImageActivity translate into MainActivity
                        filipinoFood();
                    }

                    fDialog.hide();
                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "onFailure: " + e.getMessage());
                    fDialog.hide();
                });
    }

    public static Homepage getInstance() {
        return mainActivity;
    }

    public void addFood(String food) {
        int calories = getCalorie(food);
        float carbs = getCarbs(food);
        float fats = getFats(food);
        float protein = getProteins(food);

        int cur = Integer.parseInt(total.getText().toString());
        cur += calories;
        total.setText(cur + "");

        // getting total carbs
        float curCarbs = Float.parseFloat(totalCarbs.getText().toString());
        curCarbs += carbs;
        totalCarbs.setText(String.format("%.0f", curCarbs));

        // getting total fats
        float curFats = Float.parseFloat(totalFats.getText().toString());
        curFats += fats;
        totalFats.setText(String.format("%.0f", curFats));

        // getting total protein
        float curProteins = Float.parseFloat(totalProtein.getText().toString());
        curProteins += protein;
        totalProtein.setText(String.format("%.0f", curProteins));

        progressBar_total.setText("Current calories: " + cur + "");
        progressBar.setProgress(cur);

        TableLayout t1 = (TableLayout) view.findViewById(R.id.tablelayout);
        TableRow tr = new TableRow(getContext());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        TextView date = new TextView(getContext());
        TextView foodItem = new TextView(getContext());
        TextView cals = new TextView(getContext());

        long tsLong = (long) (System.currentTimeMillis() / 1000);
        java.util.Date d = new java.util.Date(tsLong * 1000L);
        String ts = new SimpleDateFormat("h:mm a").format(d);
        date.setText(ts);
        foodItem.setText(food);
        cals.setText(calories + "");
        date.setGravity(Gravity.CENTER);
        foodItem.setGravity(Gravity.CENTER);
        cals.setGravity(Gravity.CENTER);

        date.setLayoutParams(new TableRow.LayoutParams(0));
        foodItem.setLayoutParams(new TableRow.LayoutParams(1));
        cals.setLayoutParams(new TableRow.LayoutParams(2));
        date.getLayoutParams().width = 0;
        foodItem.getLayoutParams().width = 0;
        cals.getLayoutParams().width = 0;
        tr.addView(date);
        tr.addView(foodItem);
        tr.addView(cals);
        t1.addView(tr);

        mealsModel.add(new MealModel(food, String.valueOf(calories), "", "", "", ts));
        savedToFireStore(food, calories, carbs, fats, protein, ts);
        exerciseAdapter.notifyDataSetChanged();
    }

    public int getCalorie(String food)
    {
        if (calorieInfo.get(food) != null)
        {
            return calorieInfo.get(food);
        }
        return 0;
    }
    public float getCarbs(String food)
    {
        if(carbsInfo.get(food) != null)
        {
            return carbsInfo.get(food);
        }
        return 0.0f;
    }
    public float getFats(String food)
    {
        if(fatInfo.get(food) != null)
        {
            return fatInfo.get(food);
        }
        return 0.0f;
    }
    public float getProteins(String food)
    {
        if(proteinInfo.get(food) != null)
        {
            return proteinInfo.get(food);
        }
        return 0.0f;
    }

    private HashMap loadCalorieInfo(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getActivity()).getAssets().open(filename)));
        HashMap<String, Integer> calCounts = new HashMap<>();

        String line;
        while ((line = reader.readLine()) != null) {
            //Log.d("DEBUG", line);
            calCounts.put(line.split(": ")[0], Integer.parseInt(line.split(": ")[1]));
        }
        return calCounts;
    }

    private HashMap loadCarbsInfo(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getActivity()).getAssets().open(filename)));
        HashMap<String, Float> calCounts = new HashMap<>();

        String line;
        while ((line = reader.readLine()) != null) {
            //Log.d("DEBUG", line);
            calCounts.put(line.split(": ")[0], Float.parseFloat(line.split(": ")[1]));
        }
        return calCounts;
    }

    private void savedToFireStore(String food, int calories, float carbs, float fats, float protein, String timestamp) {
        fDialog.show();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Timestamp currentTimestamp = Timestamp.now();

        // Get the current user's email as the document key
        String userEmail = firebaseAuth.getCurrentUser().getEmail();
        DocumentReference userRef = firebaseFirestore.collection("logs").document(userEmail);

        // Prepare the meal data to be saved
        Map<String, Object> user = new HashMap<>();
        user.put("food", food);
        user.put("calories", calories);
        user.put("carbs", carbs);
        user.put("fats", fats);
        user.put("protein", protein);
        user.put("date", currentTimestamp);

        // Check if the document exists
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists, so we update it
                    userRef.update("foods", FieldValue.arrayUnion(user))
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Document successfully updated");
                            })
                            .addOnFailureListener(e -> {
                                Log.d("Firestore", "Error updating document", e);
                            });
                } else {
                    // Document does not exist, we create it with the first meal entry
                    Map<String, Object> initialData = new HashMap<>();
                    initialData.put("foods", Collections.singletonList(user)); // Create a list with the first meal
                    userRef.set(initialData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Document successfully created");
                            })
                            .addOnFailureListener(e -> {
                                Log.d("Firestore", "Error creating document", e);
                            });
                }
            } else {
                Log.d("Firestore", "Failed with: ", task.getException());
            }

            fDialog.hide();
        });
    }

    @Override
    public void onItemClick(int pos, String categories) {}
}