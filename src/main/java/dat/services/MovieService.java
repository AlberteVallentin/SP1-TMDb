package dat.services;

import dat.daos.ActorDAO;
import dat.daos.DirectorDAO;
import dat.daos.GenreDAO;
import dat.daos.MovieDAO;
import dat.dtos.MovieDTO;
import dat.entities.Director;
import dat.entities.Movie;
import dat.entities.Actor;
import dat.entities.Genre;

import dat.exceptions.JpaException;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieService {
    private final MovieDAO movieDAO;
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    public MovieService(EntityManagerFactory emf) {
        this.movieDAO = new MovieDAO(emf);
    }


    public void createMovie(MovieDTO movieDTO) {
        try {
            Movie movie = movieDTO.toEntity();
            movieDAO.create(movie);
        } catch (JpaException e) {
            System.out.println("Failed to create movie: " + e.getMessage());
            throw e;
        }
    }


    public Optional<Movie> findMovieById(Long id) {
        return movieDAO.findById(id);
    }


    public List<Movie> getAllMovies() {
        return movieDAO.findAll();
    }


//    public void updateMovie(MovieDTO movieDTO) {
//        Optional<Movie> optionalMovie = movieDAO.findById(movieDTO.getId());
//
//        if (optionalMovie.isPresent()) {
//            Movie movie = optionalMovie.get();
//
//            // Update fields if present in the DTO
//            if (movieDTO.getTitle() != null) {
//                movie.setTitle(movieDTO.getTitle());
//            }
//            if (movieDTO.getEnglishTitle() != null) {
//                movie.setEnglishTitle(movieDTO.getEnglishTitle());
//            }
//            if (movieDTO.getReleaseDate() != null) {
//                movie.setReleaseDate(movieDTO.getReleaseDate());
//            }
//            if (movieDTO.getVoteAverage() != 0.0) {
//                movie.setVoteAverage(movieDTO.getVoteAverage());
//            }
//
//            // Update actors
//            if (movieDTO.getActors() != null) {
//                Set<Actor> actors = movieDTO.getActors().stream()
//                    .map(actorDTO -> {
//                        Optional<Actor> existingActor = actorDAO.findByName(actorDTO.getName());
//                        if (existingActor.isPresent()) {
//                            return existingActor.get();  // Use the existing actor
//                        } else {
//                            Actor newActor = new Actor(actorDTO);  // Create new actor
//                            actorDAO.create(newActor);  // Persist new actor
//                            return newActor;
//                        }
//                    })
//                    .collect(Collectors.toSet());
//                movie.setActors(actors);
//            }
//
//            // Update genres
//            if (movieDTO.getGenres() != null) {
//                Set<Genre> genres = movieDTO.getGenres().stream()
//                    .map(genreDTO -> {
//                        Optional<Genre> existingGenre = genreDAO.findByName(genreDTO.getGenreName());
//                        if (existingGenre.isPresent()) {
//                            return existingGenre.get();  // Use the existing genre
//                        } else {
//                            Genre newGenre = new Genre(genreDTO);  // Create new genre
//                            genreDAO.create(newGenre);  // Persist new genre
//                            return newGenre;
//                        }
//                    })
//                    .collect(Collectors.toSet());
//                movie.setGenres(genres);
//            }
//
//            // Update director
//            if (movieDTO.getDirector() != null) {
//                Optional<Director> existingDirector = directorDAO.findByName(movieDTO.getDirector().getName());
//                if (existingDirector.isPresent()) {
//                    movie.setDirector(existingDirector.get());  // Use existing director
//                } else {
//                    Director newDirector = new Director(movieDTO.getDirector());  // Create new director
//                    directorDAO.create(newDirector);  // Persist new director
//                    movie.setDirector(newDirector);
//                }
//            }
//
//            // Update the movie entity in the database
//            movieDAO.update(movie);
//        } else {
//            throw new IllegalArgumentException("Movie with ID " + movieDTO.getId() + " not found.");
//        }
//    }




    public void deleteMovie(Long id) {
        movieDAO.delete(id);
    }

    public Optional<Movie> findMovieByTitle(String title) {
        return movieDAO.findByName(title);
    }
}
