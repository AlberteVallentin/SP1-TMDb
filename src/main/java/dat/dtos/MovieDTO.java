// MovieDTO.java
package dat.dtos;

import dat.entities.Movie;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MovieDTO {
    private Long id;
    private String title;
    private String englishTitle;
    private LocalDate releaseDate;
    private double voteAverage;

    private Set<GenreDTO> genres;
    private Set<ActorDTO> actors;
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
            .collect(Collectors.toSet());
        this.actors = movie.getActors()
            .stream()
            .map(ActorDTO::new)
            .collect(Collectors.toSet());

        this.director = new DirectorDTO(movie.getDirector());
    }

    public MovieDTO(String title, String englishTitle, LocalDate releaseDate, double voteAverage, DirectorDTO director, Set<GenreDTO> genres, Set<ActorDTO> actors) {
        this.title = title;
        this.englishTitle = englishTitle;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.director = director;
        this.genres = genres;
        this.actors = actors;
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
            movie.setGenres(this.genres.stream().map(GenreDTO::toEntity).collect(Collectors.toSet()));
        }
        if (this.actors != null) {
            movie.setActors(this.actors.stream().map(ActorDTO::toEntity).collect(Collectors.toSet()));
        }
        if (this.director != null) {
            movie.setDirector(this.director.toEntity());
        }

        return movie;
    }
}



