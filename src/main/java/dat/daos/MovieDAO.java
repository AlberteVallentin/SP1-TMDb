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

    private final EntityManagerFactory  emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void create(Movie movie) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(movie);  // Persisting the new movie into the database
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Movie> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Movie movie = em.find(Movie.class, id);  // Look up a movie by its ID
            return movie != null ? Optional.of(movie) : Optional.empty();  // Return an Optional
        } finally {
            em.close();
        }
    }

    @Override
    public List<Movie> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }


    @Override
    public void update(Movie movie) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(movie);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Movie movie = em.find(Movie.class, id);
            if (movie != null) {
                em.remove(movie);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}








    public void saveMovieWithDetails(Movie movie) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Først tjek, om der allerede findes aktører i databasen
            for (Actor actor : movie.getActors()) {
                Actor existingActor = findActorByName(em, actor.getName());
                if (existingActor != null) {
                    actor.setId(existingActor.getId());  // Brug eksisterende aktør
                } else {
                    em.persist(actor);  // Gem ny aktør
                }
            }

            // Tjek, om instruktøren allerede findes i databasen
            Director director = movie.getDirector();
            Director existingDirector = findDirectorByName(em, director.getName());
            if (existingDirector != null) {
                movie.setDirector(existingDirector);
            } else {
                em.persist(director);  // Gem ny instruktør
            }

            // Tjek, om genrerne allerede findes i databasen
            for (Genre genre : movie.getGenres()) {
                Genre existingGenre = findGenreByName(em, genre.getGenreName());
                if (existingGenre != null) {
                    genre.setId(existingGenre.getId());  // Brug eksisterende genre
                } else {
                    em.persist(genre);  // Gem ny genre
                }
            }

            // Endelig gem filmen med alle dens relaterede entiteter
            em.persist(movie);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Find eksisterende skuespiller ved navn
    private Actor findActorByName(EntityManager em, String name) {
        TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a WHERE a.name = :name", Actor.class);
        query.setParameter("name", name);
        return query.getResultStream().findFirst().orElse(null);
    }

    // Find eksisterende instruktør ved navn
    private Director findDirectorByName(EntityManager em, String name) {
        TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d WHERE d.name = :name", Director.class);
        query.setParameter("name", name);
        return query.getResultStream().findFirst().orElse(null);
    }

    // Find eksisterende genre ved navn
    private Genre findGenreByName(EntityManager em, String genreName) {
        TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE g.genreName = :genreName", Genre.class);
        query.setParameter("genreName", genreName);
        return query.getResultStream().findFirst().orElse(null);
    }


}
