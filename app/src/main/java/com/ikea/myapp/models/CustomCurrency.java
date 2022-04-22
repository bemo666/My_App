package com.ikea.myapp.models;

import java.io.Serializable;
import java.util.Currency;

public class CustomCurrency {
    private String displayName;
    private String currencyCode;
    private String symbol;
    private int getNumericCode;
    private String string;

    public CustomCurrency(){ }

    public CustomCurrency(Currency currency){
        this.displayName = currency.getDisplayName();
        this.currencyCode = currency.getCurrencyCode();
        this.symbol = currency.getSymbol();
        this.getNumericCode = currency.getNumericCode();
        this.string = currency.toString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getGetNumericCode() {
        return getNumericCode;
    }

    public void setGetNumericCode(int getNumericCode) {
        this.getNumericCode = getNumericCode;
    }

    public String toString() {
        return string;
    }

    public CustomCurrency fromString(String currency){
        return new CustomCurrency(Currency.getInstance(currency));
    }
}
