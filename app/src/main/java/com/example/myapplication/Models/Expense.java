package com.example.myapplication.Models;

public class Expense {
    String expenseId, categoryName, expenseDescription , amount, expenseAdditionDate;
    public Expense(){}

    public Expense(String categoryName, String expenseDescription, String amount, String expenseAdditionDate) {
        this.categoryName = categoryName;
        this.expenseDescription = expenseDescription;
        this.expenseAdditionDate = expenseAdditionDate;
        this.amount = amount;
    }

    public String getExpenseAdditionDate() {
        return expenseAdditionDate;
    }

    public void setExpenseAdditionDate(String expenseAdditionDate) {
        this.expenseAdditionDate = expenseAdditionDate;
    }

    public String getExpenseDescription() {
        return expenseDescription;
    }

    public void setExpenseDescription(String expenseDescription) {
        this.expenseDescription = expenseDescription;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
