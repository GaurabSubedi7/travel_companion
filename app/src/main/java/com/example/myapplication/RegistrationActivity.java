package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.Models.User;
import com.example.myapplication.databinding.ActivityRegestrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {
    ActivityRegestrationBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private static final String TAG = "RegistrationActivity";

    //Loading prompt class
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegestrationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setTitle("Account Creation");
        progressDialog.setMessage("Creating your account");


        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.emailReg.getText().toString(),binding.passwordReg.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Log.d(TAG, "onComplete: **********************************************************************************************");
                                    User users = new User(binding.usernameReg.getText().toString(), binding.emailReg.getText().toString(), binding.passwordReg.getText().toString());
                                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                                    System.out.println("\nUsername : " + users.getUserName());
                                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

                                    //gets userID from the Auth Task we need this to store users in database

                                    String id = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                                    Log.d(TAG, "onComplete: " + id);
                                    databaseReference = database.getReference("message");
                                    databaseReference.setValue(users);
                                    Log.d(TAG, "onComplete: ##################################################################################");
                                    Toast.makeText(RegistrationActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }
}
/*
TODO:
 Layout / Constraints management
    Signup, Login page
 Add Input validation and form validation in userName, Email and Password section...
 Click Effect on various redirection points
 After user clicks the signup button... (Creates a User Account) so redirection back to login page after confirmation.
*/