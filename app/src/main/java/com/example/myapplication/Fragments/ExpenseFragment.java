package com.example.myapplication.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapters.ExpenseAdapter;
import com.example.myapplication.Adapters.TripAdapter;
import com.example.myapplication.Models.Expense;
import com.example.myapplication.Models.Trip;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.myapplication.MainActivity.MY_DATABASE;

public class ExpenseFragment extends Fragment {

    private Expense expense;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();
    public ArrayList<Expense> expenses = new ArrayList<>();

    private TextView budgetAmount, txtYourTripName;
    private ImageView addCategory;
    private RecyclerView expensesRecView;
    private RelativeLayout noExpenseRelLayout;
    private NestedScrollView expensesNestedScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        initView(view);

        //data from IndividualTripFragment
        String myTripId = getArguments().getString("selectedTripId");
        String myTripName = getArguments().getString("selectedTripName");
        String myTripAmount = getArguments().getString("selectedTripAmount");

        txtYourTripName.setText(myTripName);
        budgetAmount.setText("NRs. " + myTripAmount);

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryFragment cF = new CategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("finallyTripId", myTripId);
                if (getFragmentManager() != null) {
                    cF.setArguments(bundle);
                    cF.show(getFragmentManager(), "add new Spending");
                }
            }
        });

        //get data from firebase
        getDataFromFirebase(myTripId);
        return view;
    }

    private void getDataFromFirebase(String myTripId) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    expenses.clear();
                    if (auth.getUid() != null) {
                        for (DataSnapshot data : snapshot.child("Users").child(auth.getUid()).child("Trips").child(myTripId).child("expenses").getChildren()) {
                            String myKey = data.getKey();
                            expense = data.getValue(Expense.class);
                            if (myKey != null && expense != null) {
                                expense.setExpenseId(myKey);
                            } else {
                                Toast.makeText(getActivity(), "ExpenseId became null", Toast.LENGTH_SHORT).show();
                            }
                            expenses.add(expense);
                            Collections.reverse(expenses);

                            // TODO: 6/19/21 update expense data from firebase without duplication
                        }

                        if (!expenses.isEmpty()) {
                            expensesNestedScrollView.setVisibility(View.VISIBLE);
                            noExpenseRelLayout.setVisibility(View.GONE);
                            ExpenseAdapter expenseAdapter = new ExpenseAdapter(getContext(), myTripId);
                            expensesRecView.setLayoutManager(new LinearLayoutManager(getContext()));
                            expensesRecView.setAdapter(expenseAdapter);
                            expenseAdapter.setExpenses(expenses);
                        } else {
                            expensesNestedScrollView.setVisibility(View.GONE);
                            noExpenseRelLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void initView(View view) {
        addCategory = view.findViewById(R.id.addCategory);
        expensesRecView = view.findViewById(R.id.expensesRecView);
        noExpenseRelLayout = view.findViewById(R.id.noExpenseRelativeLayout);
        expensesNestedScrollView = view.findViewById(R.id.expensesNestedScrollView);
        budgetAmount = view.findViewById(R.id.budgetAmount);
        txtYourTripName = view.findViewById(R.id.txtYourTripName);
    }
}