package dat.daos;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

public class MovieDAO implements IDAO<Movie> {

    private EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf){

        this.emf=emf;
    }

    @Override
    public void create(Movie entity) {
        final EntityManager em = null;
        EntityTransaction transaction = null;

        try {
          // Single EntityManager creation
            transaction = em.getTransaction();
            transaction.begin();

            // Ensure that the Actors, Director, and Genres are attached to the persistence context
            List<Actor> managedActors = entity.getActors().stream()
                    .map(actor -> em.find(Actor.class, actor.getId()))  // Fetch existing actors from the database
                    .filter(actor -> actor != null)  // Filter out any null results
                    .collect(Collectors.toList());

            Director managedDirector = em.find(Director.class, entity.getDirector().getId());  // Fetch the director
            if (managedDirector == null) {
                throw new IllegalArgumentException("Director with ID " + entity.getDirector().getId() + " not found");
            }

            List<Genre> managedGenres = entity.getGenres().stream()
                    .map(genre -> em.find(Genre.class, genre.getId()))  // Fetch existing genres from the database
                    .filter(genre -> genre != null)  // Filter out any null results
                    .collect(Collectors.toList());

            // Set the managed entities in the movie before persisting
            entity.setActors(managedActors);
            entity.setDirector(managedDirector);
            entity.setGenres(managedGenres);

            // Persist the movie entity
            em.persist(entity);

            transaction.commit();  // Commit the transaction
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();  // Rollback if something goes wrong
            }
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();  // Close the EntityManager
            }
        }
    }


    @Override
    public Optional<Movie> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Movie> findAll() {
        return List.of();
    }

    @Override
    public void update(Movie entity) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<Movie> findByName(String name) {
        return Optional.empty();
    }
}
