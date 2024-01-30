package com.ninebythree.nutriscanph.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.adapter.AboutAdapter;
import com.ninebythree.nutriscanph.adapter.MyInterface;
import com.ninebythree.nutriscanph.adapter.NotificationAdapter;
import com.ninebythree.nutriscanph.model.AboutModel;
import com.ninebythree.nutriscanph.model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class About extends Fragment implements MyInterface {

    View view;

    private List<AboutModel> aboutModels = new ArrayList<>();
    private AboutAdapter aboutAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_about, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        aboutAdapter = new AboutAdapter(getContext(), aboutModels, this);
        recyclerView.setAdapter(aboutAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String x = "Pursuing my degree in BS in Information Technology. Have three months of experience training in Computer System Servicing with Pulxar TVI Inc. Strong oral and written communication, computer proficiency, video editing, and copy-editing skills. Seeking to leverage academic background and programming skills to fill a IT specialist position.";

        String y = "I’m a dedicated front-end developer with a strong passion for creating visually appealing and user-friendly websites and applications. While currently focused on front-end development, I aspire to become a versatile full-stack developer in the future, equipped with the skills to handle both front-end and back-end aspects of web development. With a strong work ethic and a hunger for knowledge, I am committed to achieving success in the field of web development and creating meaningful digital experiences for users.";
        String z = "I am Lester Suratos. My expertise lies in the development and deployment of computer applications. I meticulously oversee the code builds, ensuring flawless transitions to both testing and production environments. I excel at diagnosing and rectifying issues within existing code, striving for seamless performance. Collaboration is at the heart of my work, as I closely coordinate with product, design, and marketing teams to create cohesive and effective digital solutions.";
        aboutModels.add(new AboutModel(R.drawable.pic1, "Jasmin V. Bolen", x));
        aboutModels.add(new AboutModel(R.drawable.pic2,  "Carlos Miguel T. Gacuan", y));
        aboutModels.add(new AboutModel(R.drawable.pic3,  "Lester S. Suratos", z));

        return view;

    }

    @Override
    public void onItemClick(int pos, String categories) {

    }
}