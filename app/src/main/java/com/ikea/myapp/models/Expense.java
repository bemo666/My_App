package com.ikea.myapp.models;

import com.ikea.myapp.ViewModels.ExpenseTypes;

import java.io.Serializable;

public class Expense implements Serializable {

    private String description;
    private ExpenseTypes type;
    private Double price;

    public Expense(String description, ExpenseTypes type, Double price) {
        this.description = description;
        this.type = type;
        this.price = price;
    }
    public Expense(ExpenseTypes type, Double price) {
        this.type = type;
        this.price = price;
    }

    public Expense() { }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpenseTypes getType() {
        return type;
    }

    public void setType(ExpenseTypes type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
