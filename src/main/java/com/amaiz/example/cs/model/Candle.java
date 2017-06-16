package com.amaiz.example.cs.model;

public class Candle {

    private final String symbol;
    private final AggregationPeriod period;
    private final long time;

    private final double open;
    private final double close;
    private final double high;
    private final double low;

    public Candle(String symbol, AggregationPeriod period,
        long time, double open, double close, double high, double low)
    {
        this.symbol = symbol;
        this.period = period;
        this.time = time;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
    }

    public String getSymbol() {
        return symbol;
    }

    public AggregationPeriod getPeriod() {
        return period;
    }

    public long getTime() {
        return time;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    @Override
    public String toString() {
        return "Candle{" +
            "symbol='" + symbol + '\'' +
            ", period=" + period +
            ", time=" + time +
            ", open=" + open +
            ", close=" + close +
            ", high=" + high +
            ", low=" + low +
            '}';
    }
}
