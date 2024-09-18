package dat.daos;

import dat.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class GenreDAO implements IDAO<Genre> {

    private final EntityManagerFactory emf;

    public GenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }


    @Override
    public void create(Genre entity) {

    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Genre> findAll() {
        return List.of();
    }

    @Override
    public void update(Genre entity) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<Genre> findByName(String genreName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE LOWER(g.genreName) = LOWER(:genreName)", Genre.class);
            query.setParameter("genreName", genreName);
            return query.getResultStream().findFirst();
        }
    }
}