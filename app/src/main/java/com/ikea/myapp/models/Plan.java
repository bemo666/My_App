package com.ikea.myapp.models;

import android.content.Intent;

import java.util.List;
import java.util.UUID;

public class Plan {

    private String id;
    private String airline, flightCode;
    private Integer startLocationRatingCount, endLocationRatingCount;
    private Double startLocationLat, startLocationLong, startLocationRating, endLocationLat, endLocationLong, endLocationRating;
    private String startLocation, startLocationId, startLocationAddress, startLocationPhoneNumber, startLocationUrl;
    private String endLocation, endLocationId, endLocationAddress, endLocationPhoneNumber, endLocationUrl;
    private Long startTime, endTime,  startDate, endDate;
    private String note, confirmationNumber;
    private Expense cost;
    private boolean hasEnd;
    private List<String> startEstablishmentTypes, endEstablishmentTypes, startLocationTimes, endLocationTimes;
    private int objectType;

    public Plan() { }

    public boolean isHasEnd() { return hasEnd; }

    public void setHasEnd(boolean hasEnd) { this.hasEnd = hasEnd; }


    public Plan(int objectType) {
        this.objectType = objectType;
        id = UUID.randomUUID().toString();
    }

    public Long getStartDate() { return startDate; }

    public void setStartDate(Long startDate) { this.startDate = startDate; }

    public Long getEndDate() { return endDate; }

    public void setEndDate(Long endDate) { this.endDate = endDate; }

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

    public Double getStartLocationRating() {
        return startLocationRating;
    }

    public void setStartLocationRating(Double startLocationRating) { this.startLocationRating = startLocationRating; }

    public String getStartLocationPhoneNumber() { return startLocationPhoneNumber; }

    public void setStartLocationPhoneNumber(String startLocationPhoneNumber) { this.startLocationPhoneNumber = startLocationPhoneNumber; }

    public String getStartLocationUrl() { return startLocationUrl; }

    public void setStartLocationUrl(String startLocationUrl) { this.startLocationUrl = startLocationUrl; }

    public Double getEndLocationRating() { return endLocationRating; }

    public void setEndLocationRating(Double endLocationRating) { this.endLocationRating = endLocationRating; }

    public String getEndLocationPhoneNumber() { return endLocationPhoneNumber; }

    public void setEndLocationPhoneNumber(String endLocationPhoneNumber) { this.endLocationPhoneNumber = endLocationPhoneNumber; }

    public String getEndLocationUrl() { return endLocationUrl; }

    public void setEndLocationUrl(String endLocationUrl) { this.endLocationUrl = endLocationUrl; }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
