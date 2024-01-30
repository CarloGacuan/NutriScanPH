package com.ninebythree.nutriscanph.fragment;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.facade.BMI;
import com.ninebythree.nutriscanph.facade.BMIClassification;
import com.ninebythree.nutriscanph.sharedpreference.UserPreference;

import java.util.Locale;
import java.util.function.Consumer;
public class Profile extends Fragment {

    // changeable background & color instance
    private View mainBg, BMIlayout;
    private TextView txtBMI, txtClassify;

    // view instance
    private View view;
    private NavigationView navigationView;
    private TextView txtName, txtEmail, txtAge, txtHeight, txtWeight;
    private MaterialButton btnEdit;
    private ImageView imgProfile;
    private Fragment selectedFragment;
    private Consumer<Nullable> consumer;

    public Profile() {}

    public void setConsumer(Consumer<Nullable> consumer) {
        this.consumer = consumer;
    }

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mainBg = view.findViewById(R.id.mainBg);
        BMIlayout = view.findViewById(R.id.BMIlayout);
        txtClassify = view.findViewById(R.id.txtClassify);

        navigationView = view.findViewById(R.id.nav_view);
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtAge = view.findViewById(R.id.txtAge);
        txtHeight = view.findViewById(R.id.txtHeight);
        txtWeight = view.findViewById(R.id.txtWeight);
        txtBMI = view.findViewById(R.id.txtBMI);
        btnEdit = view.findViewById(R.id.btnEdit);
        imgProfile = view.findViewById(R.id.imgProfile);

        UserPreference userPreference = new UserPreference(view.getContext());
        int age = userPreference.getAge();
        double weight = userPreference.getWeight();
        double height = userPreference.getHeight();

        txtName.setText(userPreference.getFullname());
        txtEmail.setText(userPreference.getEmail());
        txtAge.setText(String.valueOf(age));
        txtHeight.setText(height + " cm");
        txtWeight.setText(weight + " kg");

        Bitmap originalBitmap = userPreference.getIMAGEKEY();

        if (originalBitmap != null) {
            // Bitmap rotatedBitmap = rotateImageIfRequired(originalBitmap);
            imgProfile.setImageBitmap(originalBitmap);
        } else {
            imgProfile.setImageResource(R.drawable.profile_bg); // Your default image
        }

        // calculate BMI
        double bmi = BMI.calculate(weight, height);

        String bmiText = String.format(Locale.getDefault(), "%.2f", bmi);
        txtBMI.setText(bmiText);

        // classify BMI
        BMIClassification bmiClassification = new BMIClassification(bmi);
        mainBg.setBackgroundResource(bmiClassification.getColorID());
        BMIlayout.setBackgroundResource(bmiClassification.getBgID());
        txtBMI.setTextColor(getResources().getColor(bmiClassification.getColorID()));
        txtClassify.setText(bmiClassification.classifyAs());

        btnEdit.setOnClickListener(view -> {
            this.consumer.accept(null);
        });

        return view;
    }

    private Bitmap rotateImageIfRequired(Bitmap img) {
        Matrix matrix = new Matrix();
        // Replace 90 with the degree of rotation you need to correct the image
        matrix.postRotate(90);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }
}