package com.ikea.myapp.models;

import android.content.Intent;

import java.util.List;

public class Plan {

    private String name;
    private String airline, flightCode;
    private Integer startLocationRatingCount, endLocationRatingCount;
    private Double startLocationLat, startLocationLong, startLocationRating, endLocationLat, endLocationLong, endLocationRating;
    private String startLocation, startLocationId, startLocationAddress, startLocationStatus, startLocationPhoneNumber, startLocationTypes, startLocationUrl, startLocationPriceLevel;
    private String endLocation, endLocationId, endLocationAddress, endLocationStatus, endLocationPhoneNumber, endLocationTypes, endLocationUrl, endLocationPriceLevel;
    private Long startTime, endTime,  startDate, endDate;
    private String note, confirmationNumber;
    private Expense cost;
    private boolean hasEnd;
    private List<String> startEstablishmentTypes, endEstablishmentTypes, startLocationTimes, endLocationTimes;

    public Plan() { }

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

    public boolean hasStartLocation(){ return startLocation != null; }

    public boolean hasEndLocation(){ return endLocation != null; }

    public Double getStartLocationLat() {
        return startLocationLat;
    }

    public void setStartLocationLat(Double startLocationLat) { this.startLocationLat = startLocationLat; }

    public Double getStartLocationLong() {
        return startLocationLong;
    }

    public void setStartLocationLong(Double startLocationLong) { this.startLocationLong = startLocationLong; }

    public Double getEndLocationLat() {
        return endLocationLat;
    }

    public void setEndLocationLat(Double endLocationLat) {
        this.endLocationLat = endLocationLat;
    }

    public Double getEndLocationLong() {
        return endLocationLong;
    }

    public void setEndLocationLong(Double endLocationLong) { this.endLocationLong = endLocationLong; }

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

    public String getStartLocationId() { return startLocationId; }

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

    public void onResult(int requestCode, int resultCode, Intent data) { }

    public Double getStartLocationRating() {
        return startLocationRating;
    }

    public void setStartLocationRating(Double startLocationRating) { this.startLocationRating = startLocationRating; }

    public String getStartLocationStatus() {
        return startLocationStatus;
    }

    public void setStartLocationStatus(String startLocationStatus) { this.startLocationStatus = startLocationStatus; }

    public String getStartLocationPhoneNumber() { return startLocationPhoneNumber; }

    public void setStartLocationPhoneNumber(String startLocationPhoneNumber) { this.startLocationPhoneNumber = startLocationPhoneNumber; }

    public String getStartLocationTypes() { return startLocationTypes; }

    public void setStartLocationTypes(String startLocationTypes) { this.startLocationTypes = startLocationTypes; }

    public String getStartLocationUrl() { return startLocationUrl; }

    public void setStartLocationUrl(String startLocationUrl) { this.startLocationUrl = startLocationUrl; }

    public String getStartLocationPriceLevel() { return startLocationPriceLevel; }

    public void setStartLocationPriceLevel(String startLocationPriceLevel) { this.startLocationPriceLevel = startLocationPriceLevel; }

    public Double getEndLocationRating() { return endLocationRating; }

    public void setEndLocationRating(Double endLocationRating) { this.endLocationRating = endLocationRating; }

    public String getEndLocationStatus() { return endLocationStatus; }

    public void setEndLocationStatus(String endLocationStatus) { this.endLocationStatus = endLocationStatus; }

    public String getEndLocationPhoneNumber() { return endLocationPhoneNumber; }

    public void setEndLocationPhoneNumber(String endLocationPhoneNumber) { this.endLocationPhoneNumber = endLocationPhoneNumber; }

    public String getEndLocationTypes() { return endLocationTypes; }

    public void setEndLocationTypes(String endLocationTypes) { this.endLocationTypes = endLocationTypes; }

    public String getEndLocationUrl() { return endLocationUrl; }

    public void setEndLocationUrl(String endLocationUrl) { this.endLocationUrl = endLocationUrl; }

    public String getEndLocationPriceLevel() { return endLocationPriceLevel; }

    public void setEndLocationPriceLevel(String endLocationPriceLevel) { this.endLocationPriceLevel = endLocationPriceLevel; }

    public List<String> getStartEstablishmentTypes() { return startEstablishmentTypes; }

    public void setStartEstablishmentTypes(List<String> establishmentTypes) { this.startEstablishmentTypes = establishmentTypes; }

    public List<String> getEndEstablishmentTypes() { return endEstablishmentTypes; }

    public void setEndEstablishmentTypes(List<String> establishmentTypes) { this.endEstablishmentTypes = establishmentTypes; }

    public Integer getEndLocationRatingCount() { return endLocationRatingCount; }

    public void setEndLocationRatingCount(Integer endLocationRatingCount) { this.endLocationRatingCount = endLocationRatingCount; }

    public Integer getStartLocationRatingCount() { return startLocationRatingCount; }

    public void setStartLocationRatingCount(Integer startLocationRatingCount) { this.startLocationRatingCount = startLocationRatingCount; }

    public List<String> getStartLocationTimes() { return startLocationTimes; }

    public void setStartLocationTimes(List<String> startLocationTimes) { this.startLocationTimes = startLocationTimes; }

    public List<String> getEndLocationTimes() { return endLocationTimes; }

    public void setEndLocationTimes(List<String> endLocationTimes) { this.endLocationTimes = endLocationTimes; }
}
