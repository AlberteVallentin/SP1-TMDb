package dat.daos;

import dat.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class ActorDAO implements IDAO<Actor> {

    private final EntityManagerFactory emf;

    public ActorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }
    @Override
    public void create(Actor entity) {

    }

    @Override
    public Optional<Actor> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Actor> findAll() {
        return List.of();
    }

    @Override
    public void update(Actor entity) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<Actor> findByName(String title) {
        // Try-with-resources to ensure EntityManager is closed automatically
        try (EntityManager em = emf.createEntityManager()) {
            // JPQL query where both name and actor's name are converted to lower case for case-insensitive comparison
            TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a WHERE LOWER(a.name) = LOWER(:name)", Actor.class);
            query.setParameter("name", title);
            return query.getResultStream().findFirst();  // Return the first match or empty if none found
        }
    }

}
