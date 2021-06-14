package com.example.myapplication.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fragments.ExpenseFragment;
import com.example.myapplication.Fragments.PlanFragment;
import com.example.myapplication.Models.Trip;
import com.example.myapplication.Models.UserPost;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private ArrayList<Trip> trips = new ArrayList<>();
    private Context context;
    private FragmentManager fm;

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
            holder.userTripSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.FrameContainer, new ExpenseFragment());
                    ft.commit();
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
        TextView tripName;
        CardView userTripSmall;
        public ViewHolder(@NonNull @NotNull View itemView) {

            super(itemView);
            tripName = itemView.findViewById(R.id.tripName);
            userTripSmall = itemView.findViewById(R.id.userTripSmall);

        }
    }
}
