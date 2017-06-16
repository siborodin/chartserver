package com.amaiz.example.cs.provider;

import com.amaiz.example.cs.aggregator.CandleAggregators;
import com.amaiz.example.cs.model.AggregationPeriod;
import com.amaiz.example.cs.model.AggregationType;
import com.amaiz.example.cs.model.Candle;
import com.amaiz.example.cs.model.CandleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChartsProvider {

    private final static Logger logger= LoggerFactory.getLogger(ChartsProvider.class);

    private CandleAggregators aggregators;

    public List<Candle> getCandles(String symbol, long from, long to, AggregationPeriod period) {
        logger.info("Processing request for {} from {] to {} aggregated by {}", symbol, from, to, period);
        Candle[] candles = aggregators.getAggregator(symbol).getCompletedCandles();
        logger.debug("Got {] M1 candles from Aggregator", candles.length);
        List<Candle> list = Arrays.stream(candles)
            .filter(c -> c.getTime() >= from && c.getTime() <= to)
            .collect(Collectors.toList());
        logger.debug("Remained {} M1 candles after filtering by time period", list.size());
        if(!list.isEmpty()
            && (period.getAggregationType() != AggregationType.M || period.getNumber() != 1))
        {
            list = convertToOtherPeriod(list, period);
        }
        logger.info("Returning {} candles", list.size());
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
