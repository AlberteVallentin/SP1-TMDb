package dat.entities;

import dat.dtos.MovieDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data

@NoArgsConstructor
@ToString
@Entity

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    private String englishTitle;
    private LocalDate releaseDate;
    private double voteAverage;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "movie_genre",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "movie_actor",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "actor_id"))
    private Set<Actor> actors;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "director_id")
    private Director director;

//    public Movie(MovieDTO movieDTO) {
//        this.title = movieDTO.getTitle();
//        this.englishTitle = movieDTO.getEnglishTitle();
//        this.releaseDate = movieDTO.getReleaseDate();
//        this.voteAverage = movieDTO.getVoteAverage();
//        this.genres = movieDTO.getGenres()
//            .stream()
//            .map(Genre::new)
//            .collect(Collectors.toSet());
//        this.actors = movieDTO.getActors()
//            .stream()
//            .map(Actor::new)
//            .collect(Collectors.toSet());
//        if (movieDTO.getDirector() != null) {
//            this.director = new Director(movieDTO.getDirector());
//        } else {
//            this.director = null;
//        }
//    }
}

