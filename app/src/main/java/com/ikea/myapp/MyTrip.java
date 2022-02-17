package com.ikea.myapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class MyTrip {
    private String id;
    private String placeId;
    private Date startDate, endDate;
    private String origin;
    private Double originLat = null;
    private Double originLon = null;
    private String destination;
    private Double destinationLat;
    private Double destinationLon;

    public MyTrip() {
    }


    public MyTrip(String origin, String destination, LatLng originLatLng, LatLng destinationLatLng,
                  Date startDate, Date endDate, String placeId, String id) {
        this.origin = origin;
        this.destination = destination;
        this.originLat = originLatLng.latitude;
        this.originLon = originLatLng.longitude;
        this.destinationLat = destinationLatLng.latitude;
        this.destinationLon = destinationLatLng.longitude;
        this.startDate = startDate;
        this.endDate = endDate;
        this.placeId = placeId;
        this.id = id;
    }

    public MyTrip(String destination, LatLng destinationLatLng, Date startDate, Date endDate,
                  String placeId, String id) {
        this.destination = destination;
        this.destinationLat = destinationLatLng.latitude;
        this.destinationLon = destinationLatLng.longitude;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
