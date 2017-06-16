package com.amaiz.example.cs.model;

import com.amaiz.example.quotes.api.Quote;
import com.amaiz.example.utils.MathUtils;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.fastinfoset.algorithm.DoubleEncodingAlgorithm;

public class CandleBuilder {

    private final String symbol;
    private final AggregationPeriod period;

    private long time;

    private double open = Double.NaN;
    private double close = Double.NaN;
    private double high = Double.NaN;
    private double low = Double.NaN;

    public CandleBuilder(AggregationPeriod period, Quote quote) {
        this.symbol = quote.getSymbol();
        this.period = period;
        reset(quote);
    }

    public CandleBuilder(AggregationPeriod period, String symbol, long time) {
        this.symbol = symbol;
        this.period = period;
        this.time = time;
    }

    public boolean isCompletedBy(long ts) {
        return ts >= period.getNextStartTime(time);
    }

    public void update(Quote quote) {
        if(!symbol.equals(quote.getSymbol())) {
            throw new IllegalArgumentException("Quote is for another symbol");
        }
        if(isCompletedBy(quote.getTime())) {
            throw new IllegalArgumentException("Quote is for next candle");
        }

        double newPrice = quote.getPrice();
        if(Double.isNaN(newPrice)) {
            return;
        }

        if(Double.isNaN(open)) {
            open = newPrice;
        }
        close = newPrice;
        if(MathUtils.compare(low, newPrice) > 0) {
            low = newPrice;
        }
        if(MathUtils.compare(high, newPrice) < 0) {
            high = newPrice;
        }
    }

    public void update(Candle candle) {
        if(period.getLengthMS() <= candle.getPeriod().getLengthMS()) {
            throw new IllegalArgumentException("Inappropriate period in candle");
        }
        if(isCompletedBy(candle.getTime() + candle.getPeriod().getLengthMS() - 1)) {
            throw new IllegalArgumentException("Candle from next period");
        }
        if(Double.isNaN(open)) {
            open = candle.getOpen();
        }
        if(Double.isNaN(high) || MathUtils.compare(candle.getHigh(), high) > 0) {
            high = candle.getHigh();
        }
        if(Double.isNaN(low) || MathUtils.compare(candle.getLow(), low) < 0) {
            low = candle.getLow();
        }
        close = candle.getClose();
    }

    public void reset(Candle candle) {
        if(!symbol.equals(candle.getSymbol())) {
            throw new IllegalArgumentException("Candle is for another symbol");
        }
        time = period.getStartTime(candle.getTime());
        open = candle.getOpen();
        close = candle.getClose();
        high = candle.getHigh();
        low = candle.getLow();
    }


    public void reset(Quote quote) {
        if(!symbol.equals(quote.getSymbol())) {
            throw new IllegalArgumentException("Quote is for another symbol");
        }
        time = period.getStartTime(quote.getTime());
        open = close = low = high = quote.getPrice();
    }

    public Candle toCandle() {
        return new Candle(symbol, period, time, open, close, high, low);
    }

}
