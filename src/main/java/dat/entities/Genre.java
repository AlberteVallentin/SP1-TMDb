package dat.entities;

import dat.dtos.GenreDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data

@NoArgsConstructor
@ToString
@Entity

public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "genre_name", nullable = false, unique = true)
    private String genreName;


    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies;


    public Genre(String genreName) {
        this.genreName = genreName;
    }


}