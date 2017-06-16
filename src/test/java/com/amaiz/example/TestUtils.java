package com.amaiz.example;

import java.util.Calendar;

public class TestUtils {

    public static void updateCalendar(Calendar calendar, int min, int sec, int ms) {
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, ms);
    }

    public static void updateCalendar(Calendar calendar, int hour, int min, int sec, int ms) {
        calendar.set(Calendar.HOUR, hour);
        updateCalendar(calendar, min, sec, ms);
    }


}
