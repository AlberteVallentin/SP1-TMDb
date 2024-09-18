package dat.services;

import dat.daos.DirectorDAO;
import dat.entities.Director;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class DirectorService {

    private final DirectorDAO directorDAO;

    public DirectorService(EntityManagerFactory emf) {
        this.directorDAO = new DirectorDAO(emf);
    }

    public void createDirector(Director director) {
        directorDAO.create(director);
    }

    public Optional<Director> findDirectorById(Long id) {
        return directorDAO.findById(id);
    }

    public List<Director> getAllDirectors() {
        return directorDAO.findAll();
    }

    public void updateDirector(Director director) {
        directorDAO.update(director);
    }

    public void deleteDirector(Long id) {
        directorDAO.delete(id);
    }

    public Optional<Director> findDirectorByName(String name) {
        return directorDAO.findByName(name);
    }
}

