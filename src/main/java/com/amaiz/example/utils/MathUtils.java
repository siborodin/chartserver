package com.amaiz.example.utils;

public class MathUtils {

    public static final double EPS = 0.000005;

    public static int compare(double a, double b) {
        return compare(a, b, EPS);
    }

    public static int compare(double a, double b, double eps) {
        if (a > b + eps)
            return +1;
        if (a < b - eps)
            return -1;
        return (Double.isNaN(a) ? 1 : 0) - (Double.isNaN(b) ? 1 : 0);
    }

}
