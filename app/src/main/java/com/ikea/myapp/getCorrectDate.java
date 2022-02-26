package com.ikea.myapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class getCorrectDate{
    private SimpleDateFormat simpleFormatMonth = new SimpleDateFormat("EEE MMM dd", Locale.ENGLISH);
    private SimpleDateFormat simpleFormatYear = new SimpleDateFormat("EEE MMM dd, yyyy", Locale.ENGLISH);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
    private MyTrip trip;
    private String startDate, endDate;
    private Date tripStart, tripEnd;

    public getCorrectDate(MyTrip trip) {
        this.trip = trip;
        try {
            tripStart = dateFormat.parse(trip.getStartDate());
            tripEnd = dateFormat.parse(trip.getEndDate());
        } catch (
                ParseException e) {
            tripEnd = null;
            tripStart = null;
        }
    }

    public String getStartDate() {
        if (tripStart.getYear() == Calendar.getInstance().get(Calendar.YEAR) - 1900) {
            startDate = simpleFormatMonth.format(tripStart);
            if (tripEnd.getYear() > tripStart.getYear()) {
                startDate = simpleFormatYear.format(tripStart);
            }
        } else {
            startDate = simpleFormatYear.format(tripStart);
            if (tripEnd.getYear() == tripStart.getYear()) {
                startDate = simpleFormatMonth.format(tripStart);
            }
        }
        return startDate;
    }

    public String getEndDate() {
        if (tripStart.getYear() == Calendar.getInstance().get(Calendar.YEAR) - 1900) {
            endDate = simpleFormatMonth.format(tripEnd);
            if (tripEnd.getYear() > tripStart.getYear()) {
                endDate = simpleFormatYear.format(tripEnd);
            }
        } else {
            endDate = simpleFormatYear.format(tripEnd);
        }
        return endDate;
    }
}
