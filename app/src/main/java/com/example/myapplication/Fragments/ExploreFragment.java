package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.myapplication.Adapters.UserPostAdapter;
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

import java.util.ArrayList;
import java.util.Collections;

import static com.example.myapplication.MainActivity.MY_DATABASE;


public class ExploreFragment extends Fragment {
    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    private UserPostAdapter adapter;
    public ArrayList<UserPost> userPosts = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();

    private RecyclerView newsFeedRecView;
    private Spinner locationFilterSpinner;
    private String selectedTripLocation;
    public ExploreFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        initView(view);
        locationFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTripLocation = locationFilterSpinner.getSelectedItem().toString();
                getDataFromFirebase(selectedTripLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void getDataFromFirebase(String location){
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
                                String profilePic = dataSnapshot.child("Users").child(userId).child("img").getValue(String.class);
                                User user = new User(userId, userName);
                                if(profilePic != null){
                                    user.setProfilePic(profilePic);
                                }
                                users.add(user);
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
                                if (!location.equals("All") && location.equals(tripLocation)) {
                                    UserPost userPost = new UserPost(myId, userId, myCaption, myDate, myImages,
                                            location, specificLocation, latitude, longitude);
                                    //all the data added to userPosts arraylist
                                    if (!liker.isEmpty()) {
                                        userPost.setLikeCount(liker);
                                    }

                                    userPosts.add(userPost);
                                }
                                if(location.equals("All")){
                                    UserPost userPost = new UserPost(myId, userId, myCaption, myDate, myImages,
                                            tripLocation, specificLocation, latitude, longitude);
                                    //all the data added to userPosts arraylist
                                    if (!liker.isEmpty()) {
                                        userPost.setLikeCount(liker);
                                    }

                                    userPosts.add(userPost);
                                }
                            }
                        }

                        if(!userPosts.isEmpty()){
                            //inflate recyclerView with images
                            FragmentManager fm = getFragmentManager();
                            adapter = new UserPostAdapter(getContext(), auth.getUid(), fm, "User");
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
        newsFeedRecView = view.findViewById(R.id.explorePostRecView);
        locationFilterSpinner = view.findViewById(R.id.exploreFilterSpinner);

    }
}