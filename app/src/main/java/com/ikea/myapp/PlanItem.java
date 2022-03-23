package com.ikea.myapp;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
public class PlanItem implements Serializable {
    private String startDate, endDate;
    private String note;
    private Location location;
    private Object object;
    private PlanType type;

    public PlanItem() {
    }

    public PlanItem(String startDate, String endDate, String note, Location location) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.note = note;
        this.location = location;
    }
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
