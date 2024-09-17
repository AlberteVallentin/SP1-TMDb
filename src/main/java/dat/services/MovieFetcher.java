package dat.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MovieFetcher {

    private static final String API_KEY =  System.getenv("API_KEY");
    private static final String DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Step 1: Fetch Danish movies between 2019-09-10 and 2024-09-10
        List<Long> movieIds = fetchDanishMovies(client);

        // Step 2: For each movie ID, fetch details including actors, director, genre, and rating
        //fetchDetailsForMovies(client, movieIds);
    }

    // Step 1: Fetch all Danish movie IDs between 2019-09-17 and 2024-09-17
    private static List<Long> fetchDanishMovies(HttpClient client) throws Exception {
        String url = DISCOVER_URL + "?api_key=" + API_KEY +
            "&with_origin_country=DK" +
            "&release_date.gte=2019-09-17" +
            "&release_date.lte=2024-09-17" +
            "&sort_by=release_date.asc";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        // Parse the response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        JsonNode results = rootNode.get("results");

        List<Long> movieIds = new ArrayList<>();
        for (JsonNode movieNode : results) {
            long movieId = movieNode.get("id").asLong();
            movieIds.add(movieId);
        }

        return movieIds;
    }

    // Step 2: Fetch details for each movie ID, including actors, director, genre, and rating
    private static void fetchDetailsForMovies(HttpClient client, List<Long> movieIds) throws Exception {
        List<CompletableFuture<Void>> futures = movieIds.stream()
            .map(movieId -> fetchMovieDetails(client, movieId))
            .collect(Collectors.toList());

        // Wait for all async calls to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    // Fetch movie details, including actors, director, genre, and rating
    private static CompletableFuture<Void> fetchMovieDetails(HttpClient client, long movieId) {
        String url = MOVIE_DETAILS_URL + movieId + "?api_key=" + API_KEY + "&append_to_response=credits";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(responseBody -> {
                try {
                    // Parse movie details
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode movieDetails = mapper.readTree(responseBody);

                    String title = movieDetails.get("title").asText();
                    String releaseDate = movieDetails.get("release_date").asText();
                    double rating = movieDetails.get("vote_average").asDouble();

                    System.out.println("Title: " + title);
                    System.out.println("Release Date: " + releaseDate);
                    System.out.println("Rating: " + rating);

                    // Extract genres
                    JsonNode genres = movieDetails.get("genres");
                    List<String> genreList = new ArrayList<>();
                    for (JsonNode genre : genres) {
                        genreList.add(genre.get("name").asText());
                    }
                    System.out.println("Genres: " + String.join(", ", genreList));

                    // Extract actors
                    JsonNode cast = movieDetails.get("credits").get("cast");
                    List<String> actorList = new ArrayList<>();
                    for (JsonNode actor : cast) {
                        actorList.add(actor.get("name").asText());
                    }
                    System.out.println("Actors: " + String.join(", ", actorList));

                    // Extract director (from crew)
                    JsonNode crew = movieDetails.get("credits").get("crew");
                    for (JsonNode crewMember : crew) {
                        if ("Director".equals(crewMember.get("job").asText())) {
                            String director = crewMember.get("name").asText();
                            System.out.println("Director: " + director);
                            break;
                        }
                    }

                    System.out.println("=====================================");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }
}

