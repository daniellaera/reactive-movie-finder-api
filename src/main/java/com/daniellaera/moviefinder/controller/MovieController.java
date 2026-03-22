package com.daniellaera.moviefinder.controller;

import com.daniellaera.moviefinder.dto.MovieCreateRequestDTO;
import com.daniellaera.moviefinder.dto.MovieResponseDTO;
import com.daniellaera.moviefinder.exceptions.MovieNotFoundException;
import com.daniellaera.moviefinder.service.MovieService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public Flux<MovieResponseDTO> findAll() {
        return movieService.findAll();
    }

    @GetMapping("/{movieId}")
    public Mono<MovieResponseDTO> findById(@PathVariable Long movieId) {
        return movieService.findById(movieId)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(movieId)));
    }

    @GetMapping("/search")
    public Flux<MovieResponseDTO> findMovieBetweenTwoDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate minDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maxDate) {
        return movieService.findMovieBetweenTwoDates(minDate, maxDate);
    }

    @PostMapping
    public Mono<MovieResponseDTO> createMovie(@RequestBody MovieCreateRequestDTO dto) {
        return movieService.createMovie(dto);
    }
}
