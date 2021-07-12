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
import android.widget.Toast;

import com.example.myapplication.Models.Image;
import com.example.myapplication.R;

import java.util.ArrayList;


public class AddPlaceToListFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState){
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_place_to_list, null);

        initView(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Add Places to List");


        return builder.create();
    }

    public void initView(View view){

    }
}