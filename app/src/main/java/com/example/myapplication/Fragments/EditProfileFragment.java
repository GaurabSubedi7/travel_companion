package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

public class EditProfileFragment extends Fragment {
    private EditText editTxtFullName, editTxtAddress, editTxtPhoneNUmber;
    private Button btnSave;
    private ImageView userImage;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        initView(view);

        return view;
    }

    private void initView(View view){
        editTxtFullName = view.findViewById(R.id.editTxtFullName);
        editTxtAddress = view.findViewById(R.id.editTxtAddress);
        editTxtPhoneNUmber  = view.findViewById(R.id.editTxtPhoneNumber);
        btnSave = view.findViewById(R.id.btnSave);
        userImage = view.findViewById(R.id.userImage);
    }
}
