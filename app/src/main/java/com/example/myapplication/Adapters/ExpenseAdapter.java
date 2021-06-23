package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fragments.IndividualTripFragment;
import com.example.myapplication.Models.Expense;
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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private Context context;
    private String myTripId;
    private ArrayList<Expense> expenses = new ArrayList<>();

    private static final String TAG = "ExpenseAdapter";

    //firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(MY_DATABASE);
    private DatabaseReference databaseReference = database.getReference();

    public ExpenseAdapter(Context context, String myTripId) {
        this.context = context;
        this.myTripId = myTripId;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_expense_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExpenseAdapter.ViewHolder holder, int position) {
        if(expenses.get(position).getCategoryName() != null){
            holder.categoryName.setText(expenses.get(position).getCategoryName());
            holder.totalBudgetAmount.setText("NRs. " + expenses.get(position).getAmount());
            holder.expenseDesc.setText(expenses.get(position).getExpenseDescription());
            holder.expenseAdditionDate.setText(expenses.get(position).getExpenseAdditionDate());

            //delete expense
            holder.deleteExpense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder expenseTerminator = new AlertDialog.Builder(Objects.requireNonNull(context))
                            .setTitle("Want To Delete " + expenses.get(position).getExpenseDescription() + "?")
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
                                                for(DataSnapshot data : snapshot.child("Users").child(auth.getUid()).child("Trips").child(myTripId).child("expenses").getChildren()){
                                                    String myKey = data.getKey();
                                                    if(myKey != null && myKey.equals(expenses.get(position).getExpenseId())){
                                                        data.getRef().removeValue();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                            Log.e(TAG, "onCancelled", error.toException());
                                        }
                                    });
                                }
                            });
                    expenseTerminator.create().show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    //sets the value of expenses arraylist
    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView totalBudgetAmount, categoryName, expenseAdditionDate, expenseDesc;
        private ImageView deleteExpense;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            totalBudgetAmount = itemView.findViewById(R.id.totalBudgetAmount);
            categoryName = itemView.findViewById(R.id.categoryName);
            deleteExpense = itemView.findViewById(R.id.deleteExpense);
            expenseDesc = itemView.findViewById(R.id.expenseDesc);
            expenseAdditionDate = itemView.findViewById(R.id.expenseAdditionDate);
        }
    }
}
