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

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");
        Actor actor1 = new Actor("Alfredo Fernandez");
        Actor actor2 = new Actor("Alberte Vallentin");
        Actor actor3 = new Actor("Malte Pavon");

        ActorDAO actorDAO = new ActorDAO(emf);
        actorDAO.create(actor1);
        actorDAO.create(actor2);
        actorDAO.create(actor3);

        emf.close();




    }


}