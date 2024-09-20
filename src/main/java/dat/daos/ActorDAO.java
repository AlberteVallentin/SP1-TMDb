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
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }
    }

    @Override
    public Optional<Actor> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Actor actor = em.find(Actor.class, id);
            return actor != null ? Optional.of(actor) : Optional.empty();
        }
    }

    @Override
    public List<Actor> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a", Actor.class);
            return query.getResultList();
        }
    }

    @Override
    public void update(Actor entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        }
    }


    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Actor actor = em.find(Actor.class, id);
            if (actor != null) {
                em.remove(actor);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public Optional<Actor> findByName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a WHERE LOWER(a.name) = LOWER(:name)", Actor.class);
            query.setParameter("name", name);
            return query.getResultStream().findFirst();
        }
    }
}
