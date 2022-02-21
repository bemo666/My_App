package com.ikea.myapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "trips")
public class MyTrip implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;
    private String placeId;
    private String startDate, endDate, startStamp, endStamp;
    private String origin;
    private Double originLat = null;
    private Double originLon = null;
    private String destination;
    private Double destinationLat;
    private Double destinationLon;

    public MyTrip() {
    }


    public MyTrip(String origin, String destination, LatLng originLatLng, LatLng destinationLatLng,
                  String startDate, String startStamp, String endDate, String endStamp, String placeId,
                  String id) {
        this.origin = origin;
        this.destination = destination;
        this.originLat = originLatLng.latitude;
        this.originLon = originLatLng.longitude;
        this.destinationLat = destinationLatLng.latitude;
        this.destinationLon = destinationLatLng.longitude;
        this.startDate = startDate;
        this.endDate = endDate;
        this.placeId = placeId;
        this.startStamp = startStamp;
        this.endStamp = endStamp;
        this.id = id;
    }

    public MyTrip(String destination, LatLng destinationLatLng, String startDate, String startStamp, String endDate,
                  String endStamp, String placeId, String id) {
        this.destination = destination;
        this.destinationLat = destinationLatLng.latitude;
        this.destinationLon = destinationLatLng.longitude;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startStamp = startStamp;
        this.endStamp = endStamp;
        this.placeId = placeId;
        this.id = id;
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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(Double originLat) {
        this.originLat = originLat;
    }

    public Double getOriginLon() {
        return originLon;
    }

    public void setOriginLon(Double originLon) {
        this.originLon = originLon;
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
}
