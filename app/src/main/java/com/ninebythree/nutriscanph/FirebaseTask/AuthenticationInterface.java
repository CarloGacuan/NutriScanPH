package com.ninebythree.nutriscanph.FirebaseTask;

import com.ninebythree.nutriscanph.model.UserDetailsModel;

import java.util.ArrayList;
import java.util.List;

public interface AuthenticationInterface {
    public List<UserDetailsModel> userDatalist = new ArrayList<>();
    public void onResult(Boolean result, String message);
}
