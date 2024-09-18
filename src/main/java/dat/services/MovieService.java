package dat.services;

import dat.daos.MovieDAO;
import dat.dtos.MovieDTO;
import dat.entities.Director;
import dat.entities.Movie;
import dat.entities.Actor;
import dat.entities.Genre;

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


    public void updateMovie(MovieDTO movieDTO) {
        Optional<Movie> optionalMovie = movieDAO.findById(movieDTO.getId());
        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();

            movie.setTitle(movieDTO.getTitle());
            movie.setEnglishTitle(movieDTO.getEnglishTitle());
            movie.setReleaseDate(movieDTO.getReleaseDate());
            movie.setVoteAverage(movieDTO.getVoteAverage());

            movie.setActors(movieDTO.getActors().stream().map(Actor::new).toList());
            movie.setGenres(movieDTO.getGenres().stream().map(Genre::new).toList());
            if (movieDTO.getDirector() != null) {
                movie.setDirector(new Director(movieDTO.getDirector()));
            }

            movieDAO.update(movie);
        } else {
            throw new IllegalArgumentException("Movie with ID " + movieDTO.getId() + " not found.");
        }
    }



    public void deleteMovie(Long id) {
        movieDAO.delete(id);
    }

    public Optional<Movie> findMovieByTitle(String title) {
        return movieDAO.findByName(title);
    }
}
