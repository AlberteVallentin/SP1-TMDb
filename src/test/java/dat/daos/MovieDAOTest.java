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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

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

        // Initialize and persist MovieDTO objects
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

        // Set the IDs of the MovieDTO objects
        m1.setId(movie1.getId());
        m2.setId(movie2.getId());


    }

    @Test
    @DisplayName("Test create movie")
    void create() {

        // Create a new movieDTO
        MovieDTO m3 = new MovieDTO();
        m3.setTitle("titel");
        m3.setEnglishTitle(null);
        m3.setReleaseDate(LocalDate.of(2023, 03, 14));
        m3.setVoteAverage(0.0);
        m3.setGenres(new HashSet<>() {{
                add(new GenreDTO("Drama"));
                add(new GenreDTO("War"));
            }});
        m3.setActors(new HashSet<>() {{
                add(new ActorDTO("Actor 1"));
                add(new ActorDTO("Actor 2"));
            }});
        m3.setDirector(new DirectorDTO("Director"));

        // Create the movie
        Movie movie3 = m3.toEntity();
        movieDAO.create(movie3);

        // Set the ID of the MovieDTO object
        m3.setId(movie3.getId());

        // Check if the movie was created
        assertNotNull(m3.getId());


    }

    @Test
    public void findById() {
        // Find the first movie
        Optional<Movie> optionalMovie = movieDAO.findById(m1.getId());

        // Check if the movie was found
        assertTrue(optionalMovie.isPresent());

        // Expected movie ID
        Long expectedId = 1L;

        // Check if the movie ID is correct
        assertEquals(expectedId, optionalMovie.get().getId());
    }

    @Test
    public void findAll() {
        // Find all movies
        List<Movie> movies = movieDAO.findAll();

        // Check if all movies were found
        assertEquals(2, movies.size());

        // Check if the movie titles are correct
        assertEquals("Test 1", movies.get(0).getTitle());
        assertEquals("Test 2", movies.get(1).getTitle());
    }




}
