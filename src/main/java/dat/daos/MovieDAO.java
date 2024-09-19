package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.MovieDTO;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.*;

public class MovieDAO implements IDAO<Movie> {

    private final EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }


    @Override
    public void create(Movie movie) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Title and Release Date are required, so we validate their presence
            if (movie.getTitle() == null || movie.getReleaseDate() == null) {
                throw new JpaException("Title and Release Date are required fields and cannot be null.");
            }

            // Set English title if it's not null or empty
            movie.setEnglishTitle(movie.getEnglishTitle() != null && !movie.getEnglishTitle().isEmpty() ? movie.getEnglishTitle() : null);

            // Set VoteAverage (optional, can be null)
            movie.setVoteAverage(movie.getVoteAverage());

            // Check if actors exist, if not persist them (can be null)
            Set<Actor> actorsToAdd = new HashSet<>();
            if (movie.getActors() != null) {
                for (Actor actor : movie.getActors()) {
                    Actor existingActor = em.createQuery("SELECT a FROM Actor a WHERE LOWER(a.name) = LOWER(:name)", Actor.class)
                        .setParameter("name", actor.getName())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                    if (existingActor == null) {
                        em.persist(actor); // Persist new actor
                    } else {
                        actorsToAdd.add(existingActor); // Use existing actor
                    }
                }
                movie.getActors().clear();
                movie.getActors().addAll(actorsToAdd); // Add all existing actors back to the movie
            } else {
                movie.setActors(null); // Actors can be null
            }

            // Check if director exists, if not persist them (can be null)
            if (movie.getDirector() != null) {
                Director existingDirector = em.createQuery("SELECT d FROM Director d WHERE LOWER(d.name) = LOWER(:name)", Director.class)
                    .setParameter("name", movie.getDirector().getName())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
                if (existingDirector == null) {
                    em.persist(movie.getDirector()); // Persist new director
                } else {
                    movie.setDirector(existingDirector); // Use existing director
                }
            } else {
                movie.setDirector(null); // Director can be null
            }

            // Check if genres exist, if not persist them (can be null)
            Set<Genre> genresToAdd = new HashSet<>();
            if (movie.getGenres() != null) {
                for (Genre genre : movie.getGenres()) {
                    Genre existingGenre = em.createQuery("SELECT g FROM Genre g WHERE LOWER(g.genreName) = LOWER(:genreName)", Genre.class)
                        .setParameter("genreName", genre.getGenreName())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                    if (existingGenre == null) {
                        em.persist(genre); // Persist new genre
                    } else {
                        genresToAdd.add(existingGenre); // Use existing genre
                    }
                }
                movie.getGenres().clear();
                movie.getGenres().addAll(genresToAdd); // Add all existing genres back to the movie
            } else {
                movie.setGenres(null); // Genres can be null
            }

            // Persist the movie
            em.persist(movie);

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Failed to create movie in the database", e);
        }
    }






    @Override
    public Optional<Movie> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = em.find(Movie.class, id);  // Look up a movie by its ID
            return movie != null ? Optional.of(movie) : Optional.empty();
        }
    }

    @Override
    public List<Movie> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
            return query.getResultList();
        }
    }

    @Override
    public void update(Movie movie) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(movie);
            em.getTransaction().commit();
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Movie movie = em.find(Movie.class, id);
            if (movie != null) {
                em.remove(movie);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public Optional<Movie> findByName(String title) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE LOWER(m.title) = LOWER(:name)", Movie.class);
            query.setParameter("name", title);
            return query.getResultStream().findFirst();
        }
    }




}
