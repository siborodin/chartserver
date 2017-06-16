package com.amaiz.example.cs;

import com.amaiz.example.cs.aggregator.CandleAggregators;
import com.amaiz.example.quotes.api.Quote;
import com.amaiz.example.quotes.api.QuotesListener;

import java.util.Arrays;
import java.util.List;

public class QuotesProcessor implements QuotesListener {

    private int threadsNumber;
    private CandleAggregators aggregators;

    private QuotesProcessorWorker[] workers;

    public void start() {
        workers = new QuotesProcessorWorker[threadsNumber];
        for (int i = 0; i < threadsNumber; i++) {
            QuotesProcessorWorker worker = new QuotesProcessorWorker(aggregators);
            workers[i] = worker;
            worker.start();
        }
    }

    public void stop() {
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
