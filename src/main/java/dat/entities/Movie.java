package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    private LocalDate releaseDate;
    private double voteAverage;

    @ManyToMany
    private List<Actor> actors;

    @ManyToOne
    private Director director;

    @ManyToMany
    private List<Genre> genres;



}
