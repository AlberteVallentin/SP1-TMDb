package dat.services;

import dat.daos.MovieDAO;
import dat.entities.Movie;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class MovieService {

    private final MovieDAO movieDAO;


    public MovieService(EntityManagerFactory emf) {
        this.movieDAO = new MovieDAO(emf);
    }


    public void createMovie(Movie movie) {
        movieDAO.create(movie);
    }


    public Optional<Movie> findMovieById(Long id) {
        return movieDAO.findById(id);
    }


    public List<Movie> getAllMovies() {
        return movieDAO.findAll();
    }


    public void updateMovie(Movie movie) {
        movieDAO.update(movie);
    }


    public void deleteMovie(Long id) {
        movieDAO.delete(id);
    }

    public Optional<Movie> findMovieByTitle(String title) {
        return movieDAO.findByName(title);
    }
}
