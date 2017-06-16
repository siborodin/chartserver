package com.amaiz.example.cs.provider;

import com.amaiz.example.cs.aggregator.CandleAggregators;
import com.amaiz.example.cs.model.AggregationPeriod;
import com.amaiz.example.cs.model.AggregationType;
import com.amaiz.example.cs.model.Candle;
import com.amaiz.example.cs.model.CandleBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChartsProvider {

    private CandleAggregators aggregators;

    public List<Candle> getCandles(String symbol, long from, long to, AggregationPeriod period) {
        Candle[] candles = aggregators.getAggregator(symbol).getCompletedCandles();
        List<Candle> list = Arrays.stream(candles)
            .filter(c -> c.getTime() >= from && c.getTime() <= to)
            .collect(Collectors.toList());
        if(!list.isEmpty()
            && (period.getAggregationType() != AggregationType.M || period.getNumber() != 1))
        {
            return convertToOtherPeriod(list, period);
        }
        return list;
    }

    public void setAggregators(CandleAggregators aggregators) {
        this.aggregators = aggregators;
    }

    List<Candle> convertToOtherPeriod(List<Candle> candles, AggregationPeriod period) {
        List<Candle> result = new ArrayList<>();
        Candle firstCandle = candles.get(0);
        CandleBuilder builder = new CandleBuilder(period, firstCandle.getSymbol(), period.getStartTime(firstCandle.getTime()));
        for(Candle candle : candles) {
            if(!builder.isCompletedBy(candle.getTime() + candle.getPeriod().getLengthMS() - 1)) {
                builder.update(candle);
            } else {
                result.add(builder.toCandle());
                builder.reset(candle);
            }
        }
        result.add(builder.toCandle());
        return result;
    }
}
