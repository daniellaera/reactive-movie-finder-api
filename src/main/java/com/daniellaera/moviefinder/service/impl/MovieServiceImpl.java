package com.daniellaera.moviefinder.service.impl;

import com.daniellaera.moviefinder.dto.ActorResponseDTO;
import com.daniellaera.moviefinder.dto.MovieCreateRequestDTO;
import com.daniellaera.moviefinder.dto.MovieResponseDTO;
import com.daniellaera.moviefinder.model.Actor;
import com.daniellaera.moviefinder.model.Movie;
import com.daniellaera.moviefinder.model.MovieActor;
import com.daniellaera.moviefinder.repository.ActorRepository;
import com.daniellaera.moviefinder.repository.MovieActorRepository;
import com.daniellaera.moviefinder.repository.MovieRepository;
import com.daniellaera.moviefinder.service.MovieService;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final MovieActorRepository movieActorRepository;

    public MovieServiceImpl(MovieRepository movieRepository, ActorRepository actorRepository, DatabaseClient databaseClient, MovieActorRepository movieActorRepository) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.movieActorRepository = movieActorRepository;
    }

    @Override
    public Flux<MovieResponseDTO> findAllPaginated(int size, int offset) {
        return movieRepository.findAllPaginated(size, offset)
                .flatMap(movie -> // eventuel N+1 problème? 1 requête pour les films, puis 1 requête par film pour les acteurs. Avec 100 films → 101 requêtes en DB.
                        // serait-il mieux une seule requête qui ramene tout?
                        actorRepository.findActorsByMovieId(movie.getId())
                                .collectList()
                                .map(actors -> toResponse(movie,
                                        actors.stream()
                                                .map(a -> new ActorResponseDTO(a.getId(), a.getFirstName(), a.getLastName()))
                                                .toList()))
                );
    }

    @Override
    public Mono<MovieResponseDTO> findById(Long movieId) {
        return movieRepository.findById(movieId)
                .flatMap(movie ->
                        actorRepository.findActorsByMovieId(movie.getId())
                                .collectList()
                                .map(actors -> toResponse(movie,
                                        actors.stream()
                                                .map(
                                                        a -> new ActorResponseDTO(a.getId(), a.getFirstName(), a.getLastName())
                                                )
                                                .toList()))
                );
    }

    @Override
    public Flux<MovieResponseDTO> findMovieBetweenTwoDates(LocalDate minDate, LocalDate maxDate) {
        return movieRepository.findMovieBetweenTwoDates(minDate, maxDate)
                .flatMap(movie ->
                        actorRepository.findActorsByMovieId(movie.getId())
                                .collectList()
                                .map(actors -> toResponse(movie,
                                        actors.stream()
                                                .map(a -> new ActorResponseDTO(a.getId(), a.getFirstName(), a.getLastName()))
                                                .toList())));
    }

    @Override
    @Transactional
    public Mono<MovieResponseDTO> createMovie(MovieCreateRequestDTO request) {
        Movie movie = new Movie();
        movie.setName(request.name());
        movie.setGenre(request.genre());
        movie.setPublicationDate(request.publicationDate());

        return movieRepository.save(movie)
                .flatMap(saved ->
                        Flux.fromIterable(request.actors())
                                .flatMap(actorDTO -> {
                                    Actor actor = new Actor();
                                    actor.setFirstName(actorDTO.firstName());
                                    actor.setLastName(actorDTO.lastName());
                                    return actorRepository.save(actor)
                                            .flatMap(savedActor -> {
                                                MovieActor movieActor = new MovieActor();
                                                movieActor.setMovieId(saved.getId());
                                                movieActor.setActorId(savedActor.getId());
                                                return movieActorRepository.save(movieActor)
                                                        .thenReturn(savedActor);
                                            });
                                })
                                .collectList()
                                .map(savedActors -> toResponse(saved,
                                        savedActors.stream()
                                                .map(a -> new ActorResponseDTO(a.getId(), a.getFirstName(), a.getLastName()))
                                                .toList()))
                );
    }

    private MovieResponseDTO toResponse(Movie movie, List<ActorResponseDTO> actors) {
        return new MovieResponseDTO(
                movie.getId(),
                movie.getName(),
                movie.getGenre(),
                movie.getPublicationDate(),
                actors
        );
    }
}
