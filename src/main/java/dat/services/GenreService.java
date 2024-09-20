package dat.services;

import dat.daos.GenreDAO;
import dat.dtos.GenreDTO;
import dat.entities.Genre;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenreService {
    private final GenreDAO genreDAO;

    public GenreService(EntityManagerFactory emf) {
        this.genreDAO = new GenreDAO(emf);
    }

    // Create a new genre
    public void createGenre(GenreDTO genreDTO) {
        try {
            Genre genre = genreDTO.toEntity();
            genreDAO.create(genre);
        } catch (JpaException e) {
            System.out.println("Failed to create genre: " + e.getMessage());
            throw e;
        }
    }

    // Update an existing genre
    public void updateGenre(GenreDTO genreDTO) {
        try {
            Genre genre = genreDTO.toEntity();
            genreDAO.findById(genre.getId());
            genreDAO.update(genre);
        } catch (JpaException e) {
            System.out.println("Failed to update genre: " + e.getMessage());
            throw e;
        }
    }

    // Get genre by ID
    public GenreDTO getGenreById(Long id) {
        try {
            Optional<Genre> genre = genreDAO.findById(id);
            return genre.map(GenreDTO::new)
                .orElseThrow(() -> new JpaException("No genre found with ID: " + id));
        } catch (JpaException e) {
            System.out.println("JpaException: " + e.getMessage());
            throw e;
        }
    }

    // Get all genres
    public List<GenreDTO> getAllGenres() {
        return genreDAO.findAll().stream()
            .map(GenreDTO::new)
            .collect(Collectors.toList());
    }

    // Delete genre by ID
    public void deleteGenre(Long id) {
        try {
            genreDAO.delete(id);
            System.out.println("The genre with ID " + id + " was deleted.");
        } catch (JpaException e) {
            System.out.println("Failed to delete genre: " + e.getMessage());
            throw e;
        }
    }

    // Find genre by name
    public Optional<Genre> findGenreByName(String name) {
        try {
            return genreDAO.findByName(name);
        } catch (JpaException e) {
            System.out.println("Failed to find genre by name: " + e.getMessage());
            throw e;
        }
    }
}

