package com.daniellaera.moviefinder.controller;

import com.daniellaera.moviefinder.dto.ActorResponseDTO;
import com.daniellaera.moviefinder.dto.MovieResponseDTO;
import com.daniellaera.moviefinder.enums.MovieGenre;
import com.daniellaera.moviefinder.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieController.class)
class MovieControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private MovieService movieService;

    @Test
    @DisplayName("GET /api/movies/withActors/id - returns a movie and its actors")
    void findByIdWithActors() {
        MovieResponseDTO response = buildMovieResponse(1L, "Forrest Gump", MovieGenre.ACTION,
                LocalDate.parse("1997-12-19"),
                List.of(new ActorResponseDTO(1L, "Tom", "Hanks")));

        when(movieService.getMovieWithActors(1L)).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/movies/withActors/{movieId}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Forrest Gump")
                .jsonPath("$.genre").isEqualTo("ACTION")
                .jsonPath("$.actors.length()").isEqualTo(1)
                .jsonPath("$.actors[0].firstName").isEqualTo("Tom")
                .jsonPath("$.actors[0].lastName").isEqualTo("Hanks");
    }

    @Test
    @DisplayName("GET /api/movies - returns all movies")
    void getAllMovies_returnsFluxOfMovies() {
        MovieResponseDTO m1 = buildMovieResponse(1L, "Forrest Gump", MovieGenre.ACTION, LocalDate.parse("1997-12-19"), List.of());
        MovieResponseDTO m2 = buildMovieResponse(2L, "Cast Away", MovieGenre.DRAMA, LocalDate.parse("1994-07-06"), List.of());

        when(movieService.findAll()).thenReturn(Flux.just(m1, m2));

        webTestClient.get()
                .uri("/api/movies")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Forrest Gump")
                .jsonPath("$[0].genre").isEqualTo("ACTION")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].name").isEqualTo("Cast Away")
                .jsonPath("$[1].genre").isEqualTo("DRAMA");
    }

    @Test
    @DisplayName("GET /api/movies/id - returns a movie by id")
    void findById() {
        MovieResponseDTO m1 = buildMovieResponse(1L, "Forrest Gump", MovieGenre.ACTION, LocalDate.parse("1997-12-19"), List.of());

        when(movieService.findById(1L)).thenReturn(Mono.just(m1));

        webTestClient.get()
                .uri("/api/movies/{movieId}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Forrest Gump")
                .jsonPath("$.genre").isEqualTo("ACTION");
    }

    @Test
    @DisplayName("GET /api/movies/search - returns all movies between a range of dates")
    void findMovieBetweenTwoDates() {
        MovieResponseDTO m1 = buildMovieResponse(1L, "Forrest Gump", MovieGenre.ACTION, LocalDate.parse("1997-12-20"), List.of());
        MovieResponseDTO m2 = buildMovieResponse(2L, "Cast Away", MovieGenre.DRAMA, LocalDate.parse("1994-05-04"), List.of());

        LocalDate minDate = LocalDate.parse("1993-01-01");
        LocalDate maxDate = LocalDate.parse("2000-05-05");

        when(movieService.findMovieBetweenTwoDates(minDate, maxDate))
                .thenReturn(Flux.just(m1, m2));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/movies/search")
                        .queryParam("minDate", minDate)
                        .queryParam("maxDate", maxDate)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[1].id").isEqualTo(2);

        verify(movieService).findMovieBetweenTwoDates(minDate, maxDate);
    }

    private MovieResponseDTO buildMovieResponse(Long id, String name, MovieGenre genre,
                                                LocalDate publicationDate, List<ActorResponseDTO> actors) {
        return new MovieResponseDTO(id, name, genre, publicationDate, actors);
    }
}