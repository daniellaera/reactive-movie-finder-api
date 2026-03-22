package com.daniellaera.moviefinder.service;

import com.daniellaera.moviefinder.dto.MovieCreateRequestDTO;
import com.daniellaera.moviefinder.dto.MovieResponseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public interface MovieService {
    Flux<MovieResponseDTO> findAll();

    Mono<MovieResponseDTO> findById(Long movieId);

    Flux<MovieResponseDTO> findMovieBetweenTwoDates(LocalDate minDate, LocalDate maxDate);

    Mono<MovieResponseDTO> createMovie(MovieCreateRequestDTO request);

}
