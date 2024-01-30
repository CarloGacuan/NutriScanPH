package com.ninebythree.nutriscanph.model;

public class MealModel {
    private String mealName;
    private String calories;
    private String carbs;
    private String fat;
    private String protein;

    private String time;

    public MealModel(String mealName, String calories, String carbs, String fat, String protein, String time) {
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

    public String getCalories() {
        return calories;
    }

    public String getCarbs() {
        return carbs;
    }

    public String getFat() {
        return fat;
    }

    public String getProtein() {
        return protein;
    }

    public String getTime() {
        return time;
    }
}
