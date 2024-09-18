package dat.dtos;


import java.time.LocalDate;
import java.util.List;

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
}


