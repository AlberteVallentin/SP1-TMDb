package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.ActorDTO;
import dat.dtos.DirectorDTO;
import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovieDAOTest {

    private static EntityManagerFactory emf;
    private static MovieDAO movieDAO;
    private static MovieDTO m1;
    private static MovieDTO m2;

    @BeforeAll
    static void setUpBeforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        movieDAO = new MovieDAO(emf);
    }

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Movie").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE movie_id_seq RESTART WITH 1").executeUpdate();
            em.createQuery("DELETE FROM Actor").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE actor_id_seq RESTART WITH 1").executeUpdate();
            em.createQuery("DELETE FROM Director").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE director_id_seq RESTART WITH 1").executeUpdate();
            em.createQuery("DELETE FROM Genre").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE genre_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test create movie")
    void create() {
        // Create MovieDTO objects
        m1 = new MovieDTO("Test 1", "English title 1", LocalDate.of(2024, 7, 14), 8.0,
            new DirectorDTO("Steven Spielberg"),
            new HashSet<>() {{
                add(new GenreDTO("Drama"));
                add(new GenreDTO("War"));
            }},
            new HashSet<>() {{
                add(new ActorDTO("Tom Hanks"));
            }});

        m2 = new MovieDTO("Test 2", "English title 2", LocalDate.of(2024, 7, 14), 8.0,
            new DirectorDTO("Martin Scorsese"),
            new HashSet<>() {{
                add(new GenreDTO("Action"));
            }},
            new HashSet<>() {{
                add(new ActorDTO("Leonardo DiCaprio"));
            }});


        // Convert MovieDTO to Movie entity
        Movie movie1 = m1.toEntity();
        Movie movie2 = m2.toEntity();

        // Persist the movies
        movieDAO.create(movie1);
        movieDAO.create(movie2);

        // Find the movies in the database
        EntityManager em = emf.createEntityManager();
        Movie foundMovie1 = em.find(Movie.class, movie1.getId());
        Movie foundMovie2 = em.find(Movie.class, movie2.getId());

        // Check if the movies were found
        assertNotNull(foundMovie1);
        assertNotNull(foundMovie2);

        em.close();
    }


}
