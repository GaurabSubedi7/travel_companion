package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import static com.example.myapplication.MainActivity.MY_DATABASE;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder>{

    private Context context;
    private String tripId;
    private ArrayList<Checklist> checklists = new ArrayList<>();

    //firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private final DatabaseReference databaseReference = database.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public ChecklistAdapter(Context context, String tripId) {
        this.context = context;
        this.tripId = tripId;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checklist_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChecklistAdapter.ViewHolder holder, int position) {
        String checklistId = checklists.get(position).getChecklistId();
        holder.placeName.setText(checklists.get(position).getTitle());

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
