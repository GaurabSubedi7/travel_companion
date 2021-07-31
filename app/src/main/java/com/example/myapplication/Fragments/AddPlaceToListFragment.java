package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapters.TripAdapter;
import com.example.myapplication.Models.Checklist;
import com.example.myapplication.Models.Trip;
import com.example.myapplication.R;
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


public class AddPlaceToListFragment extends DialogFragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private final DatabaseReference databaseReference = database.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private TextView placeName, noTripsText;
    private Button addToListButton, addNewTripButton;
    private RadioGroup tripsRadioGroup;

    private ArrayList<Trip> trips= new ArrayList<>();
    private ArrayList<Trip> selectedTrip = new ArrayList<>();
    private Trip trip = new Trip();
    private Checklist checklist;

    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState){
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_place_to_list, null);

        initView(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Add Places to List");

        if(getArguments() != null){
            double latitude = getArguments().getDouble("latitude");
            double longitude = getArguments().getDouble("longitude");
            String title = getArguments().getString("title");
            placeName.setText(title);
            getDataFromFirebase(title, latitude, longitude);
        }

        return builder.create();
    }

    private void addPlaceToFirebase(String title, double latitude, double longitude,  String tripId){
        if(auth.getUid() != null){
            checklist = new Checklist(title, latitude, longitude, false);
            String checklistId = databaseReference.push().getKey();
            if(checklistId != null) {
                databaseReference.child("Users").child(auth.getUid()).child("Trips").child(tripId).child("Checklist")
                        .child(checklistId).setValue(checklist);
            }
        }
    }

    private void getDataFromFirebase(String title, double latitude, double longitude){
        if(auth.getUid() != null){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(auth.getUid()!=null){
                            trips.clear();
                            for(DataSnapshot data : snapshot.child("Users").child(auth.getUid())
                                    .child("Trips").getChildren()){
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
                                addToListButton.setVisibility(View.VISIBLE);
                                addNewTripButton.setVisibility(View.GONE);
                                noTripsText.setVisibility(View.GONE);
                                tripsRadioGroup.setVisibility(View.VISIBLE);
                                for(Trip t: trips){
                                    if(getContext() != null){
                                        RadioButton rb = new RadioButton(getContext());
                                        rb.setId(View.generateViewId());
                                        rb.setText(t.getTripName());
                                        rb.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(getContext(), t.getTripName() + "selected", Toast.LENGTH_SHORT).show();
                                                selectedTrip.add(0, t);
                                            }
                                        });
                                        tripsRadioGroup.addView(rb);
                                    }
                                }
                                addToListButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FragmentManager fm = getFragmentManager();
                                        if(fm != null){
                                            if(!selectedTrip.isEmpty()) {
                                                FragmentTransaction ft = fm.beginTransaction();
                                                IndividualTripFragment individualTripFragment = new IndividualTripFragment();
                                                addPlaceToFirebase(title, latitude, longitude, selectedTrip.get(0).getTripId());
                                                //send data to IndividualTripFragment
                                                Bundle bundle = new Bundle();
                                                bundle.putString("myTripID", selectedTrip.get(0).getTripId());
                                                bundle.putString("myTripName", selectedTrip.get(0).getTripName());
                                                bundle.putString("myTripAmount", selectedTrip.get(0).getAmount());
                                                bundle.putString("myTripLocation", selectedTrip.get(0).getLocation());
                                                bundle.putString("myTripStartDate", selectedTrip.get(0).getStartDate());
                                                bundle.putString("myTripEndDate", selectedTrip.get(0).getEndDate());
                                                individualTripFragment.setArguments(bundle);
                                                ft.replace(R.id.FrameContainer, individualTripFragment).addToBackStack(null);
                                                dismiss();
                                                ft.commit();
                                            }else {
                                                Toast.makeText(getContext(), "Select A Trip First", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }else{
                                addToListButton.setVisibility(View.GONE);
                                addNewTripButton.setVisibility(View.VISIBLE);
                                noTripsText.setVisibility(View.VISIBLE);
                                tripsRadioGroup.setVisibility(View.GONE);

                                addNewTripButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FragmentManager fm = getFragmentManager();
                                        if(fm != null){
                                            FragmentTransaction ft = fm.beginTransaction();
                                            PlanFragment planFragment = new PlanFragment();
                                            ft.replace(R.id.FrameContainer, planFragment).addToBackStack(null);
                                            dismiss();
                                            ft.commit();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }


    private void initView(View view){
        placeName = view.findViewById(R.id.placeName);
        addToListButton = view.findViewById(R.id.addToListButton);
        tripsRadioGroup = view.findViewById(R.id.tripsRadioGroup);
        addNewTripButton = view.findViewById(R.id.addNewTrip);
        noTripsText = view.findViewById(R.id.noTripsText);
    }
}