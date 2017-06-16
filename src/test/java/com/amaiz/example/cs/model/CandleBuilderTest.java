package com.amaiz.example.cs.model;

import com.amaiz.example.quotes.api.Quote;
import com.amaiz.example.utils.MathUtils;
import org.junit.Test;

import java.util.Calendar;

import static com.amaiz.example.TestUtils.updateCalendar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CandleBuilderTest {

    @Test
    public void testUpdateByQuote() {
        AggregationPeriod period = new AggregationPeriod(AggregationType.M, 1);
        String symbol = "EUR/USD";
        Calendar calendar = Calendar.getInstance();
        updateCalendar(calendar, 10, 10, 0);
        Quote quote = new Quote(symbol, 1.2, calendar.getTimeInMillis());

        CandleBuilder builder = new CandleBuilder(period, quote);

        // new high and close
        updateCalendar(calendar, 10, 11, 0);
        quote = new Quote(symbol, 1.22, calendar.getTimeInMillis());
        builder.update(quote);

        // new low and close
        updateCalendar(calendar, 10, 15, 0);
        quote = new Quote(symbol, 1.19, calendar.getTimeInMillis());
        builder.update(quote);

        // new high
        updateCalendar(calendar, 10, 17, 0);
        quote = new Quote(symbol, 1.25, calendar.getTimeInMillis());
        builder.update(quote);

        // new close
        updateCalendar(calendar, 10, 19, 0);
        quote = new Quote(symbol, 1.21, calendar.getTimeInMillis());
        builder.update(quote);

        //check
        updateCalendar(calendar, 10, 0, 0);
        Candle candle = builder.toCandle();
        assertEquals(symbol, candle.getSymbol());
        assertEquals(period, candle.getPeriod());
        assertEquals(calendar.getTimeInMillis(), candle.getTime());
        assertEquals(1.20, candle.getOpen(), MathUtils.EPS);
        assertEquals(1.21, candle.getClose(), MathUtils.EPS);
        assertEquals(1.25, candle.getHigh(), MathUtils.EPS);
        assertEquals(1.19, candle.getLow(), MathUtils.EPS);
    }

    @Test
    public void testUpdatedByCandle() {
        AggregationPeriod periodSmall = new AggregationPeriod(AggregationType.M, 1);
        AggregationPeriod periodBig = new AggregationPeriod(AggregationType.H, 1);

        String symbol = "EUR/USD";
        Calendar calendar = Calendar.getInstance();
        updateCalendar(calendar, 5, 0, 0, 0);

        CandleBuilder builder = new CandleBuilder(periodBig, symbol, calendar.getTimeInMillis());

        updateCalendar(calendar, 10, 0, 0);
        builder.update(new Candle(symbol, periodSmall, calendar.getTimeInMillis(),
            10, 15, 20, 5));

        updateCalendar(calendar, 30, 0, 0);
        builder.update(new Candle(symbol, periodSmall, calendar.getTimeInMillis(),
            9, 11, 10, 5));

        updateCalendar(calendar, 45, 0, 0);
        builder.update(new Candle(symbol, periodSmall, calendar.getTimeInMillis(),
            30, 20, 40, 25));

        updateCalendar(calendar, 58, 0, 0);
        builder.update(new Candle(symbol, periodSmall, calendar.getTimeInMillis(),
            10, 15, 20, 5));

        Candle candle = builder.toCandle();
        assertEquals(10, candle.getOpen(), MathUtils.EPS);
        assertEquals(15, candle.getClose(), MathUtils.EPS);
        assertEquals(40, candle.getHigh(), MathUtils.EPS);
        assertEquals(5, candle.getLow(), MathUtils.EPS);

        updateCalendar(calendar, 6, 1, 0, 0);
        try {
            builder.update(new Candle(symbol, periodSmall, calendar.getTimeInMillis(), 0, 0, 0, 0));
            assertTrue(false);
        } catch (Throwable t) {
            // correct
        }
    }

    @Test
    public void testReset() {
        AggregationPeriod period = new AggregationPeriod(AggregationType.M, 1);
        String symbol = "EUR/USD";
        Calendar calendar = Calendar.getInstance();
        updateCalendar(calendar, 10, 10, 0);
        Quote quote = new Quote(symbol, 1.2, calendar.getTimeInMillis());

        CandleBuilder builder = new CandleBuilder(period, quote);

        // new high and close
        updateCalendar(calendar, 10, 11, 0);
        quote = new Quote(symbol, 1.22, calendar.getTimeInMillis());
        builder.update(quote);

        // new quote
        updateCalendar(calendar, 11, 5, 0);
        quote = new Quote(symbol, 1.3, calendar.getTimeInMillis());

        builder.reset(quote);

        // check
        updateCalendar(calendar, 11, 0, 0);
        Candle candle = builder.toCandle();
        assertEquals(symbol, candle.getSymbol());
        assertEquals(period, candle.getPeriod());
        assertEquals(calendar.getTimeInMillis(), candle.getTime());
        assertEquals(1.3, candle.getOpen(), MathUtils.EPS);
        assertEquals(1.3, candle.getClose(), MathUtils.EPS);
        assertEquals(1.3, candle.getHigh(), MathUtils.EPS);
        assertEquals(1.3, candle.getLow(), MathUtils.EPS);

    }

    @Test
    public void testIsCompletedBy() {
        AggregationPeriod period = new AggregationPeriod(AggregationType.M, 1);
        String symbol = "EUR/USD";
        Calendar calendar = Calendar.getInstance();
        updateCalendar(calendar, 10, 12, 0);
        Quote quote = new Quote(symbol, 1.2, calendar.getTimeInMillis());

        CandleBuilder builder = new CandleBuilder(period, quote);

        // time before period
        updateCalendar(calendar, 9, 59, 800);
        assertEquals(false, builder.isCompletedBy(calendar.getTimeInMillis()));

        // time inside period
        updateCalendar(calendar, 10, 59, 800);
        assertEquals(false, builder.isCompletedBy(calendar.getTimeInMillis()));

        // time equal to the end of period
        updateCalendar(calendar, 11, 0, 0);
        assertEquals(true, builder.isCompletedBy(calendar.getTimeInMillis()));

        // time after the period
        updateCalendar(calendar, 11, 15, 0);
        assertEquals(true, builder.isCompletedBy(calendar.getTimeInMillis()));
    }
}
