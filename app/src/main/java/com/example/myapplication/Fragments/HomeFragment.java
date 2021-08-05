package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Adapters.UserPostAdapter;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private BottomNavigationView bottomNavigationView;

    private UserPostAdapter adapter;

    private String userName;

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    public ArrayList<UserPost> userPosts = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);

        if(getActivity() != null){
            bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

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
                FragmentManager fm = getFragmentManager();
                if(fm != null){
                    FragmentTransaction ft = fm.beginTransaction().addToBackStack(null);
                    ft.replace(R.id.FrameContainer,new PostUploadFragment()).commit();
                }
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
                    users.clear();
                    if(auth.getUid() != null){

                        //variables to temporarily store data from firebase before adding to object.
                        ArrayList<String> myImages;
                        ArrayList<User> liker;
                        String myId, myCaption, myDate, userId, tripLocation, specificLocation;
                        double latitude, longitude;

                        //get user's post from firebase
                        for(DataSnapshot data: dataSnapshot.child("Posts").getChildren()){
                            myId = (String) data.getKey();
                            userId = (String) data.child("userId").getValue();
                            if(userId != null) {
                                String userName = dataSnapshot.child("Users").child(userId).child("userName").getValue(String.class);
                                users.add(new User(userId, userName));
                            }
                            myImages = new ArrayList<>();
                            liker = new ArrayList<>();
                            myCaption = (String) data.child("caption").getValue();
                            myDate = (String) data.child("uploadDate").getValue();
                            tripLocation = (String) data.child("tripLocation").getValue();
                            specificLocation = (String) data.child("specificLocation").getValue();
                            latitude = data.child("latitude").getValue(Double.class);
                            longitude = data.child("longitude").getValue(Double.class);
                            for(DataSnapshot imageId: data.child("Images").getChildren()){
                                myImages.add((String) imageId.child("img").getValue());
                            }

                            for(DataSnapshot likeCount: data.child("likeCount").getChildren()){
                                String uid = (String) likeCount.getValue();
                                if(uid != null) {
                                    String userName = dataSnapshot.child("Users").child(uid).child("userName").getValue(String.class);
                                    liker.add(new User(uid, userName));
                                }
                            }

                            if(!myImages.isEmpty()) {
                                UserPost userPost = new UserPost(myId, userId, myCaption, myDate, myImages,
                                        tripLocation, specificLocation, latitude, longitude);
                                //all the data added to userPosts arraylist
                                if(!liker.isEmpty()){
                                    userPost.setLikeCount(liker);
                                }
                                userPosts.add(userPost);
                            }
                        }

                        if(!userPosts.isEmpty()){
                            //inflate recyclerView with images
                            FragmentManager fm = getFragmentManager();
                            adapter = new UserPostAdapter(getContext(), auth.getUid(), fm);
                            newsFeedRecView.setAdapter(adapter);
                            newsFeedRecView.setLayoutManager(new LinearLayoutManager(getContext()));

                            //get user's post from firebase and populate the adapter
                            Collections.reverse(userPosts);
                            adapter.setUserPosts(userPosts);
                            adapter.setUsers(users);
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