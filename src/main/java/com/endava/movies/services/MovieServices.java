package com.endava.movies.services;

import java.util.Map;

public interface MovieServices {
    boolean index(final Map<String, String> params);
    String generateReport(final double voteAverage);
}
