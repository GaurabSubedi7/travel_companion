package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fragments.ExpenseFragment;
import com.example.myapplication.Fragments.IndividualTripFragment;
import com.example.myapplication.LoginActivity;
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
import java.util.Objects;

import static com.example.myapplication.MainActivity.MY_DATABASE;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private ArrayList<Trip> trips = new ArrayList<>();
    private Context context;
    private FragmentManager fm;

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    public TripAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_plan_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TripAdapter.ViewHolder holder, int position) {
        if(trips.get(position).getTripName()!=null){
            holder.tripName.setText(trips.get(position).getTripName());
            holder.startDate.setText(trips.get(position).getStartDate());
            holder.endDate.setText(trips.get(position).getEndDate());
            holder.userTripSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = fm.beginTransaction();
                    IndividualTripFragment individualTripFragment = new IndividualTripFragment();

                    //send data to IndividualTripFragment
                    Bundle bundle = new Bundle();
                    bundle.putString("myTripID", trips.get(position).getTripId());
                    bundle.putString("myTripName", trips.get(position).getTripName());
                    bundle.putString("myTripAmount", trips.get(position).getAmount());
                    bundle.putString("myTripLocation", trips.get(position).getLocation());
                    bundle.putString("myTripStartDate", trips.get(position).getStartDate());
                    bundle.putString("myTripEndDate", trips.get(position).getEndDate());
                    individualTripFragment.setArguments(bundle);
                    ft.replace(R.id.FrameContainer, individualTripFragment).addToBackStack(null);
                    ft.commit();
                }
            });
            
            //Delete Individual trips
            holder.deleteTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder tripTerminator = new AlertDialog.Builder(Objects.requireNonNull(context))
                            .setTitle("Want To Delete " + trips.get(position).getTripName() + "?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //should be empty
                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Delete From Firebase
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            if(auth.getUid() != null){
                                                for(DataSnapshot data : snapshot.child("Users").child(auth.getUid()).child("Trips").getChildren()){
                                                    String myKey = data.getKey();
                                                    if(myKey != null && myKey.equals(trips.get(position).getTripId())){
                                                        data.getRef().removeValue();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                    tripTerminator.create().show();
                }
            });
        }else{
            Toast.makeText(context, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
        }
        
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void setTrip(ArrayList<Trip> trip) {
        this.trips = trip;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tripName, startDate, endDate;
        CardView userTripSmall;
        ImageView deleteTrip;
        public ViewHolder(@NonNull @NotNull View itemView) {

            super(itemView);
            tripName = itemView.findViewById(R.id.name);
            userTripSmall = itemView.findViewById(R.id.userTripSmall);
            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            deleteTrip = itemView.findViewById(R.id.deleteTrip);
        }
    }
}
