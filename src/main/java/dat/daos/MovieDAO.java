package dat.daos;

import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class MovieDAO implements IDAO<Movie> {

    private EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf){

        this.emf=emf;
    }


    @Override
    public void create(Movie entity) {
        try(EntityManager em= emf.createEntityManager()){


        }
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Movie> findAll() {
        return List.of();
    }

    @Override
    public void update(Movie entity) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<Movie> findByName(String name) {
        return Optional.empty();
    }
}
