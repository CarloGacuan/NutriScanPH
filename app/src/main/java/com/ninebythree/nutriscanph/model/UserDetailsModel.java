package com.ninebythree.nutriscanph.model;

import com.ninebythree.nutriscanph.R;

public class UserDetailsModel {

//    genderRadioGroup = findViewById(R.id.genderRadioGroup);
//    inputEmail = findViewById(R.id.inputEmail);
//    inputFullName = findViewById(R.id.inputFullName);
//    inputAge = findViewById(R.id.inputAge);
//    inputHeight = findViewById(R.id.inputHeight);
//    inputWeight = findViewById(R.id.inputWeight);

    String fullname;
    String email;
    String gender;
    int age;
    int height;
    int weight;

    public UserDetailsModel(String fullname, String email, String gender, int age, int height, int weight) {
        this.fullname = fullname;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }
}
