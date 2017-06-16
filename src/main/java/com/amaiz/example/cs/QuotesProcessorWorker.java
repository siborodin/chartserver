package com.amaiz.example.cs;

import com.amaiz.example.cs.aggregator.CandleAggregator;
import com.amaiz.example.cs.aggregator.CandleAggregators;
import com.amaiz.example.cs.model.AggregationPeriod;
import com.amaiz.example.cs.model.AggregationType;
import com.amaiz.example.quotes.api.Quote;
import com.amaiz.example.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class QuotesProcessorWorker {

    private final static Logger logger = LoggerFactory.getLogger(QuotesProcessorWorker.class);

    private final static int QUEUE_SIZE = 5000; // subject to be changed based on profiling results

    private final static AtomicInteger NUMBER = new AtomicInteger(0);
    private final static AggregationPeriod period = new AggregationPeriod(AggregationType.M, 1);

    private final AtomicBoolean stopped = new AtomicBoolean(false);

    private final BlockingQueue<Quote> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private Map<String, CandleAggregator> cachedAggregators = new HashMap<>();
    private long lastStaleCheck = -1;

    private final Executor executor = ExecutorUtils.newLoggableSingleThreadExecutor(
        String.format("QuoteProcessorWorker-%d", NUMBER.incrementAndGet()));

    private final CandleAggregators aggregators;

    public QuotesProcessorWorker(CandleAggregators aggregators) {
        this.aggregators = aggregators;
    }

    public void start() {
        logger.info("Starting worker");
        executor.execute(this::run);
    }

    public void stop() {
        logger.info("Stopping worker");
        stopped.set(true);
    }

    public void addQuote(Quote quote) {
        queue.add(quote);
    }

    private void run() {
        while(!stopped.get()) {
            long time = System.currentTimeMillis();
            long nextStaleCheck = lastStaleCheck + period.getLengthMS();
            if(time > nextStaleCheck) {
                cachedAggregators.forEach((s, a) -> a.checkStaleCandle(time));
                lastStaleCheck = time;
            }

            try {
                Quote quote = queue.poll(nextStaleCheck - time, TimeUnit.MILLISECONDS);
                if(quote != null) {
                    String symbol = quote.getSymbol();
                    CandleAggregator aggregator = getAggregator(symbol);
                    aggregator.update(quote);
                }
            } catch(InterruptedException e) {
                logger.warn("Interrupted while waiting for quote", e);
            }
        }
    }

    private CandleAggregator getAggregator(String symbol) {
        if(!cachedAggregators.containsKey(symbol)) {
            cachedAggregators.put(symbol, aggregators.getAggregator(symbol));
        }
        return cachedAggregators.get(symbol);
    }

}
