package com.ikea.myapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.volley.Header;
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
    private String startStamp, endStamp;
    private String destination, nickname;
    private Double destinationLat;
    private Double destinationLon;
    private Budget budget;
    private CustomCurrency currency;
    private List<Plan> plans;
    private String image;
    private String timeZone;
    private int imageVersion;

    public MyTrip() {
    }

    public MyTrip(String destination, LatLng destinationLatLng, String startStamp,
                  String endStamp, String placeId, @NonNull String id, CustomCurrency currency, String timeZone) {
        this.destination = destination;
        this.destinationLat = destinationLatLng.latitude;
        this.destinationLon = destinationLatLng.longitude;
        this.startStamp = startStamp;
        this.endStamp = endStamp;
        this.placeId = placeId;
        this.id = id;
        this.budget = new Budget();
        this.currency = currency;
        this.plans = new ArrayList<>();
        this.timeZone = timeZone;
        this.imageVersion = 0;
    }


    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getStartStamp() {
        return startStamp;
    }

    public void setStartStamp(String startStamp) {
        this.startStamp = startStamp;
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

    public boolean hasBudgetEntries() {
        return budget.getExpenses().size() != 0;
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

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean hasPlan(int type) {
        for (Plan p : plans) {
            if (p.getObjectType() == type)
                return true;
        }
        return false;
    }

    public void addPlan(Plan h) {
        if (this.plans == null)
            this.plans = new ArrayList<>();
        this.plans.add(h);
    }

    public void deletePlan(Plan h) {
        this.plans.remove(h);
    }

    public List<PlanHeader> sortList() {
        List<PlanHeader> headers = new ArrayList<>();
        for (PlanType t : PlanType.values()) {
            List<Plan> list = new ArrayList<>();
            if (plans != null)
                for (Plan p : plans)
                    if (p.getObjectType() == t.getType())
                        list.add(p);
            if (!list.isEmpty())
                headers.add(new PlanHeader(t, list));
        }
        return headers;
    }

    public int getImageVersion() {
        return imageVersion;
    }

    public void setImageVersion(int imageVersion) {
        this.imageVersion = imageVersion;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void editPlan(Plan plan, int position){
        this.plans.set(position, plan);
    }
}
