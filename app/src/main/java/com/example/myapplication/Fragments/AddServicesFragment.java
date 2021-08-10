package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddServicesFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Button btnAddService, btnCancelService;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_services, container, false);
        initView(view);
        if(getActivity() != null) {
            bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationService);
            bottomNavigationView.setVisibility(View.GONE);
        }

        btnCancelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFragmentManager()!= null) {
                    if (getFragmentManager().getBackStackEntryCount() != 0) {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        getFragmentManager().popBackStack();
                    } else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        getFragmentManager().beginTransaction().replace(R.id.FrameContainer, new ServiceHomeFragment());
                    }
                }
            }
        });

        return view;
    }

    private void initView(View view) {
        btnAddService = view.findViewById(R.id.btnAddNewService);
        btnCancelService = view.findViewById(R.id.btnCancelService);
    }
}