package com.amaiz.example.cs.aggregator;

import com.amaiz.example.cs.model.AggregationPeriod;
import com.amaiz.example.cs.model.Candle;
import com.amaiz.example.cs.model.CandleBuilder;
import com.amaiz.example.quotes.api.Quote;
import com.amaiz.example.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Synchronization policy:
 * 1. Each class object is supposed to be user by one "writer" and multiple "reader" threads
 * 2. Only "writer" thread can invoke {@link #update(Quote)} and {@link #checkStaleCandle(long)} methods
 * 3. "Reader" threads can invoke only {@link #getCompletedCandles()} method
 * 4. Synchronization of {@link #candles } between writer and reader threads is performed via {@link #rwLock}
 *
 */
public class CandleAggregator {

    private final static Logger logger = LoggerFactory.getLogger(CandleAggregator.class);

    private final AggregationPeriod period;

    private final Candle[] candles;
    private int start;
    private int length;

    private CandleBuilder current;
    private long lastUpdateTS = -1;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    CandleAggregator(AggregationPeriod period, int length) {
        this.period = period;
        this.candles = new Candle[length];
        this.start = 0;
        this.length = 0;
    }

    public Candle[] getCompletedCandles() {
        rwLock.readLock().lock();
        try {
            Candle[] candles = Arrays.copyOf(this.candles, length);
            Arrays.sort(candles, (c1, c2) -> MathUtils.compare(c1.getTime(), c2.getTime()));
            return candles;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void update(Quote quote) {
        if(current == null) {
            current = new CandleBuilder(period, quote);
        } else {
            if(current.isCompletedBy(quote.getTime())) {
                storeCandle();
                current.reset(quote);
            } else {
                current.update(quote);
            }
        }
        lastUpdateTS = quote.getTime();
    }

    public void checkStaleCandle(long time) {
        if(current != null
            && time > period.getNextStartTime(lastUpdateTS))
        {
            storeCandle();
            current = null;
        }
    }

    private void storeCandle() {
        rwLock.writeLock().lock();
        try {
            int nextIndex = (start + length) % candles.length;
            candles[nextIndex] = current.toCandle();
            start = nextIndex >= start ? start : start + 1;
            length = Math.min(length + 1, candles.length);
            logger.debug("Stored Candle {}", candles[nextIndex]);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

}
