package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.Models.User;
import com.example.myapplication.databinding.ActivityRegistrationBinding;
import com.example.myapplication.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;

    //Firebase Classes
    private FirebaseAuth auth;
    private static final String MY_DATABASE = "https://travel-companion-9af58-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    //Loading prompt class
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance(MY_DATABASE);
        auth = FirebaseAuth.getInstance();
        databaseReference = database.getReference();
        progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setTitle("Account Creation");
        progressDialog.setMessage("Creating your account");


        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailReg = binding.emailReg.getText().toString().trim();
                String passwordReg = binding.passwordReg.getText().toString().trim();
                String usernameReg = binding.usernameReg.getText().toString().trim();
                if(usernameReg.isEmpty() || passwordReg.isEmpty() || emailReg.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Some Input Field missing.. Please try again : ", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(emailReg, passwordReg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                User users = new User(usernameReg, emailReg);

                                //gets userID from the Auth Task we need this to store users in database
                                String id = task.getResult().getUser().getUid();

                                databaseReference.child("Users").child(id).setValue(users);
                                Toast.makeText(RegistrationActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //Do not show signup if session already exists
        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(RegistrationActivity.this, DashboardActivity.class);
            startActivity(intent);
        }


        //Back to login
        binding.backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    Toast.makeText(RegistrationActivity.this, "Session Exist, Redirecting to dashboard", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, DashboardActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
/*
TODO:

*/