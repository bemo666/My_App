package com.ikea.myapp.utils;

import com.ikea.myapp.models.MyTrip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class getCorrectDate {
    private final SimpleDateFormat simpleFormatMonth = new SimpleDateFormat("EEE MMM dd", Locale.ENGLISH);
    private final SimpleDateFormat simpleFormatYear = new SimpleDateFormat("EEE MMM dd, yyyy", Locale.ENGLISH);
    private final SimpleDateFormat simpleFormatMonthAndYear = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
    private final SimpleDateFormat simpleFormatJustMonth = new SimpleDateFormat("MMM", Locale.ENGLISH);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
    private MyTrip trip;
    private String startDate, endDate;
    private Date tripStart, tripEnd;

    public getCorrectDate(MyTrip trip) {
        this.trip = trip;
        tripStart = new Date(Long.parseLong(trip.getStartStamp()));
        tripEnd = new Date(Long.parseLong(trip.getEndStamp()));
        simpleFormatMonth.setTimeZone(TimeZone.getTimeZone(trip.getTimeZone()));
        simpleFormatYear.setTimeZone(TimeZone.getTimeZone(trip.getTimeZone()));
        simpleFormatMonthAndYear.setTimeZone(TimeZone.getTimeZone(trip.getTimeZone()));
        simpleFormatJustMonth.setTimeZone(TimeZone.getTimeZone(trip.getTimeZone()));
    }

    public String getStartDatelongFormat() {
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


    public String getEndDateLongFormat() {
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


    public String getDatesOnlyMonthAndYearFormat() {
        endDate = simpleFormatMonthAndYear.format(tripEnd);
        if (tripStart.getYear() == tripEnd.getYear()) {
            if (tripStart.getMonth() == tripEnd.getMonth()) {
                return endDate;
            } else{
                startDate = simpleFormatJustMonth.format(tripStart);
            }
        } else{
            startDate = simpleFormatMonthAndYear.format(tripStart);
        }
        return (startDate + " - " + endDate);
    }

    public String getStartTime(){
        dateFormat.setTimeZone(TimeZone.getTimeZone(trip.getTimeZone()));
        return dateFormat.format(tripStart);
    }
    public String getEndTime(){
        dateFormat.setTimeZone(TimeZone.getTimeZone(trip.getTimeZone()));
        return dateFormat.format(tripEnd);
    }
}
