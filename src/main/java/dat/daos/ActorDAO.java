package dat.daos;

import dat.entities.Actor;
import dat.entities.Movie;
import dat.exceptions.JpaException;
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
    public void create(Actor actor) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Validate that the actor's name is not null
            if (actor.getName() == null || actor.getName().isEmpty()) {
                throw new JpaException("Actor name cannot be null or empty.");
            }

            // Check if an actor with the same name already exists
            TypedQuery<Actor> query = em.createQuery(
                "SELECT a FROM Actor a WHERE LOWER(a.name) = LOWER(:name)", Actor.class
            );
            query.setParameter("name", actor.getName());
            List<Actor> existingActors = query.getResultList();

            if (!existingActors.isEmpty()) {
                throw new JpaException("An actor with the name '" + actor.getName() + "' already exists.");
            }

            // Persist the new actor
            em.persist(actor);

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to create actor in the database", e);
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
    public void update(Actor actor) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Find the existing actor in the database
            Actor existingActor = em.find(Actor.class, actor.getId());
            if (existingActor == null) {
                throw new JpaException("Actor with ID " + actor.getId() + " not found in the database.");
            }

            // Validate the actor name
            if (actor.getName() == null || actor.getName().isEmpty()) {
                throw new JpaException("Actor name cannot be null or empty.");
            }

            // Check if an actor with the same name already exists
            TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a WHERE LOWER(a.name) = LOWER(:name) AND a.id <> :id", Actor.class);
            query.setParameter("name", actor.getName());
            query.setParameter("id", actor.getId());
            List<Actor> existingActors = query.getResultList();

            if (!existingActors.isEmpty()) {
                throw new JpaException("An actor with the name '" + actor.getName() + "' already exists.");
            }

            // Update the existing actor
            existingActor.setName(actor.getName());

            // Merge the updated actor
            em.merge(existingActor);

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to update actor in the database", e);
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Actor actor = em.find(Actor.class, id);
            if (actor != null) {
                em.remove(actor);
            } else {
                throw new JpaException("Actor with ID " + id + " not found in the database.");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to delete actor from the database", e);
        }
    }

    @Override
    public Optional<Actor> findByName(String actorName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Actor> query = em.createQuery(
                "SELECT a FROM Actor a WHERE LOWER(a.name) = LOWER(:actorName)", Actor.class
            );
            query.setParameter("actorName", actorName);
            return query.getResultStream().findFirst();
        }
    }

    // Fetch all movies in which the actor has been part
    public List<Movie> findMoviesWithActor(String actorName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery(
                "SELECT m FROM Movie m JOIN m.actors a WHERE LOWER(a.name) = LOWER(:actorName)", Movie.class);
            query.setParameter("actorName", actorName);
            return query.getResultList();
        }
    }
}

