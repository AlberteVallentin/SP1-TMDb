package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.ActorDTO;
import dat.entities.Actor;
import dat.entities.Movie;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActorDAOTest {

    private static EntityManagerFactory emf;
    private static ActorDAO actorDAO;
    private static ActorDTO a1;
    private static ActorDTO a2;


    @BeforeAll
    static void setUpBeforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        actorDAO = new ActorDAO(emf);
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Actor").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE actor_id_seq RESTART WITH 1").executeUpdate();
            em.createQuery("DELETE FROM Movie").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE movie_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        }

        // Initialize and persist ActorDTO objects
        a1 = new ActorDTO("Tom Hanks");
        a2 = new ActorDTO("Brad Pitt");

        // Convert ActorDTO to Actor entity
        Actor actor1 = a1.toEntity();
        Actor actor2 = a2.toEntity();

        // Persist the actors
        actorDAO.create(actor1);
        actorDAO.create(actor2);

        // Set the IDs of the ActorDTO objects
        a1.setId(actor1.getId());
        a2.setId(actor2.getId());
    }


    @Test
    void createActor() {
        // Create a new actorDTO
        ActorDTO a3 = new ActorDTO("Meryl Streep");

        // Create the actor
        Actor actor3 = a3.toEntity();
        actorDAO.create(actor3);

        // Set the ID of the ActorDTO object
        a3.setId(actor3.getId());

        // Check if the actor was created
        assertNotNull(a3.getId());


    }

    @Test
    void findActorById() {
        Optional<Actor> actor = actorDAO.findById(1L);
        assertTrue(actor.isPresent());
        assertEquals("Tom Hanks", actor.get().getName());
    }

    @Test
    void findAllActors() {
        List<Actor> actors = actorDAO.findAll();
        assertEquals(2, actors.size());
    }

    @Test
    void updateActor() {
        a1.setName("Updated Tom Hanks");
        actorDAO.update(a1.toEntity());
        Optional<Actor> actor = actorDAO.findById(a1.getId());

        assertEquals("Updated Tom Hanks", actor.get().getName());
    }

    @Test
    void deleteActor() {
        actorDAO.delete(a1.getId());
        Optional<Actor> actor = actorDAO.findById(a1.getId());
        assertFalse(actor.isPresent());
    }

}
