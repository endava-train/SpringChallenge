package com.endava.movies.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseFormatMovie {

    private Integer page;

    private Integer totalResults;

    private Integer totalPages;

    private List<Movie> results;
}
