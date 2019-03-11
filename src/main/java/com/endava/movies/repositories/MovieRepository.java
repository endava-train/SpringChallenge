package com.endava.movies.repositories;

import com.endava.movies.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MovieRepository extends CrudRepository<Movie, Integer>, PagingAndSortingRepository<Movie, Integer> {
    Page<Movie> findByVoteAverageGreaterThanEqual(double voteAverage, Pageable pageable);
}
