package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fragments.MapFragment;
import com.example.myapplication.Models.Checklist;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.myapplication.MainActivity.MY_DATABASE;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder>{

    private Context context;
    private FragmentManager fm;
    private String tripId;
    private ArrayList<Checklist> checklists = new ArrayList<>();

    //firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private final DatabaseReference databaseReference = database.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public ChecklistAdapter(Context context, FragmentManager fm, String tripId) {
        this.context = context;
        this.tripId = tripId;
        this.fm = fm;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checklist_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChecklistAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String checklistId = checklists.get(position).getChecklistId();
        holder.placeName.setText(checklists.get(position).getTitle());

        //Navigate to the location of the checklist on map
        holder.showDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", checklists.get(position).getTitle());
                bundle.putDouble("latitude", checklists.get(position).getLatitude());
                bundle.putDouble("longitude", checklists.get(position).getLongitude());
                mapFragment.setArguments(bundle);
                ft.replace(R.id.FrameContainer, mapFragment).addToBackStack(null);
                ft.commit();
            }
        });

        //Delete a checklist item
        holder.deleteChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder checklistTerminator = new AlertDialog.Builder(Objects.requireNonNull(context))
                        .setTitle("Want To Delete " + checklists.get(position).getTitle() + "?")
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
                                            for(DataSnapshot data : snapshot.child("Users").child(auth.getUid()).child("Trips")
                                                    .child(tripId).child("Checklist").getChildren()){
                                                String myKey = data.getKey();
                                                if(myKey != null && myKey.equals(checklists.get(position).getChecklistId())){
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
                checklistTerminator.create().show();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(auth.getUid() != null){
                    Boolean checkboxChecked = snapshot.child("Users").child(auth.getUid()).child("Trips")
                            .child(tripId).child("Checklist").child(checklistId).child("checked").getValue(Boolean.class);
                    if(checkboxChecked != null){
                        holder.placeName.setChecked(checkboxChecked);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        holder.placeName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(auth.getUid() != null){
                    if(isChecked){
                        databaseReference.child("Users").child(auth.getUid()).child("Trips").child(tripId).child("Checklist")
                                .child(checklists.get(position).getChecklistId()).child("checked").setValue(true);
                        holder.placeName.setChecked(true);
                    }else{
                        databaseReference.child("Users").child(auth.getUid()).child("Trips").child(tripId).child("Checklist")
                                .child(checklists.get(position).getChecklistId()).child("checked").setValue(false);
                        holder.placeName.setChecked(false);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return checklists.size();
    }

    public void setChecklists(ArrayList<Checklist> checklists) {
        this.checklists = checklists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CheckBox placeName;
        private ImageView showDestination, deleteChecklist;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            showDestination = itemView.findViewById(R.id.showDestination);
            deleteChecklist = itemView.findViewById(R.id.deleteChecklist);
        }
    }
}
