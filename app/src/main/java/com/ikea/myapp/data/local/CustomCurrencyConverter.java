package com.ikea.myapp.data.local;

import androidx.room.TypeConverter;

import com.ikea.myapp.CustomCurrency;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomCurrencyConverter {

    @TypeConverter
    public String toString(CustomCurrency currency) {
        if (currency == null)
            return null;
        return currency.toString();
    }

    @TypeConverter
    public CustomCurrency toCustomCurrency(String currency) {
        if (currency == null) {
            return (null);
        }
        CustomCurrency c = new CustomCurrency();
        c = c.fromString(currency);
        return c;

    }
}
