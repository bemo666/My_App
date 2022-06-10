package com.ikea.myapp.models;

import java.util.Currency;

public class CustomCurrency {
    private String symbol;
    private String string;

    public CustomCurrency(){ }

    public CustomCurrency(Currency currency){
        this.symbol = currency.getSymbol();
        this.string = currency.toString();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String toString() {
        return string;
    }

    public CustomCurrency fromString(String currency){
        return new CustomCurrency(Currency.getInstance(currency));
    }
}
