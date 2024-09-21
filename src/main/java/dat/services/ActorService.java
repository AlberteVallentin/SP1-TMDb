package dat.services;

import dat.config.HibernateConfig;
import dat.daos.ActorDAO;
import dat.dtos.ActorDTO;
import dat.dtos.MovieDTO;
import dat.entities.Actor;
import dat.entities.Movie;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActorService {

    private final ActorDAO actorDAO;

    public ActorService(EntityManagerFactory emf) {
        this.actorDAO = new ActorDAO(emf);
    }

    public void createActor(ActorDTO actorDTO) {
        try {
            Actor actor = actorDTO.toEntity();
            actorDAO.create(actor);
        } catch (JpaException e) {
            System.out.println("Failed to create actor: " + e.getMessage());
            throw e;
        }
    }

    public void updateActor(ActorDTO actorDTO) {
        try {
            Actor actor = actorDTO.toEntity();
            actorDAO.update(actor);
        } catch (JpaException e) {
            System.out.println("Failed to update actor: " + e.getMessage());
            throw e;
        }
    }

    public ActorDTO getActorById(Long id) {
        try {
            Optional<Actor> actor = actorDAO.findById(id);
            ActorDTO actorDTO = actor.map(ActorDTO::new)
                .orElseThrow(() -> new JpaException("No actor found with ID: " + id));
            return actorDTO;
        } catch (JpaException e) {
            System.out.println("Failed to find actor: " + e.getMessage());
            throw e;
        }
    }

    public List<MovieDTO> getMoviesByActorName(String actorName) {
        try {
            List<Movie> movies = actorDAO.findMoviesWithActor(actorName);
            return movies.stream().map(MovieDTO::new).collect(Collectors.toList());
        } catch (JpaException e) {
            System.out.println("Failed to find movies by actor: " + e.getMessage());
            throw e;
        }
    }

    public List<ActorDTO> getAllActors() {
        try {
            return actorDAO.findAll().stream()
                .map(ActorDTO::new)
                .collect(Collectors.toList());
        } catch (JpaException e) {
            System.out.println("Failed to get all actors: " + e.getMessage());
            throw e;
        }
    }

    public void deleteActor(Long id) {
        try {
            actorDAO.delete(id);
        } catch (JpaException e) {
            System.out.println("Failed to delete actor: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Actor> findActorByName(String name) {
        try {
            return actorDAO.findByName(name);
        } catch (JpaException e) {
            System.out.println("Failed to find actor by name: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        // Test the ActorService class
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");
        ActorService actorService = new ActorService(emf);

        // Find actor by name
        Optional<Actor> actor = actorService.findActorByName("Mathias Broe");
        System.out.println("Actor found by name: " + actor.get().getName());


        // Find all movies with actor
        List<MovieDTO> movies = actorService.getMoviesByActorName("Mathias Broe");
        for (MovieDTO movie : movies) {
            System.out.println("Movie with "+ actor.get().getName() + " in it: " + movie.getTitle());
        }

    }
}
