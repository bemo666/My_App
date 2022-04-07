package com.ikea.myapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlanHeader implements Serializable {
    private String myTitle;
    private List<Object> plans;
    private int planType;
    public static final int NOTE = 0;
    public static final int HOTEL = 1;
    public static final int RENTAL = 2;
    public static final int FLIGHT = 3;
    public static final int ACTIVITY = 4;

    public PlanHeader() {
    }

    public PlanHeader(int planType) {
        setTitle(planType);
        this.planType = planType;
        plans = new ArrayList<>();
    }


    public String getMyTitle() {
        return myTitle;
    }

    public void setMyTitle(String myTitle) {
        this.myTitle = myTitle;
    }

    public void setTitle(int planType) {
        switch (planType) {
            case NOTE:
                this.myTitle = "Notes";
                break;
            case HOTEL:
                this.myTitle = "Hotels and Lodging";
                break;
            case RENTAL:
                this.myTitle = "Rental Cars";
                break;
            case FLIGHT:
                this.myTitle = "Flights";
                break;
            case ACTIVITY:
                this.myTitle = "Itinerary";
                break;
        }
    }

    public List<Object> getObjects() { return plans; }

    public void setObjects(List<Object> plans) { this.plans = plans; }

    public void editObject(Object plan, int pos) {
        this.plans.set(pos, plan);
    }

    public void addObject(Object object) {
        if(this.plans == null)
            plans = new ArrayList<>();
        this.plans.add(object);
    }

    public int getObjectType() { return planType; }

    public void setObjectType(int planType) {
        this.planType = planType;
    }
}
