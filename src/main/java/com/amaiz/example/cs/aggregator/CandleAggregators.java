package com.amaiz.example.cs.aggregator;

import com.amaiz.example.cs.model.AggregationPeriod;
import com.amaiz.example.cs.model.AggregationType;

import java.util.concurrent.ConcurrentHashMap;

public class CandleAggregators {

    private ConcurrentHashMap<String, CandleAggregator> aggregators = new ConcurrentHashMap<>();

    public CandleAggregator getAggregator(String symbol) {
        CandleAggregator aggregator = aggregators.get(symbol);
        if(aggregator == null) {
            aggregators.putIfAbsent(symbol, new CandleAggregator(new AggregationPeriod(AggregationType.M, 1), 2000));
            aggregator = aggregators.get(symbol);
        }
        return aggregator;
    }

}
