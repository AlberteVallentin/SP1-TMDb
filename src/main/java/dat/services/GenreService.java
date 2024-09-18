package dat.services;

import dat.daos.GenreDAO;
import dat.entities.Genre;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class GenreService {

    private final GenreDAO genreDAO;

    public GenreService(EntityManagerFactory emf) {
        this.genreDAO = new GenreDAO(emf);
    }

    public void createGenre(Genre genre) {
        genreDAO.create(genre);
    }

    public Optional<Genre> findGenreById(Long id) {
        return genreDAO.findById(id);
    }

    public List<Genre> getAllGenres() {
        return genreDAO.findAll();
    }

    public void updateGenre(Genre genre) {
        genreDAO.update(genre);
    }

    public void deleteGenre(Long id) {
        genreDAO.delete(id);
    }

    public Optional<Genre> findGenreByName(String genreName) {
        return genreDAO.findByName(genreName);
    }
}

