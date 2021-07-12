package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.View;

import com.example.myapplication.Fragments.BudgetFragment;
import com.example.myapplication.Fragments.HomeFragment;
import com.example.myapplication.Fragments.MapFragment;
import com.example.myapplication.Fragments.ProfileFragment;
import com.example.myapplication.Fragments.ProgressBarFragment;
import com.example.myapplication.Fragments.ServiceHomeFragment;
import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.example.myapplication.MainActivity.MY_DATABASE;

public class DashboardActivity extends AppCompatActivity {
    private BottomNavigationView bnv, bnvService;
    private ActivityDashboardBinding binding;

    //firebase
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private final DatabaseReference databaseReference = database.getReference();

    private static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bnv = findViewById(R.id.bottomNavigation);
        bnvService = findViewById(R.id.bottomNavigationService);

        getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,new ProgressBarFragment()).commit();
        bnv.setVisibility(View.GONE);
        bnvService.setVisibility(View.GONE);
        getAccountType();

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment temp = null;
                switch(item.getItemId()){
                    case R.id.home:
                        temp = new HomeFragment();
                        break;
                    case R.id.budget:
                        temp = new BudgetFragment();
                        break;
                    case R.id.map:
                        temp = new MapFragment();
                        break;
                    case R.id.profile:
                        temp = new ProfileFragment();
                        break;
                }
                //Replace the FrameContainer with our temp container
                getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,temp).commit();
                return true;
            }
        });

        bnvService.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment temp =null;
                switch(item.getItemId()){
                    case R.id.homeService:
                        temp = new ServiceHomeFragment();
                        break;
                    case R.id.profileService:
                        temp = new ProfileFragment();
                        break;
                }
                //Replacing the FrameContainer with our temp container
                getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,temp).commit();
                return true;
            }
        });
    }

    private void getAccountType(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (auth.getUid() != null){
                        String accountType = snapshot.child("Users").child(auth.getUid()).child("accountType").getValue(String.class);
                        if(accountType != null){
                            if(accountType.equals("businessAccount")){
                                getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,new ServiceHomeFragment()).commit();
                                bnv.setVisibility(View.GONE);
                                bnvService.setVisibility(View.VISIBLE);
                            }else{
                                getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer,new HomeFragment()).commit();
                                bnv.setVisibility(View.VISIBLE);
                                bnvService.setVisibility(View.GONE);
                            }
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