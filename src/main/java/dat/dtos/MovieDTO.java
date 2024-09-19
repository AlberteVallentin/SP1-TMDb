package dat.dtos;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import dat.entities.Director;
import dat.entities.Movie;
import dat.entities.Actor;
import dat.entities.Genre;
import lombok.*;

@Data
@NoArgsConstructor

public class MovieDTO {
    private Long id;
    private String title;
    private String englishTitle;
    private LocalDate releaseDate;
    private double voteAverage;

    private List<GenreDTO> genres;
    private List<ActorDTO> actors;
    private DirectorDTO director;

    // Constructor to convert Movie entity to MovieDTO
    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.englishTitle = movie.getEnglishTitle();
        this.releaseDate = movie.getReleaseDate();
        this.voteAverage = movie.getVoteAverage();

        // Convert Genre, Actor, Director to DTOs
        this.genres = movie.getGenres()
            .stream()
            .map(GenreDTO::new)
            .toList();
        this.actors = movie.getActors()
            .stream()
            .map(ActorDTO::new)
            .toList();

        this.director = new DirectorDTO
            (movie.getDirector());
    }

    // Method to convert MovieDTO to Movie entity
    public Movie toEntity() {
        Movie movie = new Movie();
        movie.setId(this.id);
        movie.setTitle(this.title);
        movie.setEnglishTitle(this.englishTitle);
        movie.setReleaseDate(this.releaseDate);
        movie.setVoteAverage(this.voteAverage);

        if (this.genres != null) {
            movie.setGenres(this.genres.stream().map(Genre::new).collect(Collectors.toSet()));
        }
        if (this.actors != null) {
            movie.setActors(this.actors.stream().map(Actor::new).collect(Collectors.toSet()));
        }
        if (this.director != null) {
            movie.setDirector(new Director(this.director));
        }

        return movie;
    }
}


