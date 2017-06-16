package com.amaiz.example.cs.model;

import org.junit.Test;

import java.util.Calendar;

import static com.amaiz.example.TestUtils.updateCalendar;
import static org.junit.Assert.assertEquals;

public class AggregationPeriodTest {

    @Test
    public void testGetStartTime() {

        AggregationPeriod period = new AggregationPeriod(AggregationType.M, 1);
        Calendar calendar = Calendar.getInstance();

        // time in the middle aggr period
        updateCalendar(calendar, 10, 10, 10);
        long start = period.getStartTime(calendar.getTimeInMillis());
        updateCalendar(calendar, 10, 0, 0);
        assertEquals(calendar.getTimeInMillis(), start);

        // time at the beginning of aggr period
        updateCalendar(calendar, 11, 0, 0);
        start = period.getStartTime(calendar.getTimeInMillis());
        assertEquals(calendar.getTimeInMillis(), start);

    }

    @Test
    public void testGetNextStartTime() {
        AggregationPeriod period = new AggregationPeriod(AggregationType.M, 3);
        Calendar calendar = Calendar.getInstance();

        // time in the middle aggr period
        updateCalendar(calendar, 13, 10, 10);
        long nextStart = period.getNextStartTime(calendar.getTimeInMillis());
        updateCalendar(calendar, 15, 0, 0);
        assertEquals(calendar.getTimeInMillis(), nextStart);

        // time at the beginning of aggr period
        updateCalendar(calendar, 18, 0, 0);
        nextStart = period.getNextStartTime(calendar.getTimeInMillis());
        updateCalendar(calendar, 21, 0, 0);
        assertEquals(calendar.getTimeInMillis(), nextStart);

    }


}
