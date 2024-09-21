package dat.daos;

import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Movie;
import dat.exceptions.JpaException;
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
    public void create(Director director) {
        try (EntityManager em = emf.createEntityManager()) {
            // Check if the actor already exists
            Optional<Director> existingDirector = findByName(entity.getName());
            if (existingDirector.isPresent()) {
                System.out.println("Director with the name '" + entity.getName() + "' already exists.");
                return;  // Avoid inserting a duplicate actor
            }
            em.getTransaction().begin();

            // Validate that the director's name is not null
            if (director.getName() == null || director.getName().isEmpty()) {
                throw new JpaException("Director name cannot be null or empty.");
            }

            // Check if a director with the same name already exists
            TypedQuery<Director> query = em.createQuery(
                "SELECT d FROM Director d WHERE LOWER(d.name) = LOWER(:name)", Director.class
            );
            query.setParameter("name", director.getName());
            List<Director> existingDirectors = query.getResultList();

            if (!existingDirectors.isEmpty()) {
                throw new JpaException("A director with the name '" + director.getName() + "' already exists.");
            }

            // Persist the new director
            em.persist(director);

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to create director in the database", e);

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
    public void update(Director director) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();


            // Find the existing director in the database
            Director existingDirector = em.find(Director.class, director.getId());
            if (existingDirector == null) {
                throw new JpaException("Director with ID " + director.getId() + " not found in the database.");
            }

            // Validate the director name
            if (director.getName() == null || director.getName().isEmpty()) {
                throw new JpaException("Director name cannot be null or empty.");
            }

            // Check if a director with the same name already exists
            TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d WHERE LOWER(d.name) = LOWER(:name) AND d.id <> :id", Director.class);
            query.setParameter("name", director.getName());
            query.setParameter("id", director.getId());
            List<Director> existingDirectors = query.getResultList();

            if (!existingDirectors.isEmpty()) {
                throw new JpaException("A director with the name '" + director.getName() + "' already exists.");
            }

            // Update the existing director
            existingDirector.setName(director.getName());

            // Merge the updated director
            em.merge(existingDirector);

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to update director in the database", e);
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Director director = em.find(Director.class, id);
            if (director != null) {
                em.remove(director);
            } else {
                throw new JpaException("Director with ID " + id + " not found in the database.");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to delete director from the database", e);
        }
    }

    @Override
    public Optional<Director> findByName(String directorName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Director> query = em.createQuery(
                "SELECT d FROM Director d WHERE LOWER(d.name) = LOWER(:directorName)", Director.class
            );
            query.setParameter("directorName", directorName);
            return query.getResultStream().findFirst();
        }
    }

    // Fetch all movies directed by a specific director
    public List<Movie> findMoviesByDirector(String directorName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery(
                "SELECT m FROM Movie m JOIN m.director d WHERE LOWER(d.name) = LOWER(:directorName)", Movie.class);
            query.setParameter("directorName", directorName);
            return query.getResultList();
        }
    }

}
