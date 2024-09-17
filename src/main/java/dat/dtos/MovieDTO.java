package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import dat.entities.Movie;
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
        this.genres = movie.getGenres().stream().map(GenreDTO::new).collect(Collectors.toList());
        this.actors = movie.getActors().stream().map(ActorDTO::new).collect(Collectors.toList());
        this.director = new DirectorDTO(movie.getDirector());
    }
}


