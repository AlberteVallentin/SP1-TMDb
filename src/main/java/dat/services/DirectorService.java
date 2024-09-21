package dat.services;

import dat.config.HibernateConfig;
import dat.daos.DirectorDAO;
import dat.dtos.DirectorDTO;
import dat.dtos.MovieDTO;
import dat.entities.Director;
import dat.entities.Movie;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DirectorService {

    private final DirectorDAO directorDAO;

    public DirectorService(EntityManagerFactory emf) {
        this.directorDAO = new DirectorDAO(emf);
    }

    public void createDirector(DirectorDTO directorDTO) {
        try {
            Director director = directorDTO.toEntity();
            directorDAO.create(director);
        } catch (JpaException e) {
            System.out.println("Failed to create director: " + e.getMessage());
            throw e;
        }
    }

    public void updateDirector(DirectorDTO directorDTO) {
        try {
            Director director = directorDTO.toEntity();
            directorDAO.update(director);
        } catch (JpaException e) {
            System.out.println("Failed to update director: " + e.getMessage());
            throw e;
        }
    }

    public DirectorDTO getDirectorById(Long id) {
        try {
            Optional<Director> director = directorDAO.findById(id);
            DirectorDTO directorDTO = director.map(DirectorDTO::new)
                .orElseThrow(() -> new JpaException("No director found with ID: " + id));
            return directorDTO;
        } catch (JpaException e) {
            System.out.println("Failed to find director: " + e.getMessage());
            throw e;
        }
    }

    public List<MovieDTO> getMoviesByDirectorName(String directorName) {
        try {
            List<Movie> movies = directorDAO.findMoviesByDirector(directorName);
            return movies.stream().map(MovieDTO::new).collect(Collectors.toList());
        } catch (JpaException e) {
            System.out.println("Failed to find movies by director: " + e.getMessage());
            throw e;
        }
    }

    public List<DirectorDTO> getAllDirectors() {
        try {
            return directorDAO.findAll().stream()
                .map(DirectorDTO::new)
                .collect(Collectors.toList());
        } catch (JpaException e) {
            System.out.println("Failed to find directors: " + e.getMessage());
            throw e;
        }
    }

    public void deleteDirector(Long id) {
        try {
            directorDAO.delete(id);
        } catch (JpaException e) {
            System.out.println("Failed to delete director: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Director> findDirectorByName(String name) {
        try {
            return directorDAO.findByName(name);
        } catch (JpaException e) {
            System.out.println("Failed to find director by name: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        // Test the DirectorService class
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");
        DirectorService directorService = new DirectorService(emf);

        // Find director by name
        Optional<Director> director = directorService.findDirectorByName("Anders Morgenthaler");
        if (director.isPresent()) {
            System.out.println("Director found by name: " + director.get().getName());
        } else {
            System.out.println("Director not found.");
        }

        // Find all movies with the director
        List<MovieDTO> movies = directorService.getMoviesByDirectorName("Anders Morgenthaler");
        for (MovieDTO movie : movies) {
            System.out.println("Movie directed by " + director.get().getName() + ": " + movie.getTitle());
        }
    }





}

