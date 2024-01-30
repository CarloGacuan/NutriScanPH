package com.ninebythree.nutriscanph.facade;

public class BMI {

    public static double calculate(double weight, double height) {
        double cmToM = height / 100;
        return weight / (cmToM * cmToM);
    }

}
