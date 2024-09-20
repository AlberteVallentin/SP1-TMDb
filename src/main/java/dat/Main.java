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
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");
        MovieService movieService = new MovieService(emf);

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


        // Create the movie in the database
        movieService.createMovie(movieDTO);

        // Retrieve the movie by ID
        Long movieId = 1L;
        MovieDTO movieDTOFoundById = movieService.getMovieById(movieId);
        System.out.println("Retrieved movie: " + movieDTOFoundById.buildMovieDetails());

        // Retrieve all movies
        List<MovieDTO> allMovies = movieService.getAllMovies();
        for (MovieDTO allMoviesDTO : allMovies) {
            System.out.println(allMoviesDTO.buildMovieDetails());
            System.out.println("--------------------");
        }

        // Update the movie
        Long movieIdToUpdate = 1158L;
        MovieDTO movieDTOToUpdate = movieService.getMovieById(movieIdToUpdate);
        System.out.println("Movie before update: " + movieDTOToUpdate.buildMovieDetails());
        movieDTOToUpdate.setTitle("Updated Title");
        movieDTOToUpdate.setEnglishTitle("Updated English Title");
        movieDTOToUpdate.setReleaseDate(LocalDate.of(2024, 07, 14));
        movieDTOToUpdate.setVoteAverage(7.0);
        movieDTOToUpdate.setGenres(new HashSet<>() {{
            add(new GenreDTO("Action"));
            add(new GenreDTO("Adventure"));
        }});
        movieDTOToUpdate.setActors(new HashSet<>() {{
            add(new ActorDTO("Actor 1"));
            add(new ActorDTO("Actor 2"));
        }});
        movieDTOToUpdate.setDirector(null);
        movieService.updateMovie(movieDTOToUpdate);

        System.out.println("Updated movie: " + movieDTOToUpdate.buildMovieDetails());



    }
}