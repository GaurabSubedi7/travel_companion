package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.View;

import com.example.myapplication.Fragments.BudgetFragment;
import com.example.myapplication.Fragments.HomeFragment;
import com.example.myapplication.Fragments.MapFragment;
import com.example.myapplication.Fragments.ProfileFragment;
import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class DashboardActivity extends AppCompatActivity {


    BottomNavigationView bnv;


    ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//
        //fragments work
        getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,new HomeFragment()).commit();
        bnv = (BottomNavigationView)findViewById(R.id.bottomNavigation);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment temp=null;
                switch(item.getItemId()){
                    case R.id.home:
                        temp= new HomeFragment();
                        break;
                    case R.id.budget:
                        temp= new BudgetFragment();
                        break;
                    case R.id.map:
                        temp= new MapFragment();
                        break;
                    case R.id.profile:
                        temp= new ProfileFragment();
                        break;
                }
                //Replacing the FrameContainer with our temp container
                getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,temp).commit();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

