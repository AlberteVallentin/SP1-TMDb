package dat.daos;

import dat.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class DirectorDAO implements IDAO<Director> {

    private final EntityManagerFactory emf;

    public DirectorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }
    @Override
    public void create(Director entity) {

    }

    @Override
    public Optional<Director> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Director> findAll() {
        return List.of();
    }

    @Override
    public void update(Director entity) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<Director> findByName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d WHERE LOWER(d.name) = LOWER(:name)", Director.class);
            query.setParameter("name", name);
            return query.getResultStream().findFirst();
        }
    }
}
