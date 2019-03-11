package com.endava.movies.controllers;

import com.endava.movies.services.MovieServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MoviesController {

    private final MovieServices movieService;

    @GetMapping("persist")
    public ResponseEntity<String> persist(@RequestParam Map<String, String> params) {
        if (movieService.index(params)) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("generateReport")
    public ResponseEntity<String> generateReport(@RequestParam Double voteAverage) {
        return new ResponseEntity<>(movieService.generateReport(voteAverage), HttpStatus.OK);
    }
}
