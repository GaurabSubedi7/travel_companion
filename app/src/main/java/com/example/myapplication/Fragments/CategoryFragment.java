package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.Models.Expense;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.myapplication.MainActivity.MY_DATABASE;


public class CategoryFragment extends DialogFragment {

    private EditText expenseDescription, amount;
    private Button addCategory;
    private Spinner categorySpinner;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance(MY_DATABASE).getReference();
    private Expense expense;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_category, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Add Your Expense");
        initView(view);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                Date date = calendar.getTime();
                String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
                String categoryName = categorySpinner.getSelectedItem().toString();
                String description = "";
                if(expenseDescription != null){
                    description = expenseDescription.getText().toString();
                }
                String expenditure = amount.getText().toString();
                String tripID = getArguments().getString("finallyTripId");
                if (categorySpinner.getSelectedItem() != null && !expenditure.equals("") && tripID != null) {
                    expense = new Expense(categoryName, description, expenditure, currentTime);
                    addToFireBase(tripID);
                }
            }
        });
        return builder.create();
    }

    private void addToFireBase(String tripID) {
        String expensesID = databaseReference.push().getKey();
        if (auth.getUid() != null && tripID != null && expensesID != null) {
            databaseReference.child("Users").child(auth.getUid()).child("Trips").child(tripID).child("expenses").child(expensesID).setValue(expense);
            Toast.makeText(getActivity(), "Expenditure Added Successfully", Toast.LENGTH_SHORT).show();
            dismiss();
        } else {
            Toast.makeText(getActivity(), "Something Went Wrong, Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView(View view) {
        expenseDescription = view.findViewById(R.id.expenseDescription);
        amount = view.findViewById(R.id.amount);
        addCategory = view.findViewById(R.id.addCategory);
        categorySpinner = view.findViewById(R.id.categoryName);
    }
}