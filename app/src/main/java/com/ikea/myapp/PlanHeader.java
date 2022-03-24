package com.ikea.myapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlanHeader implements Serializable {
    private String title;
    private List<PlanNote> notes;
    private int planType;
    public static final int NOTE = 0;
    public static final int HOTEL = 1;
    public static final int CAR = 2;
    public static final int FLIGHT = 3;
    public static final int PLACE = 4;

    public PlanHeader() {
    }

    public PlanHeader(int planType){
        setTitle(planType);
        this.planType = planType;
        this.notes = new ArrayList<>();
    }
    public PlanHeader(String title, int planType) {
        this.title = title;
        this.notes = new ArrayList<>();
        this.planType = planType;
    }

    public PlanHeader(String title, int planType, List<PlanNote> notes) {
        this.title = title;
        this.notes = notes;
        this.planType = planType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(int planType) {
        switch (planType){
            case NOTE:
                this.title = "Notes";
                break;
            case HOTEL:
                this.title = "Hotels and Lodging";
                break;
            case CAR:
                this.title = "Rental Cars";
                break;
            case FLIGHT:
                this.title = "Flights";
                break;
            case PLACE:
                this.title = "Itinerary";
                break;
        }
    }

    public List<PlanNote> getObjects() {
        return notes;
    }

    public void setNotes(List<PlanNote> notes) {
        this.notes = notes;
    }

    public void addNotes(PlanNote note) {
        if(this.notes == null)
            this.notes = new ArrayList<>();
        this.notes.add(note);
    }

    public void moveNote(PlanNote note, int newPos) {
        Object tmp = this.notes.remove(note);
        this.notes.add(newPos, note);
    }

    public int getPlanType() {
        return planType;
    }

    public void setPlanType(int planType) {
        this.planType = planType;
    }
}
