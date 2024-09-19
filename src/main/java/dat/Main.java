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
import java.util.Optional;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");

        // Create a new movieDTO
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("titel");
        movieDTO.setEnglishTitle(null);
        movieDTO.setReleaseDate(LocalDate.of(2024, 07, 14));
        movieDTO.setVoteAverage(0.0);
        movieDTO.setGenres(new HashSet<>() {{
            add(new GenreDTO("Drama"));
            add(new GenreDTO("War"));
        }});
        movieDTO.setActors(null);
        movieDTO.setDirector(null);


        // Call the MovieService to create the movie
        MovieService movieService = new MovieService(emf);
        movieService.createMovie(movieDTO);
        movieService.getMovieById(1L);

    }
}