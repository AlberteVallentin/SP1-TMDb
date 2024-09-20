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
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }
    }

    @Override
    public Optional<Director> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Director director = em.find(Director.class, id);
            return director != null ? Optional.of(director) : Optional.empty();
        }
    }

    @Override
    public List<Director> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d", Director.class);
            return query.getResultList();
        }
    }

    @Override
    public Optional<Director> update(Director entity) {
        return Optional.empty();
    }


    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Director director = em.find(Director.class, id);
            if (director != null) {
                em.remove(director);
            }
            em.getTransaction().commit();
        }
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
