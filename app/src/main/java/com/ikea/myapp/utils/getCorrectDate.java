package com.ikea.myapp.utils;

import com.ikea.myapp.models.MyTrip;

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

    private String startDate, endDate;
    private final Date tripStart, tripEnd;

    public getCorrectDate(MyTrip trip) {
        TimeZone tz = TimeZone.getTimeZone("GMT");

        tripStart = new Date(trip.getStartStamp());
        tripEnd = new Date(trip.getEndStamp());
        simpleFormatMonth.setTimeZone(tz);
        simpleFormatYear.setTimeZone(tz);
        simpleFormatMonthAndYear.setTimeZone(tz);
        simpleFormatJustMonth.setTimeZone(tz);
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
}
