package com.example.myapplication.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;


public class HomeFragment extends Fragment {


    public HomeFragment() {

    }
    Button createPost;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

             View view =  inflater.inflate(R.layout.fragment_home, container, false);




        return view;
    }
}