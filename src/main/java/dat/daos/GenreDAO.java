package dat.daos;

import dat.entities.Genre;
import dat.entities.Movie;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

public class GenreDAO implements IDAO<Genre> {

    private final EntityManagerFactory emf;

    public GenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void create(Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Validate that the genre name is not null
            if (genre.getGenreName() == null || genre.getGenreName().isEmpty()) {
                throw new JpaException("Genre name cannot be null or empty.");
            }

            // Check if a genre with the same name already exists
            TypedQuery<Genre> query = em.createQuery(
                "SELECT g FROM Genre g WHERE LOWER(g.genreName) = LOWER(:genreName)", Genre.class
            );
            query.setParameter("genreName", genre.getGenreName());
            List<Genre> existingGenres = query.getResultList();

            if (!existingGenres.isEmpty()) {
                throw new JpaException("A genre with the name '" + genre.getGenreName() + "' already exists.");
            }

            // Persist the new genre
            em.persist(genre);

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to create genre in the database", e);
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
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g", Genre.class);
            return query.getResultList();
        }
    }

    @Override
    public void update(Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Find the existing genre in the database
            Genre existingGenre = em.find(Genre.class, genre.getId());
            if (existingGenre == null) {
                throw new JpaException("Genre with ID " + genre.getId() + " not found in the database.");
            }

            // Validate the genre name
            if (genre.getGenreName() == null || genre.getGenreName().isEmpty()) {
                throw new JpaException("Genre name cannot be null or empty.");
            }

            // Check if a genre with the same name already exists
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE LOWER(g.genreName) = LOWER(:genreName) AND g.id <> :id", Genre.class);
            query.setParameter("genreName", genre.getGenreName());
            query.setParameter("id", genre.getId());
            List<Genre> existingGenres = query.getResultList();

            if (!existingGenres.isEmpty()) {
                throw new JpaException("A genre with the name '" + genre.getGenreName() + "' already exists.");
            }

            // Update the existing genre
            existingGenre.setGenreName(genre.getGenreName());

            // Merge the updated genre
            em.merge(existingGenre);

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to update genre in the database", e);
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Genre genre = em.find(Genre.class, id);
            if (genre != null) {
                em.remove(genre);
            } else {
                throw new JpaException("Genre with ID " + id + " not found in the database.");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to delete genre from the database", e);
        }
    }

    @Override
    public Optional<Genre> findByName(String genreName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery(
                "SELECT g FROM Genre g WHERE LOWER(g.genreName) = LOWER(:genreName)", Genre.class
            );
            query.setParameter("genreName", genreName);
            return query.getResultStream().findFirst();
        }
    }


    // Fetch all movies within a specific genre
    public List<Movie> findMoviesByGenre(String genreName) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery(
                "SELECT m FROM Movie m JOIN m.genres g WHERE LOWER(g.genreName) = LOWER(:genreName)", Movie.class);
            query.setParameter("genreName", genreName);
            return query.getResultList();
        }
    }
}
