package com.ikea.myapp.models;

public class Plan {

    private String name;
    private String airline, flightCode;
    private Long startLocationLat, startLocationLong, endLocationLat, endLocationLong;
    private String startLocation, endLocation, startLocationId, endLocationId, startLocationAddress, endLocationAddress;
    private Long startTime, endTime,  startDate, endDate;
    private String note, confirmationNumber;
    private Expense cost;
    private boolean hasEnd;

    public boolean isHasEnd() { return hasEnd; }

    public void setHasEnd(boolean hasEnd) { this.hasEnd = hasEnd; }

    private int objectType;

    public Plan(int objectType) {
        this.objectType = objectType;
    }

    public Long getStartDate() { return startDate; }

    public void setStartDate(Long startDate) { this.startDate = startDate; }

    public Long getEndDate() { return endDate; }

    public void setEndDate(Long endDate) { this.endDate = endDate; }

    public Plan() { }

    public boolean hasStartLocation(){ return startLocation != null; }

    public boolean hasEndLocation(){ return endLocation != null; }

    public Long getStartLocationLat() {
        return startLocationLat;
    }

    public void setStartLocationLat(Long startLocationLat) { this.startLocationLat = startLocationLat; }

    public Long getStartLocationLong() {
        return startLocationLong;
    }

    public void setStartLocationLong(Long startLocationLong) { this.startLocationLong = startLocationLong; }

    public Long getEndLocationLat() {
        return endLocationLat;
    }

    public void setEndLocationLat(Long endLocationLat) {
        this.endLocationLat = endLocationLat;
    }

    public Long getEndLocationLong() {
        return endLocationLong;
    }

    public void setEndLocationLong(Long endLocationLong) {
        this.endLocationLong = endLocationLong;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartLocationId() {
        return startLocationId;
    }

    public void setStartLocationId(String startLocationId) { this.startLocationId = startLocationId; }

    public String getEndLocationId() {
        return endLocationId;
    }

    public void setEndLocationId(String endLocationId) {
        this.endLocationId = endLocationId;
    }

    public String getStartLocationAddress() { return startLocationAddress; }

    public void setStartLocationAddress(String startLocationAddress) { this.startLocationAddress = startLocationAddress; }

    public String getEndLocationAddress() {
        return endLocationAddress;
    }

    public void setEndLocationAddress(String endLocationAddress) { this.endLocationAddress = endLocationAddress; }

    public Long getStartTime() { return startTime; }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
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

    public void setConfirmationNumber(String confirmationNumber) { this.confirmationNumber = confirmationNumber; }

    public Expense getCost() { return cost; }

    public void setCost(Expense cost) {
        this.cost = cost;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

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

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }
}
