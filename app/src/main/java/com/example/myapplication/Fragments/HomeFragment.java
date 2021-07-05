package com.example.myapplication.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.myapplication.Adapters.PostAdapter;
import com.example.myapplication.Adapters.UserPostAdapter;
import com.example.myapplication.Models.UserPost;
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

import static com.example.myapplication.MainActivity.MY_DATABASE;


public class HomeFragment extends Fragment {

    private ImageView createPost;
    private TextView txtUsername;
    private RecyclerView newsFeedRecView;

    private UserPostAdapter adapter;

    private String userName;

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();
    public ArrayList<UserPost> userPosts = new ArrayList<>();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);

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
                postFragment.show(getFragmentManager(), "show my post");
            }
        });

        getDataFromFirebase();

        return view;
    }

    private void getDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userPosts.clear();
                    if(auth.getUid() != null){

                        //variables to temporarily store data from firebase before adding to object.
                        ArrayList<String> myImages;
                        String myId, myCaption, myDate;

                        //get user's post from firebase
                        for(DataSnapshot users : dataSnapshot.child("Users").getChildren()) {
                            for(DataSnapshot data: users.child("Posts").getChildren()){
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
                        }
                        if(!userPosts.isEmpty()){
                            //inflate recyclerView with images
                            adapter = new UserPostAdapter(getContext());
                            newsFeedRecView.setAdapter(adapter);
                            System.out.println("I just created the grid layout");
                            newsFeedRecView.setLayoutManager(new LinearLayoutManager(getContext()));

                            //get user's post from firebase and populate the adapter
                            Collections.reverse(userPosts);
                            adapter.setUserPosts(userPosts);
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
        createPost = view.findViewById(R.id.createPost);
        txtUsername = view.findViewById(R.id.txtServiceName);
        newsFeedRecView = view.findViewById(R.id.newsFeedRecView);
    }
}