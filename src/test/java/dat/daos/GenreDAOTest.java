package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.GenreDTO;
import dat.entities.Genre;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenreDAOTest {

    private static EntityManagerFactory emf;
    private static GenreDAO genreDAO;
    private static GenreDTO g1;
    private static GenreDTO g2;

    @BeforeAll
    static void setUpBeforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        genreDAO = new GenreDAO(emf);
    }

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Genre").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE genre_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize and persist GenreDTO objects
        g1 = new GenreDTO("Action");
        g2 = new GenreDTO("Drama");

        // Convert GenreDTO to Genre entity
        Genre genre1 = g1.toEntity();
        Genre genre2 = g2.toEntity();

        // Persist the genres
        genreDAO.create(genre1);
        genreDAO.create(genre2);

        // Set the IDs of the GenreDTO objects
        g1.setId(genre1.getId());
        g2.setId(genre2.getId());
    }

    @Test
    @DisplayName("Test create genre")
    void create() {
        // Create a new genreDTO
        GenreDTO g3 = new GenreDTO("Comedy");

        // Create the genre
        Genre genre3 = g3.toEntity();
        genreDAO.create(genre3);

        // Set the ID of the GenreDTO object
        g3.setId(genre3.getId());

        // Check if the genre was created
        assertNotNull(g3.getId());
    }

    @Test
    public void findById() {
        // Find the first genre
        Optional<Genre> optionalGenre = genreDAO.findById(g1.getId());

        // Check if the genre was found
        assertTrue(optionalGenre.isPresent());

        // Expected genre ID
        Long expectedId = g1.getId();

        // Check if the genre ID is correct
        assertEquals(expectedId, optionalGenre.get().getId());
    }

    @Test
    void findAll() {
        // Find all genres
        List<Genre> genres = genreDAO.findAll();

        // Check if all genres were found
        assertEquals(2, genres.size());

        // Check if the genre names are correct
        assertEquals("Action", genres.get(0).getGenreName());
        assertEquals("Drama", genres.get(1).getGenreName());
    }

    @Test
    void update() {
        // Update the first genre
        g1.setGenreName("Updated Action");

        // Update the genre
        genreDAO.update(g1.toEntity());

        // Find the updated genre
        Optional<Genre> optionalGenre = genreDAO.findById(g1.getId());

        // Check if the genre was found
        assertTrue(optionalGenre.isPresent());

        // Check if the genre was updated
        assertEquals("Updated Action", optionalGenre.get().getGenreName());
    }

    @Test
    void delete() {
        // Delete the first genre
        genreDAO.delete(g1.getId());

        // Find the first genre
        Optional<Genre> optionalGenre = genreDAO.findById(g1.getId());

        // Check if the genre was deleted
        assertFalse(optionalGenre.isPresent());
    }

    @Test
    void findByName() {
        // Find the first genre
        Optional<Genre> optionalGenre = genreDAO.findByName("Action");

        // Check if the genre was found
        assertTrue(optionalGenre.isPresent());

        // Expected genre name
        String expectedGenreName = "Action";

        // Check if the genre name is correct
        assertEquals(expectedGenreName, optionalGenre.get().getGenreName());
    }

    @Test
    void genreAlreadyExists() {
        // Try to create a genre that already exists
        GenreDTO g3 = new GenreDTO("Action");

        // Try to create the genre
        Genre genre3 = g3.toEntity();

        // Check if the exception was thrown
        assertThrows(JpaException.class, () -> genreDAO.create(genre3));
    }
}
