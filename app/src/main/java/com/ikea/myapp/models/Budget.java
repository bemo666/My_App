package com.ikea.myapp.models;

import java.util.ArrayList;
import java.util.List;


public class Budget {

    private Double budget, currentTally;
    private List<Expense> expenses;

    public Budget(Double budget) {
        this.budget = budget;
        this.currentTally = (double) 0;
        expenses = new ArrayList<>();
    }

    public Budget() {
        this.currentTally = (double) 0;
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
        fixTally();
    }

    public void editExpense(Expense expense, int position) {
        expenses.set(position, expense);
        fixTally();
    }
    public void deleteExpense(int position) {
        expenses.remove(position);
        fixTally();
    }

    public void deleteExpense(Expense e) {
        expenses.remove(e);
        fixTally();
    }
    private void fixTally() {
        currentTally = 0.0;
        for (Expense e0: this.getExpenses()) { currentTally += e0.getPrice(); }
    }


    public Double getCurrentTally() {
        return currentTally;
    }

    public void setCurrentTally(Double currentTally) {
        this.currentTally = currentTally;
    }
}
