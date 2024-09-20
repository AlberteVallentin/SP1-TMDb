package dat.daos;

import dat.entities.Actor;
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
            // Check if the actor already exists
            Optional<Director> existingDirector = findByName(entity.getName());
            if (existingDirector.isPresent()) {
                System.out.println("Director with the name '" + entity.getName() + "' already exists.");
                return;  // Avoid inserting a duplicate actor
            }
            em.getTransaction().begin();
            em.persist(entity);  // Persist the new actor
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
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
    public void update(Director entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Find the director by ID to ensure it exists
            Director existingDirector = em.find(Director.class, entity.getId());
            if (existingDirector == null) {
                throw new IllegalArgumentException("Director with ID " + entity.getId() + " does not exist.");
            }

            // Optional: Compare key fields (such as name) if necessary
            if (!existingDirector.getName().equals(entity.getName())) {
                System.out.println("Updating director name...");
            }

            // Perform the update (merge returns the updated entity)
            em.merge(entity);
            em.getTransaction().commit();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();  // Log the error or handle accordingly
        }
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
