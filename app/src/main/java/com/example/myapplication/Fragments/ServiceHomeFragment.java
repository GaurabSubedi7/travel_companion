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
import androidx.recyclerview.widget.RecyclerView;

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

public class ServiceHomeFragment extends Fragment {

    private TextView serviceName;
    private ImageView serviceProfileImage, btnAddServices;
    private BottomNavigationView bottomNavigationView;

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
        return view;
    }

    private void initView(View view){
        serviceName = view.findViewById(R.id.txtServiceName);
        btnAddServices = view.findViewById(R.id.btnAddServices);
        serviceProfileImage = view.findViewById(R.id.serviceProfileImage);
        serviceFeedRecView = view.findViewById(R.id.serviceFeedRecView);
    }
}

