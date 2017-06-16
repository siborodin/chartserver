package com.amaiz.example.cs;

import com.amaiz.example.cs.aggregator.CandleAggregators;
import com.amaiz.example.quotes.api.Quote;
import com.amaiz.example.quotes.api.QuotesListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class QuotesProcessor implements QuotesListener {

    private final Logger logger = LoggerFactory.getLogger(QuotesProcessor.class);

    private int threadsNumber;
    private CandleAggregators aggregators;

    private QuotesProcessorWorker[] workers;

    public void start() {
        logger.info("Starting");
        workers = new QuotesProcessorWorker[threadsNumber];
        logger.info("Creating and starting workers");
        for (int i = 0; i < threadsNumber; i++) {
            QuotesProcessorWorker worker = new QuotesProcessorWorker(aggregators);
            workers[i] = worker;
            worker.start();
        }
        logger.info("Started");
    }

    public void stop() {
        logger.info("Stopping");
        Arrays.stream(workers).forEach(QuotesProcessorWorker::stop);
    }


    @Override
    public void quotesReceived(List<Quote> quotes) {
        quotes.forEach(q -> workers[getWorkerIndex(q.getSymbol())].addQuote(q));
    }

    public void setThreadsNumber(int threadsNumber) {
        this.threadsNumber = threadsNumber;
    }

    public void setAggregators(CandleAggregators aggregators) {
        this.aggregators = aggregators;
    }

    private int getWorkerIndex(String symbol) {
        return Math.abs(symbol.hashCode()) % workers.length;
    }
}
