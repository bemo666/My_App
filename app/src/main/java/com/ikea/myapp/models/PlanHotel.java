package com.ikea.myapp.models;

import java.io.Serializable;

public class PlanHotel extends Plan implements Serializable {
    private String location, checkInDate, note, checkOutDate, confirmationNumber;
    private Expense cost;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public Expense getCost() {
        return cost;
    }

    public void setCost(Expense cost) {
        this.cost = cost;
    }
}
