package com.boris.pendo.microblog.util;

import java.time.Instant;

public class Calculation {

    private final static Instant A = Instant.parse("2018-10-13T10:37:30.00Z"), B = Instant.parse("2030-10-13T10:37:30.00Z");
    private final static long a = 1, b = 1000;

    private final static double SCORE_WEIGHT = 0.7, CREATE_TIME_WEIGHT = 0.3;

    /**
     * Normalize the create_time (in milliseconds) of a post
     *
     * Not Normalized: Minimum = A = Mon Oct 22 2018 19:14:12 GMT+0300 = 1540224852000 ,
     *                 Maximum = B = Mon Oct 22 2030 19:14:12 GMT+0300 = 1918916052000
     * Normalized: Minimum = a = 1 , Maximum = b = 1000
     * Any value from the original data = x
     *
     * Equation: a + (x-A)(b-a)/(B-A)
     */
    public static long dateNormalizer(Instant createTime) {
        long x = createTime.toEpochMilli();
        return a + (x - A.toEpochMilli()) * (b - a) / (B.toEpochMilli() - A.toEpochMilli());
    }

    /**
     * Calculating a post's score, by two parameters: rate and date.
     * Each parameter is given a different weight.
     */
    public static double calculateScore(int score, long normalizedDate) {
        return (SCORE_WEIGHT * score) + (CREATE_TIME_WEIGHT * normalizedDate);
    }
}
