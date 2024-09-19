package dat.services;

import dat.daos.ActorDAO;
import dat.entities.Actor;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class ActorService {

    private final ActorDAO actorDAO;

    public ActorService(EntityManagerFactory emf) {
        this.actorDAO = new ActorDAO(emf);
    }

    public void createActor(Actor actor) {
        actorDAO.create(actor);
    }

    public Optional<Actor> findActorById(Long id) {
        return actorDAO.findById(id);
    }

    public List<Actor> getAllActors() {
        return actorDAO.findAll();
    }

    public void updateActor(Actor actor) {
        actorDAO.update(actor);
    }


    public void deleteActor(Long id) {
        actorDAO.delete(id);
    }


    public Optional<Actor> findActorByName(String name) {
        return actorDAO.findByName(name);
    }
}
