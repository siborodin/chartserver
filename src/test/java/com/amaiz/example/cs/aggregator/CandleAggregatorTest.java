package com.amaiz.example.cs.aggregator;

import com.amaiz.example.cs.model.AggregationPeriod;
import com.amaiz.example.cs.model.AggregationType;
import com.amaiz.example.cs.model.Candle;
import com.amaiz.example.quotes.api.Quote;
import com.amaiz.example.utils.MathUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;

import static com.amaiz.example.TestUtils.updateCalendar;
import static org.junit.Assert.assertEquals;

// this test is intended to test functionality in single thread
public class CandleAggregatorTest {

    @Test
    public void testAggregation() {

        AggregationPeriod period = new AggregationPeriod(AggregationType.M, 1);
        CandleAggregator aggregator = new CandleAggregator(period, 8);

        String symbol = "EUR/USD";
        Calendar calendar = Calendar.getInstance();

        updateCalendar(calendar, 10, 0, 0);
        Quote quote = new Quote(symbol, 1.2, calendar.getTimeInMillis());aggregator.update(quote);

        for(int i = 0; i < 10; i++) {
            for(int j = 0; j <= 10; j++) {
                updateCalendar(calendar, 10 + i, 5 * j, 0);
                double newPrice = 1.2 + i * 0.1 + j * 0.01;
                quote = new Quote(symbol, newPrice, calendar.getTimeInMillis());
                aggregator.update(quote);
                //check
                Candle[] candles = aggregator.getCompletedCandles();
                Arrays.sort(candles, (c1, c2) -> MathUtils.compare(c1.getTime(), c2.getTime()));
                assertEquals(Math.min(i, 8), candles.length);
                for(int ci = 0; ci < candles.length; ci++) {
                    Candle candle = candles[ci];
                    int iter = i <= 8 ? ci : ci + (i - 8);
                    updateCalendar(calendar, 10 + iter, 0, 0);
                    assertEquals(calendar.getTimeInMillis(), candle.getTime());
                    assertEquals(1.2 + iter * 0.1, candle.getOpen(), MathUtils.EPS);
                    assertEquals(1.2 + iter * 0.1, candle.getLow(), MathUtils.EPS);
                    assertEquals(1.2 + iter * 0.1 + 0.1, candle.getClose(), MathUtils.EPS);
                    assertEquals(1.2 + iter * 0.1 + 0.1, candle.getHigh(), MathUtils.EPS);
                }

            }
        }
    }
}
