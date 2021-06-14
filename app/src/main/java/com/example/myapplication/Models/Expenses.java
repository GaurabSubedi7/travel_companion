package com.example.myapplication.Models;

public class Expenses {
    String categoryName , amount;
    public Expenses(){}

    public Expenses(String categoryName, String amount) {
        this.categoryName = categoryName;
        this.amount = amount;
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
