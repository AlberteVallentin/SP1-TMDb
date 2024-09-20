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
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Genre> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Genre genre = em.find(Genre.class, id);  // Find genre by ID
            return genre != null ? Optional.of(genre) : Optional.empty();  // Return an Optional
        }
    }

    @Override
    public List<Genre> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g", Genre.class);
            return query.getResultList();
        }
    }

    @Override
    public Optional<Genre> update(Genre entity) {
        return Optional.empty();
    }


    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Genre genre = em.find(Genre.class, id);
            if (genre != null) {
                em.remove(genre);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
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