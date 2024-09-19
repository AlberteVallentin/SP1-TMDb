// Main.java
package dat;

import dat.config.HibernateConfig;
import dat.daos.MovieDAO;
import dat.dtos.ActorDTO;
import dat.dtos.DirectorDTO;
import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import dat.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");

        // Create a new movieDTO
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("En god film");
        movieDTO.setEnglishTitle("A good movie");
        movieDTO.setReleaseDate(LocalDate.of(2024, 07, 14));
        movieDTO.setVoteAverage(9.3);
        movieDTO.setGenres(Set.of(new GenreDTO("Drama"), new GenreDTO("Action"), new GenreDTO("en ny genre")));
        movieDTO.setActors(Set.of(new ActorDTO("Alfredo"), new ActorDTO("anton")));
        movieDTO.setDirector(new DirectorDTO("Dennis"));

        // Call the MovieService to create the movie
        MovieService movieService = new MovieService(emf);
        movieService.createMovie(movieDTO);

    }
}