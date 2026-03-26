package com.daniellaera.moviefinder.repository;

import com.daniellaera.moviefinder.model.Movie;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface MovieRepository extends ReactiveCrudRepository<Movie, Long> {

    @Query("SELECT * FROM movies WHERE publication_date BETWEEN :minDate AND :maxDate")
    Flux<Movie> findMovieBetweenTwoDates(LocalDate minDate, LocalDate maxDate);

    Flux<Movie> findMoviesByPublicationDateBetween(LocalDate publicationDateBefore, LocalDate publicationDateAfter);

    @Query("SELECT * FROM movies LIMIT :size OFFSET :offset")
    Flux<Movie> findAllPaginated(int size, int offset);

}
