package com.example.myapplication.Fragments;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.Models.Expense;
import com.example.myapplication.R;
import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class ExpenseReportFragment extends Fragment {

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    private Expense expense;
    public ArrayList<Expense> expenses = new ArrayList<>();

    private ImageView myTrips;
    private RelativeLayout dailyPieChartRelLayout, weeklyPieChartRelLayout, monthlyPieChartRelLayout,
            noDailyExpensesRelLayout, noWeeklyExpensesRelLayout, noMonthlyExpensesRelLayout;
    private TextView dailyFoodExpense, dailyTransportExpense, dailyHotelExpense, dailyMiscExpense,
            weeklyFoodExpense, weeklyTransportExpense, weeklyHotelExpense, weeklyMiscExpense,
            monthlyFoodExpense, monthlyTransportExpense, monthlyHotelExpense, monthlyMiscExpense;
    private PieChart dailyPieChart, weeklyPieChart, monthlyPieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_report, container, false);

        initView(view);

        //go to BudgetFragment
        myTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (fm != null) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.FrameContainer, new BudgetFragment()).addToBackStack(null);
                    ft.commit();
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);
        Date date = calendar.getTime();
        System.out.println("this is the date : " + date);

        getDataFromFirebase();
        return view;
    }

    //get expenses from firebase
    private void getDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(auth.getUid() != null){
                        expenses.clear();
                        for (DataSnapshot trips: snapshot.child("Users").child(auth.getUid()).child("Trips")
                                .getChildren()){
                            for (DataSnapshot data: trips.child("expenses").getChildren()){
                                String myKey = data.getKey();
                                expense = data.getValue(Expense.class);
                                if (myKey != null && expense != null) {
                                    expense.setExpenseId(myKey);
                                } else {
                                    Toast.makeText(getActivity(), "ExpenseId became null", Toast.LENGTH_SHORT).show();
                                }
                                expenses.add(expense);
                                Collections.reverse(expenses);
                            }
                        }
                        if(!expenses.isEmpty()){
                            for(Expense e: expenses){
                                System.out.println("=== : " + e.getCategoryName() + " Rs. " + e.getAmount());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView(View view){
        myTrips = view.findViewById(R.id.myTrips);

        noDailyExpensesRelLayout = view.findViewById(R.id.noDailyExpensesRelLayout);
        noWeeklyExpensesRelLayout = view.findViewById(R.id.noWeeklyExpensesRelLayout);
        noMonthlyExpensesRelLayout= view.findViewById(R.id.noMonthlyExpensesRelLayout);

        dailyPieChartRelLayout = view.findViewById(R.id.dailyPieChartRelLayout);
        weeklyPieChartRelLayout = view.findViewById(R.id.weeklyPieChartRelLayout);
        monthlyPieChartRelLayout = view.findViewById(R.id.monthlyPieChartRelLayout);

        dailyFoodExpense = view.findViewById(R.id.dailyFoodExpenses);
        weeklyFoodExpense = view.findViewById(R.id.weeklyFoodExpenses);
        monthlyFoodExpense = view.findViewById(R.id.monthlyFoodExpenses);

        dailyTransportExpense = view.findViewById(R.id.dailyTransportExpenses);
        weeklyTransportExpense = view.findViewById(R.id.weeklyTransportExpenses);
        monthlyTransportExpense = view.findViewById(R.id.monthlyTransportExpenses);

        dailyHotelExpense = view.findViewById(R.id.dailyHotelExpenses);
        weeklyHotelExpense = view.findViewById(R.id.weeklyHotelExpenses);
        monthlyHotelExpense = view.findViewById(R.id.monthlyHotelExpenses);

        dailyMiscExpense = view.findViewById(R.id.dailyMiscExpenses);
        weeklyMiscExpense = view.findViewById(R.id.weeklyMiscExpenses);
        monthlyMiscExpense = view.findViewById(R.id.monthlyMiscExpenses);

        dailyPieChart = view.findViewById(R.id.dailyExpensePieChart);
        weeklyPieChart = view.findViewById(R.id.weeklyExpensePieChart);
        monthlyPieChart = view.findViewById(R.id.monthlyExpensePieChart);
    }
}
