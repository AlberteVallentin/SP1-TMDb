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
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "genre_name", nullable = false)
    private String genreName;

    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies;

    public Genre(GenreDTO genreDTO) {
        this.genreName = genreDTO.getGenreName();
    }
}
