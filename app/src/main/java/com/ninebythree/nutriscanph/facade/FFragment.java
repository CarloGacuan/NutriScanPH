package com.ninebythree.nutriscanph.facade;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.fragment.About;
import com.ninebythree.nutriscanph.fragment.EditProfile;
import com.ninebythree.nutriscanph.fragment.Feedback;
import com.ninebythree.nutriscanph.fragment.Homepage;
import com.ninebythree.nutriscanph.fragment.Notification;
import com.ninebythree.nutriscanph.fragment.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class FFragment {

    private HashMap<String, Fragment> fragmentMap = new HashMap<>();
    private AppCompatActivity activity;
    private DrawerLayout drawerLayout;

    public FFragment(AppCompatActivity activity, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        init();
    }

    public void init() {
        fragmentMap.put("HOME", new Homepage());
        fragmentMap.put("NOTIFICATION", new Notification());
        fragmentMap.put("FEEDBACK", new Feedback());
        fragmentMap.put("ABOUT", new About());
        fragmentMap.put("EDIT_PROFILE", new EditProfile());
        fragmentMap.put("PROFILE", new Profile());
    }

    public FFragment setConsumer(String fragmentName, Consumer<Nullable> consumer) {

        switch (fragmentName) {
            case "EDIT_PROFILE":
                EditProfile editProfile = (EditProfile) fragmentMap.get(fragmentName);
                editProfile.setConsumer(consumer);
                break;
            case "PROFILE":
                Profile profile = (Profile) fragmentMap.get(fragmentName);
                profile.setConsumer(consumer);
                break;
        }

        return this;
    }

    public void set(String fragmentName) {
        Fragment currentFragment;

        switch (fragmentName) {
            case "HOME":
                Homepage homeFragment = (Homepage) fragmentMap.get(fragmentName);
                homeFragment.fetchData();
                currentFragment = homeFragment;
                break;
            default:
                currentFragment = fragmentMap.get(fragmentName);
                break;
        }

        activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, currentFragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

}
