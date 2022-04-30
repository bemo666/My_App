package com.ikea.myapp.utils;

import android.util.Log;

import com.ikea.myapp.models.CustomDateTime;
import com.ikea.myapp.models.MyTrip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    private CustomDateTime start, end;

    public getCorrectDate(MyTrip trip) {
        this.trip = trip;
        TimeZone tz = TimeZone.getTimeZone("GMT");

        tripStart = new Date(trip.getStartStamp());
        tripEnd = new Date(trip.getEndStamp());
        start = trip.getStart();
        end = trip.getEnd();
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

    public String getStartTime(){
//        dateFormat.setTimeZone(TimeZone.getTimeZone(trip.getTimeZone()));
        return dateFormat.format(tripStart);
    }
    public String getEndTime(){
//        dateFormat.setTimeZone(TimeZone.getTimeZone(trip.getTimeZone()));
        return dateFormat.format(tripEnd);
    }
}
