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
            // Check if the actor already exists
            Optional<Actor> existingActor = findByName(entity.getName());
            if (existingActor.isPresent()) {
                System.out.println("Actor with the name '" + entity.getName() + "' already exists.");
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

            // Find the actor by ID to ensure it exists
            Actor existingActor = em.find(Actor.class, entity.getId());
            if (existingActor == null) {
                throw new IllegalArgumentException("Actor with ID " + entity.getId() + " does not exist.");
            }

            // Optional: Compare key fields (such as name) if necessary
            if (!existingActor.getName().equals(entity.getName())) {
                System.out.println("Updating actor name...");
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
