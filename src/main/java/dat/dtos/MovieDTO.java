package dat.dtos;

import dat.entities.Movie;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.englishTitle = movie.getEnglishTitle();
        this.releaseDate = movie.getReleaseDate();
        this.voteAverage = movie.getVoteAverage();

        this.genres = movie.getGenres().stream().map(GenreDTO::new).toList();
        this.actors = movie.getActors().stream().map(ActorDTO::new).toList();
        this.director = new DirectorDTO(movie.getDirector());

    }


}
