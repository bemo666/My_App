package com.ikea.myapp;

import android.util.Log;

import com.ikea.myapp.ViewModels.ExpenseTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Budget implements Serializable {

    private Double budget, currentTally;
    private List<Expense> expenses;

    public Budget(Double budget) {
        this.budget = budget;
        this.currentTally = new Double(0);
        expenses = new ArrayList<>();
    }

    public Budget() {
        this.currentTally = new Double(0);
        expenses = new ArrayList<>();
        this.budget = 0.0;
    }

    public Budget(Double budget, List<Expense> expenses) {
        this.budget = budget;
        this.expenses = expenses;
        currentTally = 0.0;
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
        currentTally += expense.getPrice();
    }
    public void editExpense(Expense expense, int position) {
        currentTally -= expenses.get(position).getPrice();
        currentTally += expense.getPrice();
        expenses.set(position, expense);
    }
    public void deleteExpense(int position) {
        currentTally -= expenses.get(position).getPrice();
        expenses.remove(position);
    }

    public Double getCurrentTally() {
        return currentTally;
    }

    public void setCurrentTally(Double currentTally) {
        this.currentTally = currentTally;
    }
}
