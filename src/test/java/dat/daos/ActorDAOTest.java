package dat.daos;

import dat.config.HibernateConfig;
import dat.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorDAOTest {

    static ActorDAO dao;
    static EntityManagerFactory emf;
    Actor a1, a2, a3;

    @BeforeAll
    static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest(); // Assuming HibernateConfig is a utility for getting EntityManagerFactory
        dao = new ActorDAO(emf);
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            // Create actors to use in tests
            a1 = new Actor();
            a1.setName("Alfredo Fernandez");

            a2 = new Actor();
            a2.setName("Chris Evans");

            a3 = new Actor();
            a3.setName("Robert Downey Jr.");

            em.getTransaction().begin();
            em.createQuery("DELETE FROM Actor").executeUpdate(); // Clear Actor table before each test
            em.persist(a1);
            em.persist(a2);
            em.persist(a3);
            em.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        // Any clean-up code can go here
    }

    @Test
    @DisplayName("Test that we can create an Actor")
    void createActor() {
        Actor a4 = new Actor();
        a4.setName("Scarlett Johansson");

        // Persist the actor using the DAO
        dao.create(a4);

        // Verify the actor was persisted correctly
        Optional<Actor> actor = dao.findByName("Scarlett Johansson");
        assertTrue(actor.isPresent(), "Actor should be present in the database");
        assertEquals("Scarlett Johansson", actor.get().getName());
    }

    @Test
    @DisplayName("Test that we can get an Actor by their name")
    void getActorByName() {
        Optional<Actor> actor = dao.findByName("Alfredo Fernandez");
        assertTrue(actor.isPresent());
        assertEquals("Alfredo Fernandez", actor.get().getName());
    }

    @Test
    @DisplayName("Test that we can get an Actor by their ID")
    void getActorById() {
        Optional<Actor> actor = dao.findById(a1.getId());
        assertTrue(actor.isPresent());
        assertEquals("Alfredo Fernandez", actor.get().getName());
    }

    @Test
    @DisplayName("Test that we can get all Actors")
    void getAllActors() {
        int expected = 3; // We have persisted 3 actors
        int actual = dao.findAll().size();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test that we can update an Actor")
    void updateActor() {
        a1.setName("Alfredo Updated");
        dao.update(a1);

        Optional<Actor> updatedActor = dao.findById(a1.getId());
        assertTrue(updatedActor.isPresent());
        assertEquals("Alfredo Updated", updatedActor.get().getName());
    }

    @Test
    @DisplayName("Test that we can delete an Actor by their ID")
    void deleteActorById() {
        dao.delete(a1.getId());
        Optional<Actor> actor = dao.findById(a1.getId());
        assertTrue(actor.isEmpty());
    }
}