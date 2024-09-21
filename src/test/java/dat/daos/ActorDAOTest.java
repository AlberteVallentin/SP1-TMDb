package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.ActorDTO;
import dat.dtos.MovieDTO;
import dat.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActorDAOTest {

    private static EntityManagerFactory emf;
    private static ActorDAO actorDAO;
    private static ActorDTO a1;
    private static ActorDTO a2;



    @BeforeAll
    static void BeforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        actorDAO = new ActorDAO(emf);

    }
    @BeforeEach
    void setUp() {
        // Clear the database before each test
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Actor").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE actor_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize and persist ActorDTO objects
        a1 = new ActorDTO("Tom Hanks");
        a2 = new ActorDTO("Leonardo DiCaprio");

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

    @AfterAll
    static void tearDown() {
        emf.close();
    }

    @Test
    @DisplayName("Test create actor")
    void create() {
        // Create a new ActorDTO
        ActorDTO a3 = new ActorDTO("Brad Pitt");

        // Convert to entity and create the actor
        Actor actor3 = a3.toEntity();
        actorDAO.create(actor3);

        // Set the ID of the ActorDTO object
        a3.setId(actor3.getId());

        // Check if the actor was created
        assertNotNull(a3.getId());
    }
    @Test
    @DisplayName("Test find actor by ID")
    void findById() {
        // Find actor by ID
        Optional<Actor> foundActor = actorDAO.findById(a1.getId());

        // Check if the actor is found
        assertEquals(a1.getName(), foundActor.get().getName());
    }

    @Test
    void findAll() {
       // find all actors
        assertEquals(2, actorDAO.findAll().size());

    }

    @Test
    @DisplayName("Test update actor")
    void update() {
        // Fetch the persisted actor
        Optional<Actor> optionalActorToUpdate = actorDAO.findById(a1.getId());

        // Ensure the actor exists
        if (optionalActorToUpdate.isPresent()) {
            Actor actorToUpdate = optionalActorToUpdate.get();

            // Modify the actor's name
            actorToUpdate.setName("Updated Name");

            // Update the actor
            try {
                actorDAO.update(actorToUpdate);

                // Fetch the updated actor and check the new name
                Optional<Actor> optionalUpdatedActor = actorDAO.findById(a1.getId());
                assertTrue(optionalUpdatedActor.isPresent(), "Updated actor should be found");

                Actor updatedActor = optionalUpdatedActor.get();
                assertEquals("Updated Name", updatedActor.getName());

            } catch (IllegalArgumentException e) {
                fail("Update failed with exception: " + e.getMessage());
            }
        } else {
            fail("Actor to update not found");
        }
    }

    @Test
    @DisplayName("Test delete actor")
    void delete() {
        // Delete the first actor
        actorDAO.delete(a1.getId());

        // Check if the actor was deleted
        Optional<Actor> deletedActor = actorDAO.findById(a1.getId());
        assertTrue(deletedActor.isEmpty());
    }

    @Test
    void findByName() {
        // Find the first actor by name
        Optional<Actor> foundActor = actorDAO.findByName(a1.getName());

        // Check if the actor is found
        assertEquals(a1.getName(), foundActor.get().getName());
    }


}