package com.example.myapplication.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.Models.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import static com.example.myapplication.MainActivity.MY_DATABASE;


public class HomeFragment extends Fragment {

    private ImageView createPost;
    private TextView txtUsername;
    private String userName;

    private User user;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    public HomeFragment() {

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        createPost = view.findViewById(R.id.createPost);
        txtUsername = view.findViewById(R.id.txtUsername);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(auth.getUid() != null){
                        userName = dataSnapshot.child("Users").child(auth.getUid()).child("userName").getValue(String.class);
                        txtUsername.setText(userName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostFragment postFragment = new PostFragment();
                if(getFragmentManager() != null) {
                    postFragment.show(getFragmentManager(), "show my post");
                }
            }
        });
        return view;
    }
}