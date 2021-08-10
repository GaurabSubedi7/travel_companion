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
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.UserPostAdapter;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ServicesFragment extends Fragment {
    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    // TODO: 8/10/21 change user classes to service classes and adapter 
    private UserPostAdapter adapter;
    public ArrayList<UserPost> userPosts = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();

    private RecyclerView newsFeedRecView;
    private Spinner locationFilterSpinner;
    private String selectedTripLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        // TODO: 8/10/21 get services to display on recycler view 
    }

    private void initView(View view){
        newsFeedRecView = view.findViewById(R.id.servicePostRecView);
        locationFilterSpinner = view.findViewById(R.id.serviceFilterSpinner);
    }
}
