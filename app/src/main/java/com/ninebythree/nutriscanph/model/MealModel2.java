package com.ninebythree.nutriscanph.model;

import com.google.firebase.Timestamp;

public class MealModel2 {
    private String mealName;
    private int calories;
    private float carbs;
    private float fat;
    private float protein;

    private Timestamp time;

    public MealModel2(String mealName, int calories, float carbs, float fat, float protein, Timestamp time) {
        this.mealName = mealName;
        this.calories = calories;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
        this.time = time;
    }

    public String getMealName() {
        return mealName;
    }

    public int getCalories() {
        return calories;
    }

    public float getCarbs() {
        return carbs;
    }

    public float getFat() {
        return fat;
    }

    public float getProtein() {
        return protein;
    }

    public Timestamp getTime() {
        return time;
    }
}
