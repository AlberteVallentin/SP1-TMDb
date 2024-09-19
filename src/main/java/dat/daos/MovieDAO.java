package dat.daos;

import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class MovieDAO implements IDAO<Movie> {

    private final EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void create(Movie movie) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

//            ActorDAO actorDAO = new ActorDAO(emf);
//            DirectorDAO directorDAO = new DirectorDAO(emf);
//            GenreDAO genreDAO = new GenreDAO(emf);

            // Check and save actors
            for (Actor actor : movie.getActors()) {
                Optional<Actor> existingActor = actorDAO.findByName(actor.getName());
                if (existingActor.isPresent()) {
                    actor.setId(existingActor.get().getId());
                } else {
                    em.persist(actor);
                }
            }

            // Check and save director
            Director director = movie.getDirector();
            if (director != null) {
               Optional<Director> existingDirector = directorDAO.findByName(director.getName());
                if (existingDirector.isPresent()) {
                    director.setId(existingDirector.get().getId());
                } else {
                    em.persist(director);
                }
            }

            // Check and save genres
            for (Genre genre : movie.getGenres()) {
                Optional<Genre> existingGenre = genreDAO.findByName(genre.getGenreName());
                if (existingGenre.isPresent()) {
                    genre.setId(existingGenre.get().getId());
                } else {
                    em.persist(genre);
                }
            }

            // Save the movie
            em.persist(movie);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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
