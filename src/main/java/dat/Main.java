package dat;

import dat.config.HibernateConfig;
import dat.daos.ActorDAO;
import dat.daos.DirectorDAO;
import dat.daos.GenreDAO;
import dat.daos.MovieDAO;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");
        Actor actor1 = new Actor("Alfredo Fernandez");
        Actor actor2 = new Actor("Alberte Vallentin");
        Actor actor3 = new Actor("Pablo Escobar Garcia");

        ActorDAO actorDAO = new ActorDAO(emf);
//        actorDAO.create(actor1);
//        actorDAO.create(actor2);
//        actorDAO.create(actor3);
//
//        emf.close();

        System.out.println("list of actors: " + actorDAO.findById(3653L).get().getName());



        // 1. Fetch the actor you want to update by ID (e.g., with ID 3653L)
        Optional<Actor> actorToUpdateOpt = actorDAO.findById(3653L);

        if (actorToUpdateOpt.isPresent()) {
            Actor actorToUpdate = actorToUpdateOpt.get();

            // 2. Update the name to "Pablo Escobar Garcia"
            actorToUpdate.setName("Pablo Escobar Garcia");

            // 3. Call the update method
            actorDAO.update(actorToUpdate);

            System.out.println("Actor updated successfully!");
        } else {
            System.out.println("Actor not found!");
        }

        emf.close();

    }


}