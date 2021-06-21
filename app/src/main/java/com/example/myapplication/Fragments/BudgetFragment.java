package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapplication.Adapters.TripAdapter;
import com.example.myapplication.Models.Trip;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.myapplication.MainActivity.MY_DATABASE;


public class BudgetFragment extends Fragment {
//Todo make the cards scrollable and  add expenses on card click

    public BudgetFragment() {

    }
    private TripAdapter tripAdapter;
    private Trip trip;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();
    public ArrayList<Trip> trips = new ArrayList<>();
    private Button addPlan;
    private FloatingActionButton addPlanFloating;
    private RelativeLayout noPlanRelLayout;
    private RecyclerView smallPlanRecView;
    private NestedScrollView tripNestedScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        initView(view);

        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlanFragment();
            }
        });

        addPlanFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlanFragment();
            }
        });
        getDataFromFireBase();
        return view;
    }

    private void goToPlanFragment(){
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.FrameContainer, new PlanFragment()).addToBackStack(null);
            ft.commit();
        }
    }

    private void getDataFromFireBase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(auth.getUid()!=null){
                        trips.clear();
                        for(DataSnapshot data : snapshot.child("Users").child(auth.getUid()).child("Trips").getChildren()){
                            String myKey = data.getKey();
                            trip = data.getValue(Trip.class);
                            if(myKey != null && trip != null){
                                trip.setTripId(myKey);
                            }else{
                                Toast.makeText(getActivity(), "TripId became null", Toast.LENGTH_SHORT).show();
                            }
                            trips.add(trip);
                            Collections.reverse(trips);
                        }

                        if(!trips.isEmpty()){
                            tripNestedScrollView.setVisibility(View.VISIBLE);
                            addPlanFloating.setVisibility(View.VISIBLE);
                            noPlanRelLayout.setVisibility(View.GONE);
                            FragmentManager fm = getFragmentManager();
                            tripAdapter = new TripAdapter(getContext(), fm);
                            smallPlanRecView.setLayoutManager(new LinearLayoutManager(getContext()));
                            smallPlanRecView.setAdapter(tripAdapter);
                            tripAdapter.setTrip(trips);
                        }else{
                            tripNestedScrollView.setVisibility(View.GONE);
                            addPlanFloating.setVisibility(View.GONE);
                            noPlanRelLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void initView(View view) {
        addPlan = view.findViewById(R.id.btnAddPlan);
        addPlanFloating = view.findViewById(R.id.btnAddPlanFloating);
        smallPlanRecView = view.findViewById(R.id.smallPlanRecView);
        noPlanRelLayout = view.findViewById(R.id.noPlanRelLayout);
        tripNestedScrollView = view.findViewById(R.id.tripNestedScrollView);
    }
}