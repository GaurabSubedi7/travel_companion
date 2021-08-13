package com.example.myapplication.Fragments;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

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

public class MyServicesFragment extends Fragment {
    private RecyclerView myServiceRecView;
    private RelativeLayout noServicesRelLayout, myServicesRelLayout;
    private Button btnAddServices2;

    private ServicePostAdapter adapter;

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    private ArrayList<ServicePost> servicePosts = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_services, container, false);
        initView(view);

        getDataFromFirebase();

        return view;
    }

    private void getDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                servicePosts.clear();
                users.clear();
                if(auth.getUid() != null && snapshot.exists()){
                    ArrayList<String> myImages;
                    String myId, serviceName, description, serviceType, serviceLocation, myDate, userId;
                    int price;
                    double rating = 0;
                    int count = 0;

                    for(DataSnapshot data: snapshot.child("Services").getChildren()){
                        myId = data.getKey();
                        userId = (String) data.child("userId").getValue();

                        if(userId != null) {
                            String userName = snapshot.child("Users").child(userId).child("thirdPartyServiceName").getValue(String.class);
                            users.add(new User(userId, userName));
                        }

                        myImages = new ArrayList<>();
                        serviceName = (String) data.child("serviceName").getValue();
                        serviceType = (String) data.child("serviceType").getValue();
                        description = (String) data.child("serviceDescription").getValue();
                        serviceLocation = (String) data.child("serviceLocation").getValue();
                        price = data.child("servicePrice").getValue(Integer.class);
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
                            if(auth.getUid().equals(userId)){
                                ServicePost servicePost = new ServicePost(myId, serviceName, serviceType, description,
                                        serviceLocation, price, rating, myImages, myDate, userId);
                                servicePost.setRating(rating);
                                servicePosts.add(servicePost);
                            }
                        }
                    }

                    if(!servicePosts.isEmpty()){
                        noServicesRelLayout.setVisibility(View.GONE);
                        myServicesRelLayout.setVisibility(View.VISIBLE);
                        //inflate recyclerView with images
                        FragmentManager fm = getFragmentManager();
                        adapter = new ServicePostAdapter(getContext(), fm, "myServices");
                        myServiceRecView.setAdapter(adapter);
                        myServiceRecView.setLayoutManager(new LinearLayoutManager(getContext()));

                        //get service's post from firebase and populate the adapter
                        Collections.reverse(servicePosts);
                        adapter.setServicePosts(servicePosts);
                        adapter.setUsers(users);
                    }else{
                        noServicesRelLayout.setVisibility(View.VISIBLE);
                        myServicesRelLayout.setVisibility(View.GONE);

                        btnAddServices2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FragmentManager fm  = getFragmentManager();
                                if(fm != null){
                                    FragmentTransaction ft = fm.beginTransaction().addToBackStack(null);
                                    ft.replace(R.id.FrameContainer,new AddServicesFragment()).commit();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView(View view) {
        myServiceRecView = view.findViewById(R.id.myServicesRecView);
        noServicesRelLayout = view.findViewById(R.id.noServiceRelLayout);
        myServicesRelLayout = view.findViewById(R.id.myServicesRelLayout);
        btnAddServices2 = view.findViewById(R.id.btnAddNewService2);
    }
}
