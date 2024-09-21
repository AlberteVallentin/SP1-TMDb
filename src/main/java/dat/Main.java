// Main.java
package dat;

import dat.config.HibernateConfig;

import dat.dtos.MovieDTO;
import dat.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");
        MovieService movieService = new MovieService(emf);

        // Search movies with title "star"
        List<MovieDTO> searchResults = movieService.searchMoviesByTitle("star");
        for (MovieDTO movie : searchResults) {
            System.out.println("Found Movie: " + movie.getTitle());
        }






    }
}