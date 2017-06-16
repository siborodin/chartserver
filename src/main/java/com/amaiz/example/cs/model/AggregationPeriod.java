package com.amaiz.example.cs.model;

public class AggregationPeriod {
    private final AggregationType type;
    private final int number;

    public AggregationPeriod(AggregationType type, int number) {
        this.type = type;
        this.number = number;
    }

    public long getLengthMS() {
        return type.getLengthMS() * number;
    }

    public AggregationType getAggregationType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public long getStartTime(long forTime) {
        return forTime - forTime % getLengthMS();
    }

    public long getNextStartTime(long forTime) {
        return getStartTime(forTime) + type.getLengthMS() * number;
    }

    @Override
    public String toString() {
        return "AggregationPeriod{" +
            "type=" + type +
            ", number=" + number +
            '}';
    }
}
