package com.amaiz.example.cs.provider;

import com.amaiz.example.cs.model.AggregationPeriod;
import com.amaiz.example.cs.model.AggregationType;
import com.amaiz.example.cs.model.Candle;
import com.amaiz.example.utils.MathUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.amaiz.example.TestUtils.updateCalendar;
import static org.junit.Assert.assertEquals;

public class ChartsProviderTest {

    @Test
    public void testConvertToOtherPeriod() {
        ChartsProvider provider = new ChartsProvider();

        AggregationPeriod periodSmall = new AggregationPeriod(AggregationType.M, 1);
        AggregationPeriod periodBig = new AggregationPeriod(AggregationType.M, 5);

        String symbol = "EUR/USD";
        Calendar calendar = Calendar.getInstance();
        updateCalendar(calendar, 5, 0, 0, 0);

        List<Candle> candles = new ArrayList<>();

        // first big candle
        updateCalendar(calendar, 5, 1, 0, 0);
        candles.add(new Candle(symbol, periodSmall, calendar.getTimeInMillis(), 10, 15, 20, 5));

        updateCalendar(calendar, 5, 3, 0, 0);
        candles.add(new Candle(symbol, periodSmall, calendar.getTimeInMillis(), 11, 16, 21, 6));

        updateCalendar(calendar, 5, 4, 0, 0);
        candles.add(new Candle(symbol, periodSmall, calendar.getTimeInMillis(), 12, 17, 22, 7));

        // second big candle
        updateCalendar(calendar, 5, 6, 0, 0);
        candles.add(new Candle(symbol, periodSmall, calendar.getTimeInMillis(), 13, 18, 23, 8));

        updateCalendar(calendar, 5, 7, 0, 0);
        candles.add(new Candle(symbol, periodSmall, calendar.getTimeInMillis(), 14, 19, 24, 9));

        // third big candle
        updateCalendar(calendar, 5, 11, 0, 0);
        candles.add(new Candle(symbol, periodSmall, calendar.getTimeInMillis(), 15, 20, 25, 10));

        List<Candle> bigCandles = provider.convertToOtherPeriod(candles, periodBig);

        assertEquals(3, bigCandles.size());

        assertEquals(10, bigCandles.get(0).getOpen(), MathUtils.EPS);
        assertEquals(17, bigCandles.get(0).getClose(), MathUtils.EPS);
        assertEquals(22, bigCandles.get(0).getHigh(), MathUtils.EPS);
        assertEquals(5, bigCandles.get(0).getLow(), MathUtils.EPS);

        assertEquals(13, bigCandles.get(1).getOpen(), MathUtils.EPS);
        assertEquals(19, bigCandles.get(1).getClose(), MathUtils.EPS);
        assertEquals(24, bigCandles.get(1).getHigh(), MathUtils.EPS);
        assertEquals(8, bigCandles.get(1).getLow(), MathUtils.EPS);

        assertEquals(15, bigCandles.get(2).getOpen(), MathUtils.EPS);
        assertEquals(20, bigCandles.get(2).getClose(), MathUtils.EPS);
        assertEquals(25, bigCandles.get(2).getHigh(), MathUtils.EPS);
        assertEquals(10, bigCandles.get(2).getLow(), MathUtils.EPS);


    }
}
