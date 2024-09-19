package dat.daos;
import dat.entities.Director;
import dat.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class GenreDAO implements IDAO<Genre> {

    private EntityManagerFactory emf;

    public GenreDAO(EntityManagerFactory emf) {

        this.emf = emf;
    }

    @Override
    public void create(Genre entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }

    }

    @Override
    public Optional<Genre> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {

            Genre genre = em.find(Genre.class, id);

            return genre != null ? Optional.of(genre) : Optional.empty();

        }

    }

    @Override
    public List<Genre> findAll() {
        try (EntityManager em = emf.createEntityManager()) {

            return em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
        }
    }

    @Override
    public void update(Genre entity) {

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
            Genre genre = em.find(Genre.class, id);
            if (genre != null) {
                em.remove(genre);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public Optional<Genre> findByName(String name) {

        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            TypedQuery<Genre> query=em.createQuery("SELECT g FROM Genre g WHERE g.name=:name",Genre.class);
            query.setParameter("name",name);

            List<Genre> genre = query.getResultList();
            // Check if the list is empty, return an Optional accordingly
            return genre.isEmpty() ? Optional.empty() : Optional.of(genre.get(0));
        }
    }

}



