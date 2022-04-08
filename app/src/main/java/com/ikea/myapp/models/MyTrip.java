package com.ikea.myapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "trips")
public class MyTrip implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;
    private String placeId;
    private String startDate, endDate, startStamp, endStamp;
    private String destination;
    private Double destinationLat;
    private Double destinationLon;
    private Budget budget;
    private CustomCurrency currency;
    private List<PlanHeader> planHeaders;
    private String image;
    private String timeZone;

    public MyTrip() {
    }

    public MyTrip(String destination, LatLng destinationLatLng, String startDate, String startStamp, String endDate,
                  String endStamp, String placeId, String id, CustomCurrency currency, String timeZone) {
        this.destination = destination;
        this.destinationLat = destinationLatLng.latitude;
        this.destinationLon = destinationLatLng.longitude;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startStamp = startStamp;
        this.endStamp = endStamp;
        this.placeId = placeId;
        this.id = id;
        this.budget = new Budget();
        this.currency = currency;
        this.planHeaders = new ArrayList<>();
        this.timeZone = timeZone;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartStamp() {
        return startStamp;
    }

    public void setStartStamp(String startStamp) {
        this.startStamp = startStamp;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndStamp() {
        return endStamp;
    }

    public void setEndStamp(String endStamp) {
        this.endStamp = endStamp;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public Double getDestinationLon() {
        return destinationLon;
    }

    public void setDestinationLon(Double destinationLon) {
        this.destinationLon = destinationLon;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public CustomCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(CustomCurrency currency) {
        this.currency = currency;
    }

    public List<PlanHeader> getPlanHeaders() {
        return planHeaders;
    }

    public void setPlanHeaders(List<PlanHeader> planHeaders) {
        this.planHeaders = planHeaders;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeZone() { return timeZone; }

    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public boolean hasPlanHeaders() {
        if (this.getPlanHeaders() != null)
            return (this.planHeaders.size() != 0);
        return false;
    }

    public void addPlanHeader(PlanHeader h) {
        if (this.planHeaders == null)
            this.planHeaders = new ArrayList<>();
        this.planHeaders.add(h);
    }

}
