package com.amaiz.example.cs.model;

public enum AggregationType {
    M(60 * 1000),
    H(60 * M.getLengthMS()),
    D(24 * H.getLengthMS());

    private long lengthMS;

    private AggregationType(long lengthMS) {
        this.lengthMS = lengthMS;
    }

    public long getLengthMS() {
        return lengthMS;
    }
}
