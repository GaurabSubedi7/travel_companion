package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.example.myapplication.R;
import com.google.android.material.navigation.NavigationView;


public class BudgetFragment extends Fragment {


    public BudgetFragment() {

    }
    private Button addPlan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_budget, container, false);
        addPlan = view.findViewById(R.id.btnAddPlan);

//        addPlan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlanFragment planFragment = new PlanFragment();
//                if(getFragmentManager() != null) {
//                    planFragment.show(getFragmentManager(), "ADD a trip");
//                }
//            }
//        });


        return view;
    }
}