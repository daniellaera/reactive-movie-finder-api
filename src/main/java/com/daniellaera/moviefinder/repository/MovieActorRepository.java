package com.daniellaera.moviefinder.repository;

import com.daniellaera.moviefinder.model.MovieActor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieActorRepository extends ReactiveCrudRepository<MovieActor, Long> {
}
