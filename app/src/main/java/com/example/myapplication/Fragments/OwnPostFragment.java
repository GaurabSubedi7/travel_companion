package com.example.myapplication.Fragments;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.PostAdapter;
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

public class OwnPostFragment extends DialogFragment {
    private RecyclerView ownPostRecView;
    private UserPostAdapter adapter;
    private String postId;
    public ArrayList<UserPost> userPosts = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.own_post_view, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
        initView(view);

        if(getArguments() != null){
            postId = getArguments().getString("postId");
        }

        getDataFromFirebase();

        return builder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
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
                        String myCaption, myDate, userId, tripLocation, specificLocation;
                        double latitude, longitude;

                        //get user's post from firebase
                        userId = dataSnapshot.child("Posts").child(postId).child("userId").getValue(String.class);
                        if(userId != null) {
                            String userName = dataSnapshot.child("Users").child(userId).child("userName").getValue(String.class);
                            users.add(new User(userId, userName));
                        }
                        myImages = new ArrayList<>();
                        liker = new ArrayList<>();
                        myCaption = (String) dataSnapshot.child("Posts").child(postId).child("caption").getValue();
                        myDate = (String) dataSnapshot.child("Posts").child(postId).child("uploadDate").getValue();
                        tripLocation = ((String) dataSnapshot.child("Posts").child(postId).child("tripLocation").getValue());
                        specificLocation = (String) dataSnapshot.child("Posts").child(postId).child("specificLocation").getValue();
                        latitude = 0.01;
                        longitude = 0.01;
                        for(DataSnapshot imageId: dataSnapshot.child("Posts").child(postId).child("Images").getChildren()){
                            myImages.add((String) imageId.child("img").getValue());
                        }

                        for(DataSnapshot likeCount: dataSnapshot.child("Posts").child(postId).child("likeCount").getChildren()){
                            String uid = (String) likeCount.getValue();
                            if(uid != null) {
                                String userName = dataSnapshot.child("Users").child(uid).child("userName").getValue(String.class);
                                liker.add(new User(uid, userName));
                            }
                        }

                        if(!myImages.isEmpty()) {
                            UserPost userPost = new UserPost(postId, userId, myCaption, myDate, myImages,
                                    tripLocation, specificLocation, latitude, longitude);
                            //all the data added to userPosts arraylist
                            if(!liker.isEmpty()){
                                userPost.setLikeCount(liker);
                            }
                            userPosts.add(userPost);
                        }

                        if(!userPosts.isEmpty()){
                            //inflate recyclerView with images
                            FragmentManager fm = getFragmentManager();
                            adapter = new UserPostAdapter(getContext(), auth.getUid(), fm, "User");
                            ownPostRecView.setAdapter(adapter);
                            ownPostRecView.setLayoutManager(new LinearLayoutManager(getContext()));

                            //get user's post from firebase and populate the adapter
                            Collections.reverse(userPosts);
                            adapter.setUserPosts(userPosts);
                            adapter.setDialog(getDialog());
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
        ownPostRecView = view.findViewById(R.id.ownPostRecView);
    }
}
