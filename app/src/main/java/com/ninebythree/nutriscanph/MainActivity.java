package com.ninebythree.nutriscanph;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ninebythree.nutriscanph.activity.Profile;
import com.ninebythree.nutriscanph.authentication.Login;
import com.ninebythree.nutriscanph.facade.FFragment;
import com.ninebythree.nutriscanph.fragment.About;
import com.ninebythree.nutriscanph.fragment.EditProfile;
import com.ninebythree.nutriscanph.fragment.Feedback;
import com.ninebythree.nutriscanph.fragment.Homepage;
import com.ninebythree.nutriscanph.fragment.Notification;
import com.ninebythree.nutriscanph.sharedpreference.UserPreference;

import java.io.IOException;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FFragment ffragment;
    private UserPreference userPreference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Set up the ActionBar with the hamburger icon
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setTitle("");
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // SET FRAGMENT
        ffragment = new FFragment(this, drawerLayout);
        ffragment.set("HOME");

        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        userPreference = new UserPreference(getApplicationContext());
        FirebaseUser currentUser = mAuth.getCurrentUser();
/*
        if(currentUser.getPhotoUrl() != null) {
            Log.i("IMGPATH", "TRIGGER 4" + currentUser.getPhotoUrl().getPath());
            try {
                Bitmap fetchedImg = MediaStore.Images.Media.getBitmap(getContentResolver(), currentUser.getPhotoUrl());
                userPreference.setImagePath(currentUser.getPhotoUrl().getPath());
                userPreference.setImagekey(fetchedImg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) ffragment.set("HOME"); // Handle action for home
                else if (itemId == R.id.nav_notification) ffragment.set("NOTIFICATION"); // Handle action for home
                else if (itemId == R.id.nav_feedback) ffragment.set("FEEDBACK"); // Handle action for Feedback
                else if (itemId == R.id.nav_about) ffragment.set("ABOUT"); // Handle action for About
                else if (itemId == R.id.nav_edit) ffragment.setConsumer("EDIT_PROFILE", new FragmentConsumer())
                        .set("EDIT_PROFILE"); // Handle action for Edit Profile
                else if (itemId == R.id.nav_logout) {
                    //TODO LOGOUT
                    mAuth.signOut();

                    userPreference.clearPreferences();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                    ffragment.closeDrawer();
                }

                return true;
            }
        });

        profileSection();

    }

    private Bitmap rotateImageIfRequired(Bitmap img) {
        Matrix matrix = new Matrix();
        // Replace 90 with the degree of rotation you need to correct the image
        matrix.postRotate(90);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void profileSection() {
        View headerView = navigationView.getHeaderView(0);
        // TextView nameTextView = headerView.findViewById(R.id.name);
        CardView btnProfile = headerView.findViewById(R.id.btnProfile);
        TextView txtName = headerView.findViewById(R.id.txtName);
        TextView txtEmail = headerView.findViewById(R.id.txtEmail);
        ImageView imgProfile = headerView.findViewById(R.id.imgProfile);

        String fullName = userPreference.getFullname();
        String email = userPreference.getEmail();
        String imgPath = userPreference.getImagePath();
        // Glide.with(this).load(imgPath).into(imgProfile);

        txtName.setText(fullName);
        txtEmail.setText(email);
        Bitmap originalBitmap = userPreference.getIMAGEKEY();

        if (originalBitmap != null) {
            Bitmap rotatedBitmap = rotateImageIfRequired(originalBitmap);
            imgProfile.setImageBitmap(rotatedBitmap);
        } else {
            imgProfile.setImageResource(R.drawable.profile_bg); // Your default image
        }

        btnProfile.setOnClickListener(v -> navigateToProfile());
    }

    private void navigateToProfile() {
        ffragment.setConsumer("PROFILE", new EditFragmentConsumer()).set("PROFILE");
    }

    // CONSUMER
    private class FragmentConsumer implements Consumer<Nullable> {
        @Override
        public void accept(Nullable nullable) { ffragment.set("HOME"); }
    }

    private class EditFragmentConsumer implements Consumer<Nullable> {
        @Override
        public void accept(Nullable nullable) {
            ffragment.setConsumer("EDIT_PROFILE", new FragmentConsumer()).set("EDIT_PROFILE");
        }
    }

}
