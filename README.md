# 🎬 Movie Finder API

A reactive REST API built with **Spring WebFlux** and **Spring Data R2DBC**, demonstrating non-blocking, asynchronous programming with Project Reactor.

---

## Tech Stack

- **Java 21**
- **Spring Boot 4.x** with Spring WebFlux
- **Spring Data R2DBC** — reactive database access
- **Project Reactor** — `Mono` / `Flux` pipelines
- **H2** — in-memory database (dev/test)
- **Lombok** — boilerplate reduction

---

## Architecture

```
Controller  →  Service  →  Repository  →  DB
    ↓               ↓
  DTOs          Entities
```

### Layering rules
- **Entities** (`Movie`, `Actor`, `MovieActor`) are never exposed outside the service layer
- **DTOs** are split by direction:
    - Input: `MovieCreateRequestDTO`, `ActorCreateRequestDTO`
    - Output: `MovieResponseDTO`, `ActorResponseDTO`

---

## Project Structure

```
src/main/java/com/daniellaera/moviefinder/
├── controller/
│   └── MovieController.java
├── service/
│   ├── MovieService.java
│   └── impl/MovieServiceImpl.java
├── repository/
│   ├── MovieRepository.java
│   ├── ActorRepository.java
│   └── MovieActorRepository.java
├── entity/
│   ├── BaseEntity.java
│   ├── Movie.java
│   ├── Actor.java
│   └── MovieActor.java
├── dto/
│   ├── MovieCreateRequestDTO.java
│   ├── MovieResponseDTO.java
│   ├── ActorCreateRequestDTO.java
│   └── ActorResponseDTO.java
└── exceptions/
    ├── MovieNotFoundException.java
    └── GlobalControllerAdvice.java
```

---

## Database Schema

```sql
CREATE TABLE movies (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    genre            VARCHAR(20)  NOT NULL,
    publication_date DATE         NOT NULL
);

CREATE TABLE actors (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL
);

CREATE TABLE movie_actors (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies (id),
    FOREIGN KEY (actor_id) REFERENCES actors (id)
);
```

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/movies` | Get all movies with actors |
| `GET` | `/api/movies/{id}` | Get movie by ID with actors |
| `GET` | `/api/movies/dates?minDate=&maxDate=` | Get movies between two dates |
| `GET` | `/api/movies/{id}/with-actors` | Get movie with actors (via zip) |
| `POST` | `/api/movies` | Create a movie with actors |

### POST `/api/movies` — Request body

```json
{
  "name": "Oppenheimer",
  "genre": "DRAMA",
  "publicationDate": "2023-07-21",
  "actors": [
    { "firstName": "Cillian", "lastName": "Murphy" },
    { "firstName": "Emily", "lastName": "Blunt" }
  ]
}
```

### Response

```json
{
  "id": 11,
  "name": "Oppenheimer",
  "genre": "DRAMA",
  "publicationDate": "2023-07-21",
  "actors": [
    { "id": 15, "firstName": "Cillian", "lastName": "Murphy" },
    { "id": 16, "firstName": "Emily", "lastName": "Blunt" }
  ]
}
```

### Supported genres

`ACTION`, `DRAMA`, `SCI_FI`

---

## Key Design Decisions

### Reactive pipeline — `createMovie`

Movie creation uses a fully non-blocking pipeline:
1. Save the `Movie` via `movieRepository.save()`
2. For each actor in the request, save an `Actor` via `actorRepository.save()`
3. For each saved actor, insert the join record via `movieActorRepository.save()`
4. Collect all saved actors with `collectList()` and build the `MovieResponseDTO`

`@Transactional` ensures full rollback if any step fails.

### N+1 query awareness

`findAll()` currently uses `flatMap` to fetch actors per movie — this is intentional for clarity and works acceptably thanks to parallel execution via `flatMap`. A single LEFT JOIN query with grouping is a known optimization path.

### Error handling

`MovieNotFoundException` is thrown via `switchIfEmpty` in the controller and handled globally by `GlobalControllerAdvice` (`@RestControllerAdvice`), returning a clean `404` response.

### Entity mapping

Spring Data R2DBC maps columns to entity fields via **reflection**, bypassing setters. Setters are only used when constructing entities manually before `save()`. `BaseEntity` centralizes the `@Id` field. Audit fields (`createdDate`, `updatedDate`) are marked `@Transient` to avoid mapping issues.

---

## Running the Application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

The H2 database is initialized automatically from `schema.sql` and `data.sql` on startup.

---

## Notes for Interview

- Never use `block()` in production — breaks the reactive model
- `doOnNext` is a side-effect tap only, not a transformer
- `StepVerifier` acts as the subscriber in tests — never call `.subscribe()` alongside it
- `flatMap` = async + parallel; `concatMap` = async + sequential; `map` = sync
- `zipWith` combines two independent Monos in parallel