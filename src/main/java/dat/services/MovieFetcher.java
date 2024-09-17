package dat.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat.dtos.ActorDTO;
import dat.dtos.DirectorDTO;
import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;

public class MovieFetcher {

    private static final String API_KEY = System.getenv("API_KEY");
    private static final String DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Step 1: Fetch Danish movies between 2019-09-10 and 2024-09-10
        List<Long> movieIds = fetchDanishMovies(client);
        System.out.println("Movie IDs: " + movieIds);
        System.out.println("the last 20 movie id's: " + movieIds.subList(movieIds.size() - 20, movieIds.size()));
        System.out.println("Movie IDs size: " + movieIds.size());


        // Step 2: For each movie ID, fetch details including actors, director, genre, and rating
        fetchDetailsForMovies(client, movieIds);
        System.out.println("number of fetched movies: " + movieIds.size());


    }

    // Fetch all Danish movie ID's between 2019-09-17 and 2024-09-17
    private static List<Long> fetchDanishMovies(HttpClient client) throws Exception {
        List<Long> movieIds = new ArrayList<>();
        int page = 1;
        int totalPages;

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
                movieIds.add(movieId);
            }
            page++;

        } while (page <= totalPages);

        return movieIds;
    }


    // Fetch details for each movie ID, including actors, director, genre, and vote average

    private static void fetchDetailsForMovies(HttpClient client, List<Long> movieIds) throws Exception {
        // Limit concurrent requests to 10
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<CompletableFuture<Void>> futures = movieIds.stream()
            .map(movieId -> CompletableFuture.runAsync(() -> {
                try {
                    fetchMovieDetails(client, movieId).join();
                    // Sleep for 100ms to avoid rate limiting and make sure we get all responses
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executor))
            .collect(Collectors.toList());

        // Wait for all async calls to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
    }


    // MovieFetcher.java
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
}