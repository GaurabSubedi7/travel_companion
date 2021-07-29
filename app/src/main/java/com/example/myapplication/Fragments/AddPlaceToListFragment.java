package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
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

    private TextView placeName;
    private Button addToListButton;
    private RadioGroup tripsRadioGroup;

    private ArrayList<Trip> trips= new ArrayList<>();
    private Trip trip = new Trip();

    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState){
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_place_to_list, null);

        initView(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Add Places to List");

        double latitude = getArguments().getDouble("latitude");
        double longitude = getArguments().getDouble("longitude");
        String title = getArguments().getString("title");
        placeName.setText(title);
        getDataFromFirebase();

        addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaceToFirebase(title, latitude, longitude);

            }
        });


        return builder.create();
    }

    private void addPlaceToFirebase(String title, double latitude, double longitude){
        if(auth.getUid() != null){
//            databaseReference.child("Users").child(auth.getUid()).child("Trips").

        }
    }

    private void getDataFromFirebase(){
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
                                for(Trip t: trips){
                                    if(getContext() != null){
                                        RadioButton rb = new RadioButton(getContext());
                                        rb.setId(View.generateViewId());
                                        rb.setText(t.getTripName());
                                        rb.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(getContext(), t.getTripName() + "selected", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        tripsRadioGroup.addView(rb);

                                    }                                }
                            }else{
                                //NO TRIPS HERE
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
    }
}