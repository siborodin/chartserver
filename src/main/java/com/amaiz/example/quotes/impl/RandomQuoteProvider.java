package com.amaiz.example.quotes.impl;

import com.amaiz.example.cs.QuotesProcessor;
import com.amaiz.example.quotes.api.Quote;
import com.amaiz.example.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;


public class RandomQuoteProvider {

    private final static Logger logger = LoggerFactory.getLogger(RandomQuoteProvider.class);

    private final static int BATCH_SIZE = 10; // number of quotes provided at a time

    private QuotesProcessor quotesProcessor;

    private int symbolsNumber;
    private int quotesPerSecond;
    private int durationSeconds;

    private ArrayList<String> symbols;
    private Map<String, Quote> latest = new HashMap<>();

    private final Random priceRandom = new Random();

    public void start() {
        logger.info("Preparing to generate quotes");
        symbols = new ArrayList<>(symbolsNumber);
        for (int i = 0; i < symbolsNumber; i++) {
            symbols.add(String.format("Symbol%d", i));
        }
        logger.info("Created {} symbols", symbolsNumber);

        int publicationsPerSecond = quotesPerSecond / BATCH_SIZE;
        int iterations = durationSeconds * publicationsPerSecond;
        long delay = 1000 / publicationsPerSecond;

        Random symbolRandom = new Random();

        logger.info("Going to publish {} quotes {} times per second for {} seconds",
            BATCH_SIZE, publicationsPerSecond, durationSeconds);

        Executor executor = ExecutorUtils.newLoggableSingleThreadExecutor("QuoteGen");
        executor.execute(() -> {
            for (int i = 0; i < iterations; i++) {

                List<Quote> quotes = new ArrayList<>();
                for (int j = 0; j < BATCH_SIZE; j++) {
                    String symbol = symbols.get(symbolRandom.nextInt(symbolsNumber));
                    quotes.add(nextQuote(symbol));
                }

                quotesProcessor.quotesReceived(quotes);
                //logger.info("Finished iteration {} of {}", i, iterations);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    logger.warn("Interrupted while sleeping");
                }
            }
            logger.info("Finished generating quotes");
        });

    }

    public void setQuotesProcessor(QuotesProcessor quotesProcessor) {
        this.quotesProcessor = quotesProcessor;
    }

    public void setSymbolsNumber(int symbolsNumber) {
        this.symbolsNumber = symbolsNumber;
    }

    public void setQuotesPerSecond(int quotesPerSecond) {
        this.quotesPerSecond = quotesPerSecond;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    private Quote nextQuote(String symbol) {
        Quote prev = latest.get(symbol);
        Quote next;
        if (prev == null) {
            double price = 100 * priceRandom.nextDouble();
            next = new Quote(symbol, price, System.currentTimeMillis());
        } else {
            double price = prev.getPrice() * (1 + (priceRandom.nextDouble() - 0.5) / 10);
            next = new Quote(symbol, price, System.currentTimeMillis());
        }
        latest.put(symbol, next);
        return next;
    }
}
