package com.ikea.myapp;

import com.google.android.gms.maps.model.LatLng;

public class MyTrip {
    String origin;
    String destination;
    String originLat;
    String originLon;
    String destinationLat;
    String destinationLon;

    public MyTrip() {
    }


    public MyTrip(String origin, String destination, LatLng originLatLng, LatLng destinationLatLng) {
        this.origin = origin;
        this.destination = destination;
        this.originLat = originLatLng.latitude + "";
        this.originLon = originLatLng.longitude + "";
        this.destinationLat = destinationLatLng.latitude + "";
        this.destinationLon = destinationLatLng.longitude + "";
    }

    public MyTrip(String destination, LatLng destinationLatLng) {
        this.destination = destination;
        this.destinationLat = destinationLatLng.latitude + "";
        this.destinationLon = destinationLatLng.longitude + "";
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getOriginLat() {
        return originLat;
    }

    public String getOriginLon() {
        return originLon;
    }

    public String getDestinationLat() {
        return destinationLat;
    }

    public String getDestinationLon() {
        return destinationLon;
    }

}
