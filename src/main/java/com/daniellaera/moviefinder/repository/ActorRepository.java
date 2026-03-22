package com.daniellaera.moviefinder.repository;

import com.daniellaera.moviefinder.model.Actor;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ActorRepository extends ReactiveCrudRepository<Actor, Long> {

    @Query("""
        SELECT a.* FROM actors a
        JOIN movie_actors ma ON ma.actor_id = a.id
        WHERE ma.movie_id = :movieId
        """)
    Flux<Actor> findActorsByMovieId(Long movieId);
}
