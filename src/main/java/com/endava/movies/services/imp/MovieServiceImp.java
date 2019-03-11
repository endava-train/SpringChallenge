package com.endava.movies.services.imp;


import com.endava.movies.batches.BatchLauncher;
import com.endava.movies.models.Movie;
import com.endava.movies.models.ResponseFormatMovie;
import com.endava.movies.repositories.MovieRepository;
import com.endava.movies.services.MovieServices;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MovieServiceImp implements MovieServices {

    private final MovieRepository movieRepository;
    private final BatchLauncher batchLauncher;

    @Value("${key.themoviedb}")
    private String keyTheMovieDB;

    public boolean index(final Map<String, String> params) {
        val urlApi = buildURL(params);
        return doRequestAndSaveURL(urlApi);
    }

    public String generateReport(final double voteAverage) {
        batchLauncher.launchDatabaseToCsvFileJob(voteAverage);
        return "OK";
    }

    private boolean doRequestAndSaveURL(String urlApi) {
        val restTemplate = new RestTemplate();
        try {
            val movies = restTemplate.getForObject(urlApi, ResponseFormatMovie.class);
            movies.getResults()
                    .stream()
                    .limit(10)
                    .forEach(movieRepository::save);
        }  catch (Exception e) {
            return false;
        }
        return true;
    }

    private String buildURL(final Map<String, String> params) {
        val urlApi = new StringBuilder(keyTheMovieDB);
        val validParameters = Arrays.asList("sort_by", "primary_release_year", "language");
        validParameters.forEach(x -> {
            if (params.containsKey(x)) {
                urlApi.append('&');
                urlApi.append(x);
                urlApi.append('=');
                urlApi.append(params.get(x));
            }
        });
        return urlApi.toString();
    }

}
