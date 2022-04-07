package com.ikea.myapp.data.local;

import androidx.room.TypeConverter;

import com.ikea.myapp.models.CustomCurrency;

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
