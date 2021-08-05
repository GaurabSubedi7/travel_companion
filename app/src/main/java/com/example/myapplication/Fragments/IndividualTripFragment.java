package com.example.myapplication.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapters.ChecklistAdapter;
import com.example.myapplication.Adapters.ExpenseAdapter;
import com.example.myapplication.Models.Checklist;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.myapplication.MainActivity.MY_DATABASE;

public class IndividualTripFragment extends Fragment {
    public IndividualTripFragment() {
    }
    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    private Expense expense;
    private Checklist checklist;
    public ArrayList<Expense> expenses = new ArrayList<>();
    public ArrayList<Checklist> checklists = new ArrayList<>();

    private TextView tripName, tripLocation, tripAmount, startDate, food, transport, hotel, misc;
    private ImageView locationImage;
    private RelativeLayout noExpenseRelLayout, pieChartRelLayout, noChecklistRelLayout, actualChecklistRelLayout;
    private PieChart pieChart;
    private Button btnAddExpenses, btnAddExpenses2, btnAddPlaces, btnAddPlaces2;
    private RecyclerView checklistRecView;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_trip, container, false);
        initView(view);

        //data from TripAdapter
        String myTripId = getArguments().getString("myTripID");
        String myTripName = getArguments().getString("myTripName");
        String myTripAmount = getArguments().getString("myTripAmount");
        String myTripLocation = getArguments().getString("myTripLocation");
        String myTripStartDate = getArguments().getString("myTripStartDate");
