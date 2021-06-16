package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Adapters.TripAdapter;
import com.example.myapplication.Models.Expenses;
import com.example.myapplication.Models.Trip;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import static com.example.myapplication.MainActivity.MY_DATABASE;


public class CategoryFragment extends DialogFragment {

    private EditText category, amount;
    private Button addCategory;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance(MY_DATABASE).getReference();


    Expenses expense;


    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_category, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view).setTitle("Note down how much you spent on what...");
        initView(view);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = category.getText().toString();
                String expenditure = amount.getText().toString();
                String tripID = getArguments().getString("selectedTripId");
                expense = new Expenses(categoryName, expenditure);
                if (categoryName != null && expenditure != null) {
                    addToFireBase(tripID);

                }

            }
        });


        return builder.create();
    }


    private void addToFireBase(String tripID) {
        if (auth.getUid() != null && tripID != null) {
            String expensesID = databaseReference.push().getKey();
            databaseReference.child("Users").child(auth.getUid()).child("Trips").child(tripID).child("expenses").child(expensesID).setValue(expense);

            Toast.makeText(getActivity(), "New Expenditure added successfully)", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity(), "Failed To add your Expenditure)", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView(View view) {
        category = view.findViewById(R.id.category);
        amount = view.findViewById(R.id.amount);
        addCategory = view.findViewById(R.id.addCategory);
    }
}