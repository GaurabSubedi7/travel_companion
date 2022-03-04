package com.example.myapplication.Fragments;

import static com.example.myapplication.MainActivity.MY_DATABASE;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public ArrayList<Expense> dailyExpenses = new ArrayList<>();
    public ArrayList<Expense> weeklyExpenses = new ArrayList<>();
    public ArrayList<Expense> monthlyExpenses = new ArrayList<>();

    private ImageView myTrips;
    private RelativeLayout dailyPieChartRelLayout, weeklyPieChartRelLayout, monthlyPieChartRelLayout,
            noDailyExpensesRelLayout, noWeeklyExpensesRelLayout, noMonthlyExpensesRelLayout;
    private TextView dailyFoodExpense, dailyTransportExpense, dailyHotelExpense, dailyMiscExpense,
            weeklyFoodExpense, weeklyTransportExpense, weeklyHotelExpense, weeklyMiscExpense,
            monthlyFoodExpense, monthlyTransportExpense, monthlyHotelExpense, monthlyMiscExpense;
    private PieChart dailyPieChart, weeklyPieChart, monthlyPieChart;
    public PieData pieData;

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
    //the code below has to be the worst code i wrote till date. But got few hours to my external exam...
    //so it is what it is....
    private void getDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(auth.getUid() != null){
                        expenses.clear();
                        dailyExpenses.clear();
                        weeklyExpenses.clear();
                        monthlyExpenses.clear();
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
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                            for(Expense e: expenses){
                                //daily expense arraylist
                                try {
                                    Calendar today = Calendar.getInstance();
                                    @SuppressLint("SimpleDateFormat")
                                    String dateTodayString = new SimpleDateFormat("yyyy-MM-dd").format(today.getTime());
                                    Date dateToday = formatter.parse(dateTodayString);
                                    Date dailyExpenseDate = formatter.parse(e.getExpenseAdditionDate());
                                    if(dateToday.compareTo(dailyExpenseDate) == 0)
                                        dailyExpenses.add(e);
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }

                                //weekly expense arraylist
                                try {
                                    Calendar sevenDaysAgo = Calendar.getInstance();
                                    sevenDaysAgo.add(Calendar.DATE, -7);
                                    @SuppressLint("SimpleDateFormat")
                                    String dateSevenDaysAgoString = new SimpleDateFormat("yyyy-MM-dd").format(sevenDaysAgo.getTime());
                                    Date dateSevenDaysAgo = formatter.parse(dateSevenDaysAgoString);
                                    Date weeklyExpenseDate = formatter.parse(e.getExpenseAdditionDate());
                                    if(dateSevenDaysAgo.compareTo(weeklyExpenseDate) < 0)
                                        weeklyExpenses.add(e);
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }

                                //monthly expense arraylist
                                try {
                                    Calendar monthAgo = Calendar.getInstance();
                                    monthAgo.add(Calendar.DATE, -30);
                                    @SuppressLint("SimpleDateFormat")
                                    String dateMonthAgoString = new SimpleDateFormat("yyyy-MM-dd").format(monthAgo.getTime());
                                    Date dateMonthAgo = formatter.parse(dateMonthAgoString);
                                    Date monthlyExpenseDate = formatter.parse(e.getExpenseAdditionDate());
                                    if(dateMonthAgo.compareTo(monthlyExpenseDate) < 0)
                                        monthlyExpenses.add(e);
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                            }

                            //daily expense pieChart calculation
                            if (!dailyExpenses.isEmpty()) {
                                float dailyFood = 0, dailyTransport = 0, dailyHotel = 0, dailyMisc = 0;

                                noDailyExpensesRelLayout.setVisibility(View.GONE);
                                dailyPieChartRelLayout.setVisibility(View.VISIBLE);

                                for (Expense e : dailyExpenses) {
                                    switch (e.getCategoryName().toLowerCase()) {
                                        case "food":
                                            dailyFood += Float.parseFloat(e.getAmount());
                                            break;
                                        case "transport":
                                            dailyTransport += Float.parseFloat(e.getAmount());
                                            break;
                                        case "hotel":
                                            dailyHotel += Float.parseFloat(e.getAmount());
                                            break;
                                        case "miscellaneous":
                                            dailyMisc += Float.parseFloat(e.getAmount());
                                            break;
                                        default:
                                            System.out.println("Something fucked up");
                                            break;
                                    }
                                }

                                //generate data for daily expense pieChart
                                pieData = generateDailyExpensePieChart(dailyFood, dailyTransport, dailyHotel, dailyMisc);

                                //set values in pieChart
                                setDailyExpensePieChart(pieData);
                            } else {
                                noDailyExpensesRelLayout.setVisibility(View.VISIBLE);
                                dailyPieChartRelLayout.setVisibility(View.GONE);
                            }

                            //weekly expense pieChart calculation
                            if (!weeklyExpenses.isEmpty()) {
                                float weeklyFood = 0, weeklyTransport = 0, weeklyHotel = 0, weeklyMisc = 0;

                                noWeeklyExpensesRelLayout.setVisibility(View.GONE);
                                weeklyPieChartRelLayout.setVisibility(View.VISIBLE);

                                for (Expense e : weeklyExpenses) {
                                    switch (e.getCategoryName().toLowerCase()) {
                                        case "food":
                                            weeklyFood += Float.parseFloat(e.getAmount());
                                            break;
                                        case "transport":
                                            weeklyTransport += Float.parseFloat(e.getAmount());
                                            break;
                                        case "hotel":
                                            weeklyHotel += Float.parseFloat(e.getAmount());
                                            break;
                                        case "miscellaneous":
                                            weeklyMisc += Float.parseFloat(e.getAmount());
                                            break;
                                        default:
                                            System.out.println("Something fucked up");
                                            break;
                                    }
                                }

                                //generate data for daily expense pieChart
                                pieData = generateWeeklyExpensePieChart(weeklyFood, weeklyTransport, weeklyHotel, weeklyMisc);

                                //set values in pieChart
                                setWeeklyExpensePieChart(pieData);
                            } else {
                                noWeeklyExpensesRelLayout.setVisibility(View.VISIBLE);
                                weeklyPieChartRelLayout.setVisibility(View.GONE);
                            }

                            //monthly expense pieChart calculation
                            if (!monthlyExpenses.isEmpty()) {
                                float monthlyFood = 0, monthlyTransport = 0, monthlyHotel = 0, monthlyMisc = 0;

                                noMonthlyExpensesRelLayout.setVisibility(View.GONE);
                                monthlyPieChartRelLayout.setVisibility(View.VISIBLE);

                                for (Expense e : monthlyExpenses) {
                                    switch (e.getCategoryName().toLowerCase()) {
                                        case "food":
                                            monthlyFood += Float.parseFloat(e.getAmount());
                                            break;
                                        case "transport":
                                            monthlyTransport += Float.parseFloat(e.getAmount());
                                            break;
                                        case "hotel":
                                            monthlyHotel += Float.parseFloat(e.getAmount());
                                            break;
                                        case "miscellaneous":
                                            monthlyMisc += Float.parseFloat(e.getAmount());
                                            break;
                                        default:
                                            System.out.println("Something fucked up");
                                            break;
                                    }
                                }

                                //generate data for daily expense pieChart
                                pieData = generateMonthlyExpensePieChart(monthlyFood, monthlyTransport, monthlyHotel, monthlyMisc);

                                //set values in pieChart
                                setMonthlyExpensePieChart(pieData);
                            } else {
                                noMonthlyExpensesRelLayout.setVisibility(View.VISIBLE);
                                monthlyPieChartRelLayout.setVisibility(View.GONE);
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

    //generate data for daily expense PieChart
    private PieData generateDailyExpensePieChart(float foodExpenses, float transportExpenses, float hotelExpenses, float miscellaneousExpenses) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        if (foodExpenses != 0) {
            entries.add(new PieEntry(foodExpenses, "Food"));
            dailyFoodExpense.setVisibility(View.VISIBLE);
            int f = (int) foodExpenses;
            dailyFoodExpense.setText("Food : NRs. " + f);
        }else{
            dailyFoodExpense.setVisibility(View.GONE);
        }

        if (transportExpenses != 0) {
            entries.add(new PieEntry(transportExpenses, "Transport"));
            dailyTransportExpense.setVisibility(View.VISIBLE);
            int t = (int) transportExpenses;
            dailyTransportExpense.setText("Transport : NRs. " + t);
        }else{
            dailyTransportExpense.setVisibility(View.GONE);
        }

        if (hotelExpenses != 0) {
            entries.add(new PieEntry(hotelExpenses, "Hotel"));
            dailyHotelExpense.setVisibility(View.VISIBLE);
            int h = (int) hotelExpenses;
            dailyHotelExpense.setText("Hotel : NRs. " + h);
        }else{
            dailyHotelExpense.setVisibility(View.GONE);
        }

        if (miscellaneousExpenses != 0) {
            entries.add(new PieEntry(miscellaneousExpenses, "Miscellaneous"));
            dailyMiscExpense.setVisibility(View.VISIBLE);
            int m = (int) miscellaneousExpenses;
            dailyMiscExpense.setText("Miscellaneous : NRs. " + m);
        }else{
            dailyMiscExpense.setVisibility(View.GONE);
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(ColorTemplate.COLORFUL_COLORS);
        ds.setSliceSpace(2f);
        ds.setValueTextColor(Color.WHITE);
        ds.setValueTextSize(20f);

        return new PieData(ds);
    }

    //generate data for weekly expense PieChart
    private PieData generateWeeklyExpensePieChart(float foodExpenses, float transportExpenses, float hotelExpenses, float miscellaneousExpenses) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        if (foodExpenses != 0) {
            entries.add(new PieEntry(foodExpenses, "Food"));
            weeklyFoodExpense.setVisibility(View.VISIBLE);
            int f = (int) foodExpenses;
            weeklyFoodExpense.setText("Food : NRs. " + f);
        }else{
            weeklyFoodExpense.setVisibility(View.GONE);
        }

        if (transportExpenses != 0) {
            entries.add(new PieEntry(transportExpenses, "Transport"));
            weeklyTransportExpense.setVisibility(View.VISIBLE);
            int t = (int) transportExpenses;
            weeklyTransportExpense.setText("Transport : NRs. " + t);
        }else{
            weeklyTransportExpense.setVisibility(View.GONE);
        }

        if (hotelExpenses != 0) {
            entries.add(new PieEntry(hotelExpenses, "Hotel"));
            weeklyHotelExpense.setVisibility(View.VISIBLE);
            int h = (int) hotelExpenses;
            weeklyHotelExpense.setText("Hotel : NRs. " + h);
        }else{
            weeklyHotelExpense.setVisibility(View.GONE);
        }

        if (miscellaneousExpenses != 0) {
            entries.add(new PieEntry(miscellaneousExpenses, "Miscellaneous"));
            weeklyMiscExpense.setVisibility(View.VISIBLE);
            int m = (int) miscellaneousExpenses;
            weeklyMiscExpense.setText("Miscellaneous : NRs. " + m);
        }else{
            weeklyMiscExpense.setVisibility(View.GONE);
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(ColorTemplate.COLORFUL_COLORS);
        ds.setSliceSpace(2f);
        ds.setValueTextColor(Color.WHITE);
        ds.setValueTextSize(20f);

        return new PieData(ds);
    }

    //generate data for monthly expense PieChart
    private PieData generateMonthlyExpensePieChart(float foodExpenses, float transportExpenses, float hotelExpenses, float miscellaneousExpenses) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        if (foodExpenses != 0) {
            entries.add(new PieEntry(foodExpenses, "Food"));
            monthlyFoodExpense.setVisibility(View.VISIBLE);
            int f = (int) foodExpenses;
            monthlyFoodExpense.setText("Food : NRs. " + f);
        }else{
            monthlyFoodExpense.setVisibility(View.GONE);
        }

        if (transportExpenses != 0) {
            entries.add(new PieEntry(transportExpenses, "Transport"));
            monthlyTransportExpense.setVisibility(View.VISIBLE);
            int t = (int) transportExpenses;
            monthlyTransportExpense.setText("Transport : NRs. " + t);
        }else{
            monthlyTransportExpense.setVisibility(View.GONE);
        }

        if (hotelExpenses != 0) {
            entries.add(new PieEntry(hotelExpenses, "Hotel"));
            monthlyHotelExpense.setVisibility(View.VISIBLE);
            int h = (int) hotelExpenses;
            monthlyHotelExpense.setText("Hotel : NRs. " + h);
        }else{
            monthlyHotelExpense.setVisibility(View.GONE);
        }

        if (miscellaneousExpenses != 0) {
            entries.add(new PieEntry(miscellaneousExpenses, "Miscellaneous"));
            monthlyMiscExpense.setVisibility(View.VISIBLE);
            int m = (int) miscellaneousExpenses;
            monthlyMiscExpense.setText("Miscellaneous : NRs. " + m);
        }else{
            monthlyMiscExpense.setVisibility(View.GONE);
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(ColorTemplate.COLORFUL_COLORS);
        ds.setSliceSpace(2f);
        ds.setValueTextColor(Color.WHITE);
        ds.setValueTextSize(20f);

        return new PieData(ds);
    }

    //daily expense Pie Chart
    @SuppressLint("ClickableViewAccessibility")
    private void setDailyExpensePieChart(PieData data) {
        dailyPieChart.getDescription().setEnabled(false);
        dailyPieChart.setHoleRadius(30f);
        dailyPieChart.setTransparentCircleRadius(40f);
        dailyPieChart.setData(data);
        Legend legend = dailyPieChart.getLegend();
        legend.setEnabled(false);
        dailyPieChart.animateXY(1000, 1000);
        dailyPieChart.setUsePercentValues(true);
        dailyPieChart.setClickable(true);
        dailyPieChart.invalidate();
    }

    //weekly expense Pie Chart
    @SuppressLint("ClickableViewAccessibility")
    private void setWeeklyExpensePieChart(PieData data) {
        weeklyPieChart.getDescription().setEnabled(false);
        weeklyPieChart.setHoleRadius(30f);
        weeklyPieChart.setTransparentCircleRadius(40f);
        weeklyPieChart.setData(data);
        Legend legend = weeklyPieChart.getLegend();
        legend.setEnabled(false);
        weeklyPieChart.animateXY(1000, 1000);
        weeklyPieChart.setUsePercentValues(true);
        weeklyPieChart.setClickable(true);
        weeklyPieChart.invalidate();
    }

    //monthly expense Pie Chart
    @SuppressLint("ClickableViewAccessibility")
    private void setMonthlyExpensePieChart(PieData data) {
        monthlyPieChart.getDescription().setEnabled(false);
        monthlyPieChart.setHoleRadius(30f);
        monthlyPieChart.setTransparentCircleRadius(40f);
        monthlyPieChart.setData(data);
        Legend legend = monthlyPieChart.getLegend();
        legend.setEnabled(false);
        monthlyPieChart.animateXY(1000, 1000);
        monthlyPieChart.setUsePercentValues(true);
        monthlyPieChart.setClickable(true);
        monthlyPieChart.invalidate();
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
