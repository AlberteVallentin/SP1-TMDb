package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.MovieDTO;
import dat.entities.Director;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieDAOTest {
    private static MovieDAO dao;
    private static EntityManagerFactory emf;

    @BeforeAll
    static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = new MovieDAO(emf);

    }


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        // Clean up the database after each test
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Movie").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    void create() {
        // Create a new MovieDTO instance
        MovieDTO movieDTO = new MovieDTO();

    }
}