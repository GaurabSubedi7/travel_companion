package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.jetbrains.annotations.NotNull;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import java.util.ArrayList;
import java.util.Collections;

public class ServiceHomeFragment extends Fragment {

    private TextView serviceName;
    private ImageView serviceProfileImage, btnAddServices;
    private BottomNavigationView bottomNavigationView;

    private UserPostAdapter adapter;

    public ArrayList<UserPost> userPosts = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    private RecyclerView serviceFeedRecView;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_service_home, container, false);

        //initialize all the views
        initView(view);
        if(getActivity() != null){
            bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationService);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(auth.getUid() != null){
                        String thirdPartyServiceName = snapshot.child("Users").child(auth.getUid()).child("thirdPartyServiceName").getValue(String.class);
                        serviceName.setText(thirdPartyServiceName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        btnAddServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm  = getFragmentManager();
                if(fm != null){
                    FragmentTransaction ft = fm.beginTransaction().addToBackStack(null);
                    ft.replace(R.id.FrameContainer,new AddServicesFragment()).commit();
                }
            }
        });

        getDataFromFirebase();

        return view;
    }

    private void getDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @com.google.firebase.database.annotations.NotNull DataSnapshot dataSnapshot) {
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
                            myId = data.getKey();
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
                            serviceFeedRecView.setAdapter(adapter);
                            serviceFeedRecView.setLayoutManager(new LinearLayoutManager(getContext()));

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
        serviceName = view.findViewById(R.id.txtServiceName);
        btnAddServices = view.findViewById(R.id.btnAddServices);
        serviceProfileImage = view.findViewById(R.id.serviceProfileImage);
        serviceFeedRecView = view.findViewById(R.id.serviceFeedRecView);
    }
}

