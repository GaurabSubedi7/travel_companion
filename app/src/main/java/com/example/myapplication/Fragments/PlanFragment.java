package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.DashboardActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.Models.Image;
import com.example.myapplication.Models.Trip;
import com.example.myapplication.R;
import com.example.myapplication.RegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.myapplication.MainActivity.MY_DATABASE;

public class
PlanFragment extends Fragment {

    public PlanFragment() {
        // Required empty public constructor
    }

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private DatePickerDialog startDialog ,endDialog;
    private EditText tripName;
    private EditText Amount;
    private EditText startDate;
    private EditText endDate;
    private Button setTrip;
    Trip trip;
    //Firebase Classes
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance(MY_DATABASE).getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("trips");

    private DatePickerDialog.OnDateSetListener startListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            startDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(startCalendar.getTime()));
        }
    };
    private DatePickerDialog.OnDateSetListener endListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, month);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            endDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(endCalendar.getTime()));
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        initView(view);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog = new DatePickerDialog(
                        getContext(),
                        startListener,
                        startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH)
                );
                startDialog.show();


            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDialog = new DatePickerDialog(
                        getContext(),
                        endListener,
                        endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)
                );
                endDialog.show();
            }
        });



        setTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tripname = tripName.getText().toString();
                String amount = Amount.getText().toString();
                String startdate = startDate.getText().toString();
                String enddate = endDate.getText().toString();

                trip = new Trip( tripname, amount, startdate, enddate);
                if (tripName != null && Amount != null && startDate != null && endDate != null) {
                    addToFirebase( tripname, amount, startdate, enddate);
                }

            }
        });

        return view;
    }

    private void addToFirebase( String tripName, String Amount, String startDate, String endDate) {
        String tripId = databaseReference.push().getKey();
        if (auth.getUid() != null && tripId != null) {
            databaseReference.child("Users").child(auth.getUid()).child("Trips").child(tripId).setValue(trip);
            trip.setTripId(tripId);
            Toast.makeText(getActivity(),"New trip added successfully)",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getActivity(),"Failed To add you trip)",Toast.LENGTH_SHORT).show();
        }
        

    }

    private void initView(View view) {
        tripName = view.findViewById(R.id.tripName);
        Amount = view.findViewById(R.id.amount);
        startDate = view.findViewById(R.id.startDate);
        endDate = view.findViewById(R.id.endDate);
        setTrip = view.findViewById(R.id.setTrip);
    }

}