//        String myTripEndDate = getArguments().getString("myTripEndDate");

        //GET EXPENSES DATA FOR PIE CHART
        getExpenseFromFirebase(myTripId);
        getChecklistFromFirebase(myTripId);

        tripName.setText(myTripName);
        tripLocation.setText(myTripLocation);
        tripAmount.setText(myTripAmount);
        startDate.setText(myTripStartDate);

        if (getContext() != null) {
            switch (myTripLocation.toLowerCase()) {
                case "pokhara":
                    Glide.with(getContext()).asBitmap().load(R.mipmap.pokhara).into(locationImage);
                    break;
                case "kathmandu valley":
                    Glide.with(getContext()).asBitmap().load(R.mipmap.kathmandu).into(locationImage);
                    break;
                default:
                    Toast.makeText(getActivity(), "Something Went Horribly Wrong :(", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        btnAddExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to expenses Fragment
                gotToExpenseFragment(myTripId, myTripName, myTripAmount);
            }
        });

        btnAddExpenses2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to expenses Fragment
                gotToExpenseFragment(myTripId, myTripName, myTripAmount);
            }
        });

        btnAddPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to map fragment
                goToMapFragment();
            }
        });

        btnAddPlaces2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to map fragment
                goToMapFragment();
            }
        });

        return view;
    }

    private void gotToExpenseFragment(String myTripId, String myTripName, String myTripAmount){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ExpenseFragment expenseFragment = new ExpenseFragment();

        //send data to budget fragment
        Bundle bundle = new Bundle();
        bundle.putString("selectedTripId", myTripId);
        bundle.putString("selectedTripName", myTripName);
        bundle.putString("selectedTripAmount", myTripAmount);
        expenseFragment.setArguments(bundle);
        ft.replace(R.id.FrameContainer, expenseFragment).addToBackStack(null);
        ft.commit();
    }

    private void goToMapFragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        MapFragment mapFragment = new MapFragment();
        ft.replace(R.id.FrameContainer, mapFragment).addToBackStack(null);
        ft.commit();
    }

    //getChecklist
    private void getChecklistFromFirebase(String myTripId){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot != null){
                    if(auth.getUid() != null){
                        checklists.clear();
                        for (DataSnapshot data : snapshot.child("Users").child(auth.getUid()).child("Trips")
                                .child(myTripId).child("Checklist").getChildren()) {
                            String myKey = data.getKey();
                            checklist = data.getValue(Checklist.class);
                            if (myKey != null && checklist != null) {
                                checklist.setChecklistId(myKey);
                            } else {
                                Toast.makeText(getActivity(), "ChecklistId became null", Toast.LENGTH_SHORT).show();
                            }
                            checklists.add(checklist);
                            Collections.reverse(checklists);
                        }

                        if(!checklists.isEmpty()){
                            noChecklistRelLayout.setVisibility(View.GONE);
                            actualChecklistRelLayout.setVisibility(View.VISIBLE);

                            FragmentManager fm = getFragmentManager();
                            ChecklistAdapter adapter = new ChecklistAdapter(getContext(), fm, myTripId);
                            checklistRecView.setAdapter(adapter);
                            checklistRecView.setLayoutManager(new LinearLayoutManager(getContext()));

                            adapter.setChecklists(checklists);
                        }else{
                            noChecklistRelLayout.setVisibility(View.VISIBLE);
                            actualChecklistRelLayout.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //get expenses data from firebase
    private void getExpenseFromFirebase(String myTripId) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (auth.getUid() != null) {
                        expenses.clear();
                        for (DataSnapshot data : snapshot.child("Users").child(auth.getUid()).child("Trips")
                                .child(myTripId).child("expenses").getChildren()) {
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

                        if (!expenses.isEmpty()) {
                            float foodExpenses = 0, transportExpenses = 0, hotelExpenses = 0, miscellaneousExpenses = 0;

                            noExpenseRelLayout.setVisibility(View.GONE);
                            pieChartRelLayout.setVisibility(View.VISIBLE);

                            for (Expense e : expenses) {
                                switch (e.getCategoryName().toLowerCase()) {
                                    case "food":
                                        foodExpenses += Float.parseFloat(e.getAmount());
                                        break;
                                    case "transport":
                                        transportExpenses += Float.parseFloat(e.getAmount());
                                        break;
                                    case "hotel":
                                        hotelExpenses += Float.parseFloat(e.getAmount());
                                        break;
                                    case "miscellaneous":
                                        miscellaneousExpenses += Float.parseFloat(e.getAmount());
                                        break;
                                    default:
                                        System.out.println("Something fucked up");
                                        break;
                                }
                            }

                            //generate data for pieChart
                            PieData data = generatePieData(foodExpenses, transportExpenses, hotelExpenses, miscellaneousExpenses);

                            //set values in pieChart
                            setPieChart(data);
                        } else {
                            noExpenseRelLayout.setVisibility(View.VISIBLE);
                            pieChartRelLayout.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //pieChart
    @SuppressLint("ClickableViewAccessibility")
    private void setPieChart(PieData data) {
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setData(data);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        pieChart.animateXY(1000, 1000);
        pieChart.setUsePercentValues(true);
        pieChart.setClickable(true);
        pieChart.invalidate();
    }

    //generate data for PieChart
    private PieData generatePieData(float foodExpenses, float transportExpenses, float hotelExpenses, float miscellaneousExpenses) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        if (foodExpenses != 0) {
            entries.add(new PieEntry(foodExpenses, "Food"));
            food.setVisibility(View.VISIBLE);
            int f = (int) foodExpenses;
            food.setText("Food : NRs. " + f);
        }else{
            food.setVisibility(View.GONE);
        }

        if (transportExpenses != 0) {
            entries.add(new PieEntry(transportExpenses, "Transport"));
            transport.setVisibility(View.VISIBLE);
            int t = (int) transportExpenses;
            transport.setText("Transport : NRs. " + t);
        }else{
            transport.setVisibility(View.GONE);
        }

        if (hotelExpenses != 0) {
            entries.add(new PieEntry(hotelExpenses, "Hotel"));
            hotel.setVisibility(View.VISIBLE);
            int h = (int) hotelExpenses;
            hotel.setText("Hotel : NRs. " + h);
        }else{
            hotel.setVisibility(View.GONE);
        }

        if (miscellaneousExpenses != 0) {
            entries.add(new PieEntry(miscellaneousExpenses, "Miscellaneous"));
            misc.setVisibility(View.VISIBLE);
            int m = (int) miscellaneousExpenses;
            misc.setText("Miscellaneous : NRs. " + m);
        }else{
            misc.setVisibility(View.GONE);
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(ColorTemplate.COLORFUL_COLORS);
        ds.setSliceSpace(2f);
        ds.setValueTextColor(Color.WHITE);
        ds.setValueTextSize(20f);

        return new PieData(ds);
    }

    private void initView(View view) {
        tripName = view.findViewById(R.id.tripName);
        tripLocation = view.findViewById(R.id.tripLocation);
        tripAmount = view.findViewById(R.id.tripAmount);
        startDate = view.findViewById(R.id.startDate);

        pieChart = view.findViewById(R.id.pieChart);
        locationImage = view.findViewById(R.id.locationImage);
        noExpenseRelLayout = view.findViewById(R.id.noExpensesRelLayout);
        noChecklistRelLayout = view.findViewById(R.id.noChecklistRelLayout);

        actualChecklistRelLayout = view.findViewById(R.id.actualChecklistRelLayout);
        pieChartRelLayout = view.findViewById(R.id.pieChartRelLayout);
        btnAddExpenses = view.findViewById(R.id.btnAddExpenses);
        btnAddExpenses2 = view.findViewById(R.id.btnAddExpenses2);

        btnAddPlaces = view.findViewById(R.id.btnAddPlaces);
        btnAddPlaces2 = view.findViewById(R.id.btnAddPlaces2);

        food = view.findViewById(R.id.foodExpenses);
        hotel = view.findViewById(R.id.hotelExpenses);
        transport = view.findViewById(R.id.transportExpenses);
        misc = view.findViewById(R.id.miscExpenses);

        checklistRecView = view.findViewById(R.id.checklistRecView);
    }
}
