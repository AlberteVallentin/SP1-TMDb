package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.DirectorDTO;
import dat.entities.Director;

import dat.entities.Movie;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

class DirectorDAOTest {

    private static EntityManagerFactory emf;
    private static DirectorDAO directorDAO;
    private static DirectorDTO d1;
    private static DirectorDTO d2;

    @BeforeAll
    static void setUpBeforeAll() {

        emf = HibernateConfig.getEntityManagerFactoryForTest();
        directorDAO = new DirectorDAO(emf);
    }

    @BeforeEach
    void setUp() {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Director").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE director_id_seq RESTART WITH 1").executeUpdate();
            em.createQuery("DELETE FROM Movie").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE movie_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        }

        // Initialize and persist DirectorDTO objects
        d1 = new DirectorDTO("Steven Spielberg");
        d2 = new DirectorDTO("Christopher Nolan");

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


    @Test
    void createDirector() {
        // Create a new DirectorDTO
        DirectorDTO d3 = new DirectorDTO("Quentin Tarantino");

        // Create the director
        Director director3 = d3.toEntity();
        directorDAO.create(director3);

        // Set the ID of the DirectorDTO object
        d3.setId(director3.getId());

        // Check if the director was created
        assertNotNull(d3.getId());
    }

    @Test
    void findDirectorById() {
        Optional<Director> director = directorDAO.findById(1L);
        assertTrue(director.isPresent());
        assertEquals("Steven Spielberg", director.get().getName());
    }

    @Test
    void findAllDirectors() {
        List<Director> directors = directorDAO.findAll();
        assertEquals(2, directors.size());
    }

    @Test
    void updateDirector() {
        d1.setName("Updated Steven Spielberg");
        directorDAO.update(d1.toEntity());
        Optional<Director> director = directorDAO.findById(d1.getId());

        assertEquals("Updated Steven Spielberg", director.get().getName());
    }

    @Test
    void deleteDirector() {
        directorDAO.delete(d1.getId());
        Optional<Director> director = directorDAO.findById(d1.getId());
        assertFalse(director.isPresent());
    }

}
