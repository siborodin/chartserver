package com.amaiz.example.quotes.api;

public class Quote {

    private final String symbol;
    private final double price;
    private final long time;

    public Quote(String symbol, double price, long time) {
        this.symbol = symbol;
        this.price = price;
        this.time = time;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public long getTime() {
        return time;
    }
}
