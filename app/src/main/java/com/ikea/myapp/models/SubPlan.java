package com.ikea.myapp.models;

public class SubPlan {

    private String name;
    private String airline, flightCode;
    private Double latitude, longitude;
    private String location, locationId, locationAddress;
    private Long time, date, endtime;
    private String note, confirmationNumber;
    private boolean start;
    private PlanType type;

    public SubPlan(String name, String airline, String flightCode, Double latitude, Double longitude, String location, String locationId, String locationAddress, Long time, Long date, Long endtime, String note, String confirmationNumber, boolean start, PlanType type) {
        this.name = name;
        this.airline = airline;
        this.flightCode = flightCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.locationId = locationId;
        this.locationAddress = locationAddress;
        this.time = time;
        this.date = date;
        this.endtime = endtime;
        this.note = note;
        this.confirmationNumber = confirmationNumber;
        this.start = start;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public Long getEndtime() {
        return endtime;
    }

    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }

    public PlanType getType() {
        return type;
    }

    public void setType(PlanType type) {
        this.type = type;
    }
}
