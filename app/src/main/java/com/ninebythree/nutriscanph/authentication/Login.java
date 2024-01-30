package com.ninebythree.nutriscanph.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ninebythree.nutriscanph.FirebaseTask.AuthenticationInterface;
import com.ninebythree.nutriscanph.FirebaseTask.FirebaseLogin;
import com.ninebythree.nutriscanph.MainActivity;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.facade.FDialog;
import com.ninebythree.nutriscanph.fragment.Homepage;
import com.ninebythree.nutriscanph.sharedpreference.UserPreference;

public class Login extends AppCompatActivity implements AuthenticationInterface {

    private MaterialButton btnLogin, btnGoogle;
    private TextInputEditText inputEmail, inputPassword;
    private TextView btnForgotPassword, btnSignUp;
    private FDialog fDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnSignUp = findViewById(R.id.btnSignup);
        fDialog = new FDialog(this);

        btnLogin.setOnClickListener(v -> {
            fDialog.show();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            validateInput(email, password);
        });

        btnGoogle.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Google Sign In", Toast.LENGTH_SHORT).show());
        btnForgotPassword.setOnClickListener(v -> startActivity(new Intent(Login.this, ForgotPassword.class)));

        btnSignUp.setOnClickListener(v -> {
            startActivity( new Intent(Login.this, SignUp.class));
        });


    }

    private void validateInput(String email, String password) {
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

        if (!email.isEmpty() && !password.isEmpty()) {
            FirebaseLogin firebaseLogin = new FirebaseLogin(this);
            firebaseLogin.login(email, password);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onResult(Boolean result, String message) {
        if(result){
            UserPreference userPreference = new UserPreference(getApplicationContext());
            userPreference.setFullname(userDatalist.get(0).getFullname());
            userPreference.setEmail(userDatalist.get(0).getEmail());
            userPreference.setGender(userDatalist.get(0).getGender());
            userPreference.setAge(userDatalist.get(0).getAge());
            userPreference.setHeight(userDatalist.get(0).getHeight());
            userPreference.setWeight(userDatalist.get(0).getWeight());
            startActivity(new Intent(Login.this, GetStarted.class));
            finish();
        } else {
            fDialog.hide();
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}