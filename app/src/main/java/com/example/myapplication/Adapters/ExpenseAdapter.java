package com.example.myapplication.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fragments.IndividualTripFragment;
import com.example.myapplication.Models.Expense;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Expense> expenses = new ArrayList<>();

    public ExpenseAdapter(Context context) {
        this.context = context;
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
                    // TODO: 6/18/21 Delete expenses from firebase and from arrayList
                    Toast.makeText(context, "Yet to build", Toast.LENGTH_SHORT).show();
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
