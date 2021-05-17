package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.View;

import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    ActivityDashboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });




    }
}
//TODO: add fragments for each menu items and design those fragments