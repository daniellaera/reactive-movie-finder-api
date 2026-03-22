package com.daniellaera.moviefinder.dto;

import com.daniellaera.moviefinder.enums.MovieGenre;

import java.time.LocalDate;
import java.util.List;

public record MovieResponseDTO(
        Long id,
        String name,
        MovieGenre genre,
        LocalDate publicationDate,
        List<ActorResponseDTO> actors
) {}