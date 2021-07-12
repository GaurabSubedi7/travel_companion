package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Models.Image;
import com.example.myapplication.R;

import java.util.ArrayList;


public class AddPlaceToListFragment extends DialogFragment {

    private TextView placeName;
    private Button addToListButton, showDirectionButton;

    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState){
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_place_to_list, null);

        initView(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Add Places to List");

        double latitude = getArguments().getDouble("latitude");
        double longitude = getArguments().getDouble("longitude");
        String title = getArguments().getString("title");
        placeName.setText(title);
        System.out.println("================" + latitude +"   "+ longitude);

        addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaceToFirebase(title, latitude, longitude);
            }
        });

        return builder.create();
    }

    public void addPlaceToFirebase(String title, double latitude, double longitude){

    }


    public void initView(View view){
        placeName = view.findViewById(R.id.placeName);
        addToListButton = view.findViewById(R.id.addToListButton);
        showDirectionButton = view.findViewById(R.id.showDirectionButton);
    }
}