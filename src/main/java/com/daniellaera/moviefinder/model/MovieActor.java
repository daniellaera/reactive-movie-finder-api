package com.daniellaera.moviefinder.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("MOVIE_ACTORS")
public class MovieActor {

    @Id
    private Long id;
    @Column("MOVIE_ID")
    private Long movieId;
    @Column("ACTOR_ID")
    private Long actorId;

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }
}
