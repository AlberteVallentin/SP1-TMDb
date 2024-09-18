package dat.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
        List<Long> movieIds = fetchDanishMovies(client);
      //  System.out.println("Movie IDs: " + movieIds);
     //   System.out.println("the last 20 movie id's: " + movieIds.subList(movieIds.size() - 20, movieIds.size()));
      //  System.out.println("Movie IDs size: " + movieIds.size());

        // Step 2: Fetch details for each movie ID and return them as a list of MovieDTOs
        List<MovieDTO> movieDTOList = fetchDetailsForMovies(client, movieIds);
       // System.out.println("Fetched Movies: " + movieDTOList);

        // Step 3: Convert and save these DTOs to entities and persist them to the database
        saveMoviesToDatabase(movieDTOList);
    }

    // Step 1: Fetch all Danish movie IDs between 2019-09-17 and 2024-09-17
    private static List<Long> fetchDanishMovies(HttpClient client) throws Exception {
        List<Long> movieIds = new ArrayList<>();
        int page = 1;
        int totalPage;

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
            totalPage = rootNode.get("total_pages").asInt();


            if (results == null) {
                System.out.println("No movies found");
                break;
            }

            for (JsonNode movieNode : results) {
                long movieId = movieNode.get("id").asLong();
                JsonNode releaseDateNode = movieNode.get("release_date");

                //check if realease date is null or empty

                if (releaseDateNode != null && !releaseDateNode.asText().isEmpty()) {
                    String releaseDateStr = releaseDateNode.asText();
                    LocalDate releasedate = LocalDate.parse(releaseDateStr, formatter);

                    //filter movies by realase date
                    if (!releasedate.isBefore(LocalDate.parse("2019-09-17", formatter)) && !releasedate.isAfter(LocalDate.parse("2024-09-17", formatter))) {
                        movieIds.add(movieId);
                    } else {
                        System.out.println("Movie with id: " + movieId + " is not between 2019-09-17 and 2024-09-17");
                    }

                }

            }
            page++;
        } while (page <= totalPage);

        return movieIds;
    }

    // Step 2: Fetch details for each movie ID, including actors, director, genre, and rating
    private static List<MovieDTO> fetchDetailsForMovies(HttpClient client, List<Long> movieIds) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(10);


        List<CompletableFuture<MovieDTO>> futures = movieIds.stream()
                .map(movieId -> CompletableFuture.supplyAsync(() -> {

                    try {
                        return fetchMovieDetails(client, movieId).join(); // Assuming fetchMovieDetails returns CompletableFuture<MovieDTO>

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null; // Handle the exception properly by returning a default value or null
                    }
                }, executor))
                .collect(Collectors.toList());


        // Wait for all async calls to complete and collect the results
        List<MovieDTO> movieDTOs = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        executor.shutdown();

        return movieDTOs; // Return the list of MovieDTOs
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

                        // Extract genres
                        JsonNode genres = movieDetails.get("genres");
                        List<GenreDTO> genreList = new ArrayList<>();
                        for (JsonNode genre : genres) {
                            GenreDTO genreDTO = new GenreDTO();
                            genreDTO.setGenreName(genre.get("name").asText());
                            genreList.add(genreDTO);
                        }
                        movieDTO.setGenres(genreList);

                        // Extract actors
                        JsonNode cast = movieDetails.get("credits").get("cast");
                        List<ActorDTO> actorList = new ArrayList<>();
                        for (JsonNode actor : cast) {
                            ActorDTO actorDTO = new ActorDTO();
                            actorDTO.setName(actor.get("original_name").asText());
                            actorList.add(actorDTO);
                        }
                        movieDTO.setActors(actorList);

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
    private static void saveMoviesToDatabase(List<MovieDTO> movieDTOList) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_db");
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            for (MovieDTO movieDTO : movieDTOList) {
                // Convert DTOs to entities
                Movie movie = new Movie(movieDTO);

                // Check if actors exist, if not persist them
                List<Actor> actorsToAdd = new ArrayList<>();
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
                List<Genre> genresToAdd = new ArrayList<>();
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

