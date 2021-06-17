package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;

public class ExpenseFragment extends Fragment {

    //Todo add categories and amount  on clicking the add button retrieve added cateogries in the view
    private Button addCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        addCategory = view.findViewById(R.id.addCategory);
        String myTripId = this.getArguments().getString("myTripID");

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryFragment cF = new CategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("selectedTripId", myTripId);
                if (getFragmentManager() != null) {
                    cF.setArguments(bundle);
                    cF.show(getFragmentManager(), "add new Spending");
                }
            }
        });

        return view;
    }
}