package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.DirectorDTO;
import dat.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class DirectorDAOTest {

    private static EntityManagerFactory emf;
    private static DirectorDAO directorDAO;
    private static DirectorDTO d1;
    private static DirectorDTO d2;

    @BeforeAll
    static void BeforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        directorDAO = new DirectorDAO(emf);
    }

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Director").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE director_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize and persist DirectorDTO objects
        d1 = new DirectorDTO("Steven Spielberg");
        d2 = new DirectorDTO("Martin Scorsese");

        // Convert DirectorDTO to Director entity
        Director director1 = d1.toEntity();
        Director director2 = d2.toEntity();

        // Persist the directors
        directorDAO.create(director1);
        directorDAO.create(director2);

        // Set the IDs of the DirectorDTO objects
        d1.setId(director1.getId());
        d2.setId(director2.getId());
    }

    @AfterAll
   static void tearDown() {
        emf.close();
    }

    @Test
    @DisplayName("Test create director")
    void create() {
        // Create a new DirectorDTO
        DirectorDTO d3 = new DirectorDTO("Quentin Tarantino");

        // Convert to entity and create the director
        Director director3 = d3.toEntity();
        directorDAO.create(director3);

        // Set the ID of the DirectorDTO object
        d3.setId(director3.getId());

        // Check if the director was created
        assertNotNull(d3.getId());
    }

    @Test
    @DisplayName("Test find director by ID")
    void findById() {
        // Find director by ID
        Optional<Director> foundDirector = directorDAO.findById(d1.getId());

        // Check if the director is found
        assertEquals(d1.getName(), foundDirector.get().getName());
    }

    @Test
    void findAll() {
        // Find all directors
        assertEquals(2, directorDAO.findAll().size());
    }

    @Test
    @DisplayName("Test update director")
    void update() {
        // Fetch the persisted director
        Optional<Director> optionalDirectorToUpdate = directorDAO.findById(d1.getId());

        // Ensure the director exists
        if (optionalDirectorToUpdate.isPresent()) {
            Director directorToUpdate = optionalDirectorToUpdate.get();

            // Modify the director's name
            directorToUpdate.setName("Updated Name");

            // Update the director
            try {
                directorDAO.update(directorToUpdate);

                // Re-fetch the updated director from the database
                Optional<Director> optionalUpdatedDirector = directorDAO.findById(d1.getId());
                assertTrue(optionalUpdatedDirector.isPresent(), "Updated director should be found");

                Director updatedDirector = optionalUpdatedDirector.get();
                // Check if the name was updated correctly
                assertEquals("Updated Name", updatedDirector.getName());

            } catch (IllegalArgumentException e) {
                fail("Update failed with exception: " + e.getMessage());
            }
        } else {
            fail("Director to update not found");
        }
    }

    @Test
    @DisplayName("Test delete director")
    void delete() {
        // Delete the first director
        directorDAO.delete(d1.getId());

        // Check if the director was deleted
        Optional<Director> deletedDirector = directorDAO.findById(d1.getId());
        assertTrue(deletedDirector.isEmpty());
    }
}

