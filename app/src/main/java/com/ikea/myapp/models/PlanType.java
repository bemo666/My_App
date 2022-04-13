package com.ikea.myapp.models;

import androidx.annotation.NonNull;

import com.ikea.myapp.R;

public enum PlanType {

    Note(0, "Notes", R.string.ui_add_a_note, R.layout.layout_plan_note),
    Hotel(1, "Hotels and Lodging", R.string.ui_add_a_hotel, R.layout.layout_plan_hotel),
    Rental(2, "Rentals", R.string.ui_add_a_rental_car, R.layout.layout_plan_rental),
    Flight(3, "Flights", R.string.ui_add_a_flight, R.layout.layout_plan_flight),
    Activity(4, "Activities", R.string.ui_add_an_activity, R.layout.layout_plan_activity);

    private int type, note, layout;
    private String name;

    PlanType(int type, String name, int note, int layout) {
        this.type = type;
        this.name = name;
        this.note = note;
        this.layout = layout;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static PlanType getTypeByInt(int type) {
        for (PlanType p : PlanType.values())
            if (p.getType() == type)
                return p;
        return null;
    }

    public int getNote() {
        return note;
    }

    public int getLayout() {
        return layout;
    }
}
