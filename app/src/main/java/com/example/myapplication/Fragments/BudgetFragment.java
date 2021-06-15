package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.example.myapplication.Adapters.TripAdapter;
import com.example.myapplication.Models.Trip;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    private RecyclerView smallPlanRecView;
//    private CardView userTripSmall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        addPlan = view.findViewById(R.id.btnAddPlan);
        smallPlanRecView = view.findViewById(R.id.smallPlanRecView);
//        userTripSmall = view.findViewById(R.id.userTripSmall);

        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (fm != null) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.FrameContainer, new PlanFragment());
                    ft.commit();
                }
            }
        });
        getDataFromFireBase();


        return view;
    }

    private void getDataFromFireBase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(auth.getUid()!=null){

                        for(DataSnapshot data : snapshot.child("Users").child(auth.getUid()).child("Trips").getChildren()){
                            trip = data.getValue(Trip.class);
                            trips.add(trip);
                        }

                        FragmentManager fm = getFragmentManager();
                        tripAdapter = new TripAdapter(getContext(), fm);
                        smallPlanRecView.setLayoutManager(new LinearLayoutManager(getContext()));
                        smallPlanRecView.setAdapter(tripAdapter);
                        tripAdapter.setTrip(trips);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });





    }

}