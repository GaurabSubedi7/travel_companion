package com.example.myapplication.Fragments;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ServicePostAdapter;
import com.example.myapplication.Adapters.UserPostAdapter;
import com.example.myapplication.Models.ServicePost;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ServicesFragment extends Fragment {
    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    private ServicePostAdapter adapter;
    public ArrayList<ServicePost> servicePosts = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();

    private RecyclerView newsFeedRecView;
    private Spinner locationFilterSpinner;
    private String selectedTripLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
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
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                servicePosts.clear();
                users.clear();
                if(auth.getUid() != null && snapshot.exists()){
                    ArrayList<String> myImages;
                    String myId, serviceName, description, serviceType, serviceLocation, myDate, userId, contact,
                            email = "unknown";
                    double rating = 0;
                    int count = 0;

                    for(DataSnapshot data: snapshot.child("Services").getChildren()){
                        myId = data.getKey();
                        userId = (String) data.child("userId").getValue();

                        if(userId != null) {
                            String userName = snapshot.child("Users").child(userId).child("thirdPartyServiceName").getValue(String.class);
                            email = snapshot.child("Users").child(userId).child("email").getValue(String.class);
                            String profilePic = snapshot.child("Users").child(userId).child("img").getValue(String.class);
                            User user = new User(userId, userName);
                            if(profilePic != null){
                                user.setProfilePic(profilePic);
                            }
                            users.add(user);
                        }

                        myImages = new ArrayList<>();
                        serviceName = (String) data.child("serviceName").getValue();
                        serviceType = (String) data.child("serviceType").getValue();
                        description = (String) data.child("serviceDescription").getValue();
                        serviceLocation = (String) data.child("serviceLocation").getValue();
                        contact = (String) data.child("contactNumber").getValue();
                        myDate = (String) data.child("uploadDate").getValue();

                        for(DataSnapshot imageId: data.child("Images").getChildren()){
                            myImages.add((String) imageId.child("img").getValue());
                        }
                        rating = 0;
                        count = 0;
                        for(DataSnapshot ratings: data.child("rating").getChildren()){
                            rating += ratings.getValue(Double.class);
                            count++;
                        }

                        if(!myImages.isEmpty()) {
                            if(rating != 0 && count != 0){
                                rating = rating/count;
                            }

                            if(location.equals("All")) {
                                ServicePost servicePost = new ServicePost(myId, serviceName, serviceType, description,
                                        serviceLocation, contact, rating, myImages, myDate, userId);
                                servicePost.setRating(rating);
                                servicePosts.add(servicePost);
                            }

                            if(!location.equals("All") && location.equals(serviceLocation)) {
                                ServicePost servicePost = new ServicePost(myId, serviceName, serviceType, description,
                                        location, contact, rating, myImages, myDate, userId);
                                servicePost.setRating(rating);
                                servicePosts.add(servicePost);
                            }
                        }
                    }

                    if(!servicePosts.isEmpty()){
                        //inflate recyclerView with images
                        FragmentManager fm = getFragmentManager();
                        adapter = new ServicePostAdapter(getContext(), fm, "users", email);
                        newsFeedRecView.setAdapter(adapter);
                        newsFeedRecView.setLayoutManager(new LinearLayoutManager(getContext()));

                        //get service's post from firebase and populate the adapter
                        Collections.reverse(servicePosts);
                        adapter.setServicePosts(servicePosts);
                        adapter.setUsers(users);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView(View view){
        newsFeedRecView = view.findViewById(R.id.servicePostRecView);
        locationFilterSpinner = view.findViewById(R.id.serviceFilterSpinner);
    }
}
