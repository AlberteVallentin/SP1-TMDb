package dat.entities;

import dat.dtos.MovieDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;



@Data
@NoArgsConstructor
@ToString
@Entity

public class Movie {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    private String englishTitle;
    private LocalDate releaseDate;
    private double voteAverage;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")

    )
    private List<Genre> genres;


    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "director_id")
    private Director director;


    public Movie(String title, String englishTitle, LocalDate releaseDate, double voteAverage, List<Actor> actors, List<Genre> genres, Director director) {
        this.title = title;
        this.englishTitle = englishTitle;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.actors = actors;
        this.genres = genres;
        this.director = director;
    }

    public Movie (MovieDTO movieDTO) {
        this.id = movieDTO.getId();
        this.title = movieDTO.getTitle();
        this.englishTitle = movieDTO.getEnglishTitle();
        this.releaseDate = movieDTO.getReleaseDate();
        this.voteAverage = movieDTO.getVoteAverage();
        this.actors = movieDTO.getActors().stream().map(Actor::new).collect(Collectors.toList());
        this.genres = movieDTO.getGenres().stream().map(Genre::new).collect(Collectors.toList());
        this.director = new Director(movieDTO.getDirector());
    }
}
