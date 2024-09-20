// MovieFetcher.java
package dat.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.HibernateConfig;
import dat.dtos.ActorDTO;
import dat.dtos.DirectorDTO;
import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManagerFactory;

public class MovieFetcher {

    private static final String API_KEY = System.getenv("API_KEY");
    private static final String DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Step 1: Fetch Danish movies between 2019-09-10 and 2024-09-10
        Set<Long> movieIds = fetchDanishMovies(client);

        // Step 2: Fetch details for each movie ID and return them as a set of MovieDTOs
        Set<MovieDTO> movieDTOSet = fetchDetailsForMovies(client, movieIds);

        // Step 3: Convert and save these DTO's to entities and persist them to the database
        saveMoviesToDatabase(movieDTOSet);
    }

    // Step 1: Fetch all Danish movie ID's between 2019-09-17 and 2024-09-17
    private static Set<Long> fetchDanishMovies(HttpClient client) throws Exception {
        Set<Long> movieIds = new HashSet<>();
        int page = 1;
        int totalPages;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        do {
            String url = DISCOVER_URL + "?api_key=" + API_KEY +
                "&with_origin_country=DK" +
                "&release_date.gte=2019-09-17" +
                "&release_date.lte=2024-09-17" +
                "&sort_by=release_date.asc" +
                "&page=" + page;

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // Parse the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);
            JsonNode results = rootNode.get("results");
            totalPages = rootNode.get("total_pages").asInt();

            // Check if results is not null
            if (results == null) {
                System.err.println("Error: 'results' field is missing in the response.");
                break;
            }

            for (JsonNode movieNode : results) {
                long movieId = movieNode.get("id").asLong();
                JsonNode releaseDateNode = movieNode.get("release_date");

                // Check if release_date is not null or empty
                if (releaseDateNode != null && !releaseDateNode.asText().isEmpty()) {
                    String releaseDateStr = releaseDateNode.asText();
                    LocalDate releaseDate = LocalDate.parse(releaseDateStr, formatter);

                    // Filter movies by release date
                    if (!releaseDate.isBefore(LocalDate.of(2019, 9, 17)) && !releaseDate.isAfter(LocalDate.of(2024, 9, 17))) {
                        movieIds.add(movieId);
                    }
                } else {
                    System.err.println("Warning: 'release_date' is empty or null for movie ID: " + movieId);
                }
            }
            page++;

        } while (page <= totalPages);

        return movieIds;
    }

    // Step 2: Fetch details for each movie ID, including actors, director, genre, and vote average
    private static Set<MovieDTO> fetchDetailsForMovies(HttpClient client, Set<Long> movieIds) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Fetch MovieDTOs for each movie ID
        Set<CompletableFuture<MovieDTO>> futures = movieIds.stream()
            .map(movieId -> CompletableFuture.supplyAsync(() -> {
                try {
                    return fetchMovieDetails(client, movieId).join();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }, executor))
            .collect(Collectors.toSet());

        // Wait for all async calls to complete and collect the results
        Set<MovieDTO> movieDTOs = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toSet());

        executor.shutdown();

        return movieDTOs; // Return the set of MovieDTOs
    }

    // Fetch movie details and convert them to MovieDTO
    private static CompletableFuture<MovieDTO> fetchMovieDetails(HttpClient client, long movieId) {
        String url = MOVIE_DETAILS_URL + movieId + "?api_key=" + API_KEY + "&append_to_response=credits";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenApply(responseBody -> {
                try {
                    // Parse movie details
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode movieDetails = mapper.readTree(responseBody);

                    // Create MovieDTO object
                    MovieDTO movieDTO = new MovieDTO();
                    movieDTO.setTitle(movieDetails.get("original_title").asText());
                    movieDTO.setEnglishTitle(movieDetails.get("title").asText());


                    // Check if release_date is not null or empty
                    JsonNode releaseDateNode = movieDetails.get("release_date");
                    if (releaseDateNode != null && !releaseDateNode.asText().isEmpty()) {
                        movieDTO.setReleaseDate(LocalDate.parse(releaseDateNode.asText()));
                    } else {
                        movieDTO.setReleaseDate(null); // Allow null value
                        System.err.println("Warning: 'release_date' is empty or null for movie ID: " + movieId);
                    }

                    movieDTO.setVoteAverage(movieDetails.get("vote_average").asDouble());
                    movieDTO.setPopularity(movieDetails.get("popularity").asDouble());

                    // Extract genres
                    JsonNode genres = movieDetails.get("genres");
                    Set<GenreDTO> genreSet = new HashSet<>();
                    for (JsonNode genre : genres) {
                        GenreDTO genreDTO = new GenreDTO();
                        genreDTO.setGenreName(genre.get("name").asText());
                        genreSet.add(genreDTO);
                    }
                    movieDTO.setGenres(genreSet);

                    // Extract actors
                    JsonNode cast = movieDetails.get("credits").get("cast");
                    Set<ActorDTO> actorSet = new HashSet<>();
                    for (JsonNode actor : cast) {
                        ActorDTO actorDTO = new ActorDTO();
                        actorDTO.setName(actor.get("original_name").asText());
                        actorSet.add(actorDTO);
                    }
                    movieDTO.setActors(actorSet);

                    // Extract director
                    JsonNode crew = movieDetails.get("credits").get("crew");
                    for (JsonNode crewMember : crew) {
                        if ("Director".equals(crewMember.get("job").asText())) {
                            DirectorDTO directorDTO = new DirectorDTO();
                            directorDTO.setName(crewMember.get("name").asText());
                            movieDTO.setDirector(directorDTO);
                            break;
                        }
                    }

                    return movieDTO;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            });
    }

    // Step 3: Save the movies, actors, directors, and genres to the database
    private static void saveMoviesToDatabase(Set<MovieDTO> movieDTOSet) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            for (MovieDTO movieDTO : movieDTOSet) {
                // Convert DTOs to entities
                Movie movie = movieDTO.toEntity();

                // Check if actors exist, if not persist them
                Set<Actor> actorsToAdd = new HashSet<>();
                for (Iterator<Actor> iterator = movie.getActors().iterator(); iterator.hasNext(); ) {
                    Actor actor = iterator.next();
                    Actor existingActor = em.createQuery("SELECT a FROM Actor a WHERE a.name = :name", Actor.class)
                        .setParameter("name", actor.getName())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                    if (existingActor == null) {
                        em.persist(actor);
                    } else {
                        iterator.remove();
                        actorsToAdd.add(existingActor);
                    }
                }
                movie.getActors().addAll(actorsToAdd);

                // Check if director exists, if not persist them
                if (movie.getDirector() != null) {
                    Director existingDirector = em.createQuery("SELECT d FROM Director d WHERE d.name = :name", Director.class)
                        .setParameter("name", movie.getDirector().getName())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                    if (existingDirector == null) {
                        em.persist(movie.getDirector());
                    } else {
                        movie.setDirector(existingDirector);
                    }
                }

                // Check if genres exist, if not persist them
                Set<Genre> genresToAdd = new HashSet<>();
                for (Iterator<Genre> iterator = movie.getGenres().iterator(); iterator.hasNext(); ) {
                    Genre genre = iterator.next();
                    Genre existingGenre = em.createQuery("SELECT g FROM Genre g WHERE g.genreName = :genreName", Genre.class)
                        .setParameter("genreName", genre.getGenreName())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                    if (existingGenre == null) {
                        em.persist(genre);
                    } else {
                        iterator.remove();
                        genresToAdd.add(existingGenre);
                    }
                }
                movie.getGenres().addAll(genresToAdd);

                // Save or update movie
                em.persist(movie);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}