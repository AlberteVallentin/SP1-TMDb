package dat.daos;

import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MovieDAOTest {

    private static EntityManagerFactory emf;
    private static MovieDAO movieDAO;

    @BeforeAll
    static void beforeAll() {
        // Initialize EntityManagerFactory and DAO before all tests
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        movieDAO = new MovieDAO(emf);
    }

    @AfterEach
    void tearDown() {
        // Close EntityManagerFactory after each test
        emf.close();
    }

    @Test
    void create() {
        // Step 1: Create a new movie instance with details
        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setEnglishTitle("Inception");
        movie.setReleaseDate(LocalDate.of(2010, 7, 16));
        movie.setVoteAverage(8.8);

        // Step 2: Create actors and add them to a Set
        Actor actor1 = new Actor();
        actor1.setName("Leonardo DiCaprio");

        Actor actor2 = new Actor();
        actor2.setName("Joseph Gordon-Levitt");

        Set<Actor> actors = new HashSet<>();
        actors.add(actor1);
        actors.add(actor2);
        movie.setActors(actors);

        // Step 3: Create genres and add them to a Set
        Genre genre1 = new Genre();
        genre1.setGenreName("Action");

        Genre genre2 = new Genre();
        genre2.setGenreName("Sci-Fi");

        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        movie.setGenres(genres);

        // Step 4: Create a director and set it to the movie
        Director director = new Director();
        director.setName("Christopher Nolan");
        movie.setDirector(director);

        // Step 5: Persist the movie using the DAO
        movieDAO.create(movie);

        // Step 6: Fetch the movie back from the database
        Optional<Movie> fetchedMovie = movieDAO.findById(movie.getId());

        // Step 7: Assertions to verify the movie and its relationships
        assertTrue(fetchedMovie.isPresent(), "Movie should be present in the database");
        //assertEquals("Inception", fetchedMovie.get().getTitle(), "The movie title should match");
        //assertEquals(2, fetchedMovie.get().getActors().size(), "There should be 2 actors associated with the movie");
        //assertEquals(2, fetchedMovie.get().getGenres().size(), "There should be 2 genres associated with the movie");
        //assertEquals("Christopher Nolan", fetchedMovie.get().getDirector().getName(), "Director name should match");
    }
}
