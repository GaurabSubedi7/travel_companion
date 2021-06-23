package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.myapplication.Models.ThirdPartyService;
import com.example.myapplication.Models.User;
import com.example.myapplication.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import static com.example.myapplication.MainActivity.MY_DATABASE;

public class RegistrationActivity extends AppCompatActivity {
    ActivityRegistrationBinding binding;

    //Firebase Classes
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    //Loading prompt class
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance(MY_DATABASE);
        auth = FirebaseAuth.getInstance();

        databaseReference = database.getReference();
        progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setTitle("Account Creation");
        progressDialog.setMessage("Creating your account");

        Intent intent = getIntent();
        String accountType = intent.getStringExtra("accountType");
        if(accountType.equals("businessAccount")){
            binding.txtAccountType.setText("Signing Up as Business");
        }else{
            binding.txtAccountType.setText("Signing Up as User");
        }

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
                                //gets userID from the Auth Task we need this to store users in database
                                String id = task.getResult().getUser().getUid();
                                if(accountType.equals("businessAccount")){
                                    ThirdPartyService tpService = new ThirdPartyService(usernameReg, emailReg);
                                    databaseReference.child("Services").child(id).setValue(tpService);
                                }else {
                                    User users = new User(usernameReg, emailReg);
                                    databaseReference.child("Users").child(id).setValue(users);
                                }

                                Toast.makeText(RegistrationActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();

                                if (auth.getCurrentUser() != null) {
                                    Intent intent = new Intent(RegistrationActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                                finish();
                            } else {
                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //Back to login
        binding.backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    Toast.makeText(RegistrationActivity.this, "Session already exist, Redirecting to dashboard", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}