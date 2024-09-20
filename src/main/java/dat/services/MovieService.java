package dat.services;

import dat.config.HibernateConfig;
import dat.daos.MovieDAO;
import dat.dtos.ActorDTO;
import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;
import dat.entities.Movie;

import dat.exceptions.JpaException;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieService {
    private final MovieDAO movieDAO;

    public MovieService(EntityManagerFactory emf) {
        this.movieDAO = new MovieDAO(emf);
    }


    public void createMovie(MovieDTO movieDTO) {
        try {
            Movie movie = movieDTO.toEntity();
            movieDAO.create(movie);
        } catch (JpaException e) {
            System.out.println("Failed to create movie: " + e.getMessage());
            throw e;
        }
    }


    public void updateMovie(MovieDTO movieDTO) {
        try {
            Movie movie = movieDTO.toEntity();
            movieDAO.findById(movie.getId());
            movieDAO.update(movie);
        } catch (JpaException e) {
            System.out.println("Failed to update movie: " + e.getMessage());
            throw e;
        }
    }


    public MovieDTO getMovieById(Long id) {
        try {
            Optional<Movie> movie = movieDAO.findById(id);
            MovieDTO movieDTO = movie.map(MovieDTO::new)
                .orElseThrow(() -> new JpaException("No movie found with ID: " + id));
            System.out.println("The movie with ID " + id + " was found. ");
            return movieDTO;
        } catch (JpaException e) {
            System.out.println("JpaException: " + e.getMessage());
            throw e;
        }

    }

    public List<MovieDTO> getAllMovies() {
        return movieDAO.findAll().stream()
            .map(MovieDTO::new)
            .collect(Collectors.toList());
    }


    public void deleteMovie(Long id) {
        try {
            movieDAO.delete(id);
            System.out.println("The movie with ID " + id + " was deleted.");
        } catch (JpaException e) {
            System.out.println("Failed to delete movie: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Movie> findMovieByTitle(String title) {
        try {
            return movieDAO.findByName(title);
        } catch (JpaException e) {
            System.out.println("Failed to find movie by title: " + e.getMessage());
            throw e;
        }
    }

    public List<MovieDTO> searchMoviesByTitle(String searchString) {
        try {
            List<Movie> movies = movieDAO.findByTitleContains(searchString);
            return movies.stream()
                .map(MovieDTO::new)
                .collect(Collectors.toList());
        } catch (JpaException e) {
            System.out.println("Failed to search movies by title: " + e.getMessage());
            throw e;
        }
    }

    // Get the total average rating of all movies
    public double getTotalAverageRating() {
        try {
            return movieDAO.getTotalAverageRating();
        } catch (JpaException e) {
            System.out.println("Failed to get total average rating: " + e.getMessage());
            throw e;
        }
    }

    // Get the top X highest rated movies
    public List<MovieDTO> getTopXHighestRatedMovies(int x) {
        try {
            return movieDAO.getTopXHighestRatedMovies(x).stream()
                .map(MovieDTO::new)
                .collect(Collectors.toList());
        } catch (JpaException e) {
            System.out.println("Failed to get top " + x + " highest rated movies: " + e.getMessage());
            throw e;
        }
    }

    // Get the top X lowest rated movies
    public List<MovieDTO> getTopXLowestRatedMovies(int x) {
        try {
            return movieDAO.getTopXLowestRatedMovies(x).stream()
                .map(MovieDTO::new)
                .collect(Collectors.toList());
        } catch (JpaException e) {
            System.out.println("Failed to get top " + x + " lowest rated movies: " + e.getMessage());
            throw e;
        }
    }




    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");
        MovieService movieService = new MovieService(emf);

//        // Create a new movieDTO
//        MovieDTO movieDTO = new MovieDTO();
//        movieDTO.setTitle("titel");
//        movieDTO.setEnglishTitle(null);
//        movieDTO.setReleaseDate(LocalDate.of(2024, 07, 14));
//        movieDTO.setVoteAverage(0.0);
       // movieDTO.setPopularity(3.4);
//        movieDTO.setGenres(new HashSet<>() {{
//            add(new GenreDTO("Drama"));
//            add(new GenreDTO("War"));
//        }});
//        movieDTO.setActors(null);
//        movieDTO.setDirector(null);
//
//
//        // Create the movie in the database
//        movieService.createMovie(movieDTO);
//
//        // Retrieve the movie by ID
//        Long movieId = 1L;
//        MovieDTO movieDTOFoundById = movieService.getMovieById(movieId);
//        System.out.println("Retrieved movie: " + movieDTOFoundById.buildMovieDetails());
//
//        // Retrieve all movies
//        List<MovieDTO> allMovies = movieService.getAllMovies();
//        for (MovieDTO allMoviesDTO : allMovies) {
//            System.out.println(allMoviesDTO.buildMovieDetails());
//            System.out.println("--------------------");
//        }
//
//        // Update the movie
//        Long movieIdToUpdate = 1158L;
//        MovieDTO movieDTOToUpdate = movieService.getMovieById(movieIdToUpdate);
//        System.out.println("Movie before update: " + movieDTOToUpdate.buildMovieDetails());
//        movieDTOToUpdate.setTitle("Updated Title");
//        movieDTOToUpdate.setEnglishTitle("Updated English Title");
//        movieDTOToUpdate.setReleaseDate(LocalDate.of(2024, 07, 14));
//        movieDTOToUpdate.setVoteAverage(7.0);
//        movieDTOToUpdate.setGenres(new HashSet<>() {{
//            add(new GenreDTO("Action"));
//            add(new GenreDTO("Adventure"));
//        }});
//        movieDTOToUpdate.setActors(new HashSet<>() {{
//            add(new ActorDTO("Actor 1"));
//            add(new ActorDTO("Actor 2"));
//        }});
//        movieDTOToUpdate.setDirector(null);
//        movieService.updateMovie(movieDTOToUpdate);
//
//        System.out.println("Updated movie: " + movieDTOToUpdate.buildMovieDetails());


        // Search movies with title "star"
        List<MovieDTO> searchResults = movieService.searchMoviesByTitle("star");
        for (MovieDTO movie : searchResults) {
            System.out.println("Found Movie: " + movie.getTitle());
        }

        // Get the total average rating of all movies
        double totalAverageRating = movieService.getTotalAverageRating();
        System.out.println("Total average rating of all movies: " + totalAverageRating);

        // Get the top 10 highest rated movies
        List<MovieDTO> top10HighestRatedMovies = movieService.getTopXHighestRatedMovies(10);
        for (MovieDTO movie : top10HighestRatedMovies) {
            System.out.println("Top 10 highest rated movie: " + movie.getTitle() + " - " + movie.getVoteAverage());
        }

        // Get the top 10 lowest rated movies
        List<MovieDTO> top10LowestRatedMovies = movieService.getTopXLowestRatedMovies(10);
        for (MovieDTO movie : top10LowestRatedMovies) {
            System.out.println("Top 10 lowest rated movie: " + movie.getTitle() + " - " + movie.getVoteAverage());
        }

    }
}
