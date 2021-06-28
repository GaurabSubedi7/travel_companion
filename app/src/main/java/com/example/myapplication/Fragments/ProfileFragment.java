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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.Adapters.PostAdapter;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.example.myapplication.MainActivity.MY_DATABASE;


public class ProfileFragment extends Fragment {

    //initializing adapter
    private PostAdapter adapter;

    private RecyclerView smallImageRecView;
    private TextView userName;
    private ImageView createPost, userImage, logout;
    private Button editProfile;
    private ConstraintLayout profileFragment;
    private RelativeLayout noSmallPostRelLayout;

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();
    public ArrayList<UserPost> userPosts = new ArrayList<>();

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

        //userPost array list
        getDataFromFirebase();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
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
                if(fm != null) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.FrameContainer, new EditProfileFragment()).addToBackStack(null);
                    ft.commit();
                }
            }
        });

        return view;
    }

    private void getDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userPosts.clear();
                    if(auth.getUid() != null){
                        String username = dataSnapshot.child("Users").child(auth.getUid()).child("userName").getValue(String.class);
                        userName.setText(username);

                        //variables to temporarily store data from firebase before adding to object.
                        ArrayList<String> myImages;
                        String myId, myCaption, myDate;

                        //get user's post from firebase
                        for(DataSnapshot data : dataSnapshot.child("Users").child(auth.getUid()).child("Posts").getChildren()) {
                            myId = (String) data.getKey();
                            myImages = new ArrayList<>();
                            myCaption = (String) data.child("caption").getValue();
                            myDate = (String) data.child("uploadDate").getValue();
                            for(DataSnapshot imageId: data.child("Images").getChildren()){
                                myImages.add((String) imageId.child("img").getValue());
                            }

                            if(!myImages.isEmpty()) {
                                //all the data added to userPosts arraylist
                                userPosts.add(new UserPost(myId, myCaption, myDate, myImages));
                            }
                        }
                        if(!userPosts.isEmpty()){
                            smallImageRecView.setVisibility(View.VISIBLE);
                            noSmallPostRelLayout.setVisibility(View.GONE);
                            //inflate recyclerView with images
                            adapter = new PostAdapter(getContext(), "profile");
                            smallImageRecView.setAdapter(adapter);
                            System.out.println("I just created the grid layout");
                            smallImageRecView.setLayoutManager(new GridLayoutManager(getContext(), 3));

                            //get user's post from firebase and populate the adapter
                            Collections.reverse(userPosts);
                            adapter.setUserPosts(userPosts);
                        }else{
                            smallImageRecView.setVisibility(View.GONE);
                            noSmallPostRelLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
           }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });
    }

    private void initView(View view){
        userName = view.findViewById(R.id.userName);
        editProfile = view.findViewById(R.id.editProfile);
        logout = view.findViewById(R.id.logout);
        profileFragment = view.findViewById(R.id.profileFragment);
        userImage = view.findViewById(R.id.userImage);
        createPost = view.findViewById(R.id.createPost);

        smallImageRecView = view.findViewById(R.id.smallImageRecView);
        noSmallPostRelLayout = view.findViewById(R.id.noSmallPostRelLayout);
    }
}