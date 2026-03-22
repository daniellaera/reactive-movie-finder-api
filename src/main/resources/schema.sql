CREATE TABLE movies
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    genre            VARCHAR(20)  NOT NULL,
    publication_date DATE         NOT NULL
);

CREATE TABLE actors
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL
);

CREATE TABLE movie_actors
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies (id),
    FOREIGN KEY (actor_id) REFERENCES actors (id)
);

INSERT INTO movies (name, genre, publication_date)
VALUES ('Titanic', 'DRAMA', '1997-12-19'),
       ('The Dark Knight', 'ACTION', '2008-07-18'),
       ('Inception', 'SCI_FI', '2010-07-16'),
       ('The Godfather', 'DRAMA', '1972-03-24'),
       ('Interstellar', 'SCI_FI', '2014-11-07'),
       ('The Matrix', 'ACTION', '1999-03-31'),
       ('Gladiator', 'ACTION', '2000-05-05'),
       ('Forrest Gump', 'DRAMA', '1994-07-06'),
       ('Avengers: Endgame', 'ACTION', '2019-04-26'),
       ('Dune', 'SCI_FI', '2021-10-22');

INSERT INTO actors (first_name, last_name)
VALUES ('Leonardo', 'DiCaprio'),
       ('Kate', 'Winslet'),
       ('Christian', 'Bale'),
       ('Heath', 'Ledger'),
       ('Keanu', 'Reeves'),
       ('Carrie-Anne', 'Moss'),
       ('Russell', 'Crowe'),
       ('Tom', 'Hanks'),
       ('Robert', 'Downey Jr.'),
       ('Timothée', 'Chalamet'),
       ('Al', 'Pacino'),
       ('Marlon', 'Brando'),
       ('Matthew', 'McConaughey'),
       ('Cillian', 'Murphy');

INSERT INTO movie_actors (movie_id, actor_id)
VALUES (1, 1),   -- Titanic: DiCaprio
       (1, 2),   -- Titanic: Winslet
       (2, 3),   -- Dark Knight: Bale
       (2, 4),   -- Dark Knight: Ledger
       (3, 1),   -- Inception: DiCaprio
       (3, 14),  -- Inception: Murphy
       (4, 11),  -- Godfather: Pacino
       (4, 12),  -- Godfather: Brando
       (5, 13),  -- Interstellar: McConaughey
       (5, 14),  -- Interstellar: Murphy
       (6, 5),   -- Matrix: Reeves
       (6, 6),   -- Matrix: Moss
       (7, 7),   -- Gladiator: Crowe
       (8, 8),   -- Forrest Gump: Hanks
       (9, 9),   -- Endgame: Downey Jr.
       (10, 10); -- Dune: Chalamet