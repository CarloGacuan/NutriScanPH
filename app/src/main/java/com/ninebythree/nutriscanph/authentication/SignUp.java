package com.ninebythree.nutriscanph.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ninebythree.nutriscanph.FirebaseTask.AuthenticationInterface;
import com.ninebythree.nutriscanph.FirebaseTask.FirebaseSignUp;
import com.ninebythree.nutriscanph.MainActivity;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.facade.FDialog;
import com.ninebythree.nutriscanph.fragment.Homepage;
import com.ninebythree.nutriscanph.model.UserDetailsModel;
import com.ninebythree.nutriscanph.sharedpreference.UserPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity implements AuthenticationInterface {

    private MaterialButton btnRegister;
    private TextInputEditText inputEmail, inputPassword, inputFullName, inputAge, inputHeight, inputWeight;
    private TextView btnSignIn;
    private RadioGroup genderRadioGroup;
    private String selectedGender = "male"; // default value
    private FDialog fDialog;
    ArrayList<UserDetailsModel> userDetailsModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_sign_up);

        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        btnRegister = findViewById(R.id.btnRegister);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputFullName = findViewById(R.id.inputFullName);
        inputAge = findViewById(R.id.inputAge);
        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);
        btnSignIn = findViewById(R.id.btnSignIn);
        fDialog = new FDialog(this);

        btnRegister.setOnClickListener(v -> {
            fDialog.show();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String fullName = inputFullName.getText().toString().trim();
            int age = Integer.parseInt(inputAge.getText().toString().trim());
            int height = Integer.parseInt(inputHeight.getText().toString().trim());
            int weight = Integer.parseInt(inputWeight.getText().toString().trim());
            validateInput(email, password, fullName, age, height, weight);
        });

        btnSignIn.setOnClickListener(v -> {
            startActivity( new Intent(SignUp.this, Login.class));
        });

        genderSelected();
    }

    private void validateInput(String email, String password, String fullName, int age, int height, int weight) {
        if (email.isEmpty()) {
            inputEmail.setError("Email is required");
            inputEmail.requestFocus();
            fDialog.hide();
            return;
        }

        if (password.isEmpty()) {
            inputPassword.setError("Password is required");
            inputPassword.requestFocus();
            fDialog.hide();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("Please enter a valid email");
            inputEmail.requestFocus();
            fDialog.hide();
            return;
        }

        if (password.length() < 6) {
            inputPassword.setError("Password should be atleast 6 characters long");
            inputPassword.requestFocus();
            fDialog.hide();
            return;
        }


        if (fullName.isEmpty()) {
            inputFullName.setError("Full Name is required");
            inputFullName.requestFocus();
            fDialog.hide();
            return;
        }

        if (inputAge.toString().isEmpty()) {
            inputAge.setError("Age is required");
            inputAge.requestFocus();
            fDialog.hide();
            return;
        }

        if (inputHeight.toString().isEmpty()) {
            inputHeight.setError("Height is required");
            inputHeight.requestFocus();
            fDialog.hide();
            return;
        }

        if (inputWeight.toString().isEmpty()) {
            inputWeight.setError("Weight is required");
            inputWeight.requestFocus();
            fDialog.hide();
            return;
        }

        if (!fullName.isEmpty() && !inputAge.toString().isEmpty() && !inputHeight.toString().isEmpty() && !inputWeight.toString().isEmpty()) {
            // input data in a arraylist
            userDetailsModels.add(new UserDetailsModel(fullName, email, selectedGender, age, height, weight));

            // create a map for the arraylist
            Map<String, Object> user = new HashMap<>();
            user.put("userData", userDetailsModels);

            // create a firebase signup object
            FirebaseSignUp firebaseSignUp = new FirebaseSignUp(user,this);
            firebaseSignUp.signUp(email, password);
        }


    }

    private void genderSelected() {
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRadioButton) {
                    selectedGender = "male";
                } else if (checkedId == R.id.femaleRadioButton) {
                    selectedGender = "female";
                } else if (checkedId == R.id.otherRadioButton) {
                    selectedGender = "other";
                }
            }
        });
    }

    @Override
    public void onResult(Boolean result, String message) {
        if (result) {
            UserPreference userPreference = new UserPreference(this);
            userPreference.setFullname(userDetailsModels.get(0).getFullname());
            userPreference.setEmail(userDetailsModels.get(0).getEmail());
            userPreference.setGender(userDetailsModels.get(0).getGender());
            userPreference.setAge(userDetailsModels.get(0).getAge());
            userPreference.setHeight(userDetailsModels.get(0).getHeight());
            userPreference.setWeight(userDetailsModels.get(0).getWeight());

            startActivity(new Intent(SignUp.this, GetStarted.class));
            finish();
        }else {
            fDialog.hide();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}