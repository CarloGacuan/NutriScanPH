package com.ninebythree.nutriscanph.facade;

import android.util.Log;

import java.util.ArrayList;

public class CaloryClassification {

    private String gender;
    private int age;
    private ArrayList<int[]> calories = new ArrayList<>();

    public CaloryClassification(String gender, int age) {
        this.gender = gender;
        this.age = age;
        init();
    }

    public void init() {
        calories.add(new int[]{2400, 3000});
        calories.add(new int[]{2400, 3000});
        calories.add(new int[]{2200, 2800});
        calories.add(new int[]{2200, 2800});
        calories.add(new int[]{2000, 2600});
    }

    public int classify() {
        int index = 0;

        if(age >= 60) index = 4;
        else if(age >= 46) index = 3;
        else if(age >= 26) index = 2;
        else if(age >= 19) index = 1;

        return calories.get(index)[gender.equalsIgnoreCase("female") ? 0 : 1];
    }

}
