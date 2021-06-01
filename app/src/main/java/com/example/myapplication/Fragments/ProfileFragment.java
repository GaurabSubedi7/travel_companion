package com.example.myapplication.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DashboardActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.sql.SQLOutput;
import java.util.ArrayList;

import static com.example.myapplication.MainActivity.MY_DATABASE;


public class ProfileFragment extends Fragment {

    private TextView userName;
    private ImageView createPost, userImage, logout;
    private Button editProfile;
    private ConstraintLayout profileFragment;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();
    private ArrayList<UserPost> userPosts = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }
    //variables are declared here in fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initView(view);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(auth.getUid() != null){
                        String username = dataSnapshot.child("Users").child(auth.getUid()).child("userName").getValue(String.class);
                        userName.setText(username);
                        //pulling user's posts from firebase
                        getUserPosts(dataSnapshot);
                    }
                }
            }

            private void getUserPosts(DataSnapshot dataSnapshot) {
                if(auth.getUid() != null){
//                    for (DataSnapshot)
                }
//                userPosts.add();
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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Are You Sure You Want To Logout?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //should be empty
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                auth.signOut();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                builder.create().show();
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm= getFragmentManager();
                FragmentTransaction ft= fm.beginTransaction();
//                FrameLayout p = (FrameLayout) view.findViewById(R.id.profileFragment);
//                p.removeAllViews();
                ft.replace( R.id.FrameContainer, new EditProfileFragment());
                ft.commit();
            }
        });

        return view;
    }

    private void initView(View view){
        userName = view.findViewById(R.id.userName);
        editProfile = view.findViewById(R.id.editProfile);
        logout = view.findViewById(R.id.logout);
        profileFragment = view.findViewById(R.id.profileFragment);
        userImage = view.findViewById(R.id.userImage);
        createPost = view.findViewById(R.id.createPost);
    }
}