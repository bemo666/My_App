package com.ikea.myapp;

import com.ikea.myapp.ViewModels.ExpenseTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Budget implements Serializable {

    private Double budget;
    private List<Expense> expenses;

    public Budget(Double budget) {
        this.budget = budget;
        expenses = new ArrayList<>();
    }

    public Budget() {
        expenses = new ArrayList<>();
    }

    public Budget(Double budget, List<Expense> expenses) {
        this.budget = budget;
        this.expenses = expenses;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }
}
