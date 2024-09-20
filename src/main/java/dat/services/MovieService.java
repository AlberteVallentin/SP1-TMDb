package dat.services;

import dat.daos.MovieDAO;
import dat.dtos.MovieDTO;
import dat.entities.Movie;

import dat.exceptions.JpaException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieService {
    private final MovieDAO movieDAO;

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


    public void updateMovie(MovieDTO movieDTO) {
        try {
            Movie movie = movieDTO.toEntity();
            movieDAO.findById(movie.getId());
            movieDAO.update(movie);
        } catch (JpaException e) {
            System.out.println("Failed to update movie: " + e.getMessage());
            throw e;
        }
    }


        public MovieDTO getMovieById (Long id){
            try {
                Optional<Movie> movie = movieDAO.findById(id);
                MovieDTO movieDTO = movie.map(MovieDTO::new)
                    .orElseThrow(() -> new JpaException("No movie found with ID: " + id));
                System.out.println("The movie with ID " + id + " was found. ");
                return movieDTO;
            } catch (JpaException e) {
                System.out.println("JpaException: " + e.getMessage());
                throw e;
            }

        }

        public List<MovieDTO> getAllMovies () {
            return movieDAO.findAll().stream()
                .map(MovieDTO::new)
                .collect(Collectors.toList());
        }


    public void deleteMovie(Long id) {
        try {
            movieDAO.delete(id);
            System.out.println("The movie with ID " + id + " was deleted.");
        } catch (JpaException e) {
            System.out.println("Failed to delete movie: " + e.getMessage());
            throw e;
        }
    }
        public Optional<Movie> findMovieByTitle (String title){
            return movieDAO.findByName(title);
        }
    }
