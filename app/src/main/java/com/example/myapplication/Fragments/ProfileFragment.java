package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }
    //so many unnecessary things are given while creating fragments so removed it
    //variables are declared here in fragment
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Button logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);

            }
        });
        return view;
    }
}