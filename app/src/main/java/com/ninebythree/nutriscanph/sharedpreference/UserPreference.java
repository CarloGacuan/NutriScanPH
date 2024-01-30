package com.ninebythree.nutriscanph.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.ninebythree.nutriscanph.R;

import java.io.ByteArrayOutputStream;

public class UserPreference {

    private static final String PREFS_NAME = "UserPreferences";
    private static final String FULLNAME = "fullname";
    private static final String IMAGEKEY = "imagekey";

    private static final String EMAIL = "email";
    private static final String GENDER = "gender";
    private static final String AGE = "age";
    private static final String HEIGHT = "height";
    private static final String WEIGHT = "weight";
    private static final String IMG_PATH = "img_path";

    private SharedPreferences prefs;
    Context context;
    public UserPreference(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Setters
    public void setFullname(String fullname) {
        prefs.edit().putString(FULLNAME, fullname).apply();
    }

    public void setEmail(String email) {
        prefs.edit().putString(EMAIL, email).apply();
    }

    public void setGender(String gender) {
        prefs.edit().putString(GENDER, gender).apply();
    }

    public void setAge(int age) {
        prefs.edit().putInt(AGE, age).apply();
    }

    public void setHeight(int height) {
        prefs.edit().putInt(HEIGHT, height).apply();
    }

    public void setWeight(int weight) {
        prefs.edit().putInt(WEIGHT, weight).apply();
    }

    public void setImagePath(String path) {
        prefs.edit().putString(IMG_PATH, path).apply();
    }

    public void setImagekey(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        prefs.edit().putString(IMAGEKEY, encodedImage).apply();
    }

    // Getters
    public String getFullname() {
        return prefs.getString(FULLNAME, null);
    }

    public String getEmail() {
        return prefs.getString(EMAIL, null);
    }

    public String getGender() {
        return prefs.getString(GENDER, null);
    }

    public int getAge() {
        return prefs.getInt(AGE, -1);
    }

    public int getHeight() {
        return prefs.getInt(HEIGHT, -1);
    }

    public int getWeight() {
        return prefs.getInt(WEIGHT, -1);
    }
    public String getImagePath() {
        return prefs.getString(IMG_PATH, null);
    }

    public boolean hasImage() {
        // Check if the IMAGEKEY preference has a value
        String encodedImage = prefs.getString(IMG_PATH, null);
        return encodedImage != null;
    }

    public Bitmap getIMAGEKEY() {
        String encodedImage = prefs.getString(IMAGEKEY, null);
        if (encodedImage != null) {
            byte[] byteArray = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            // Return default drawable as Bitmap
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.profile);
        }
    }

    // Clear all preferences
    public void clearPreferences() {
        prefs.edit().clear().apply();
    }
}
