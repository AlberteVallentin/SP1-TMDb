package dat.entities;

import dat.dtos.DirectorDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data

@NoArgsConstructor
@ToString
@Entity
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "director")
    private List<Movie> movies;

    public Director(DirectorDTO directorDTO) {
        this.name = directorDTO.getName();
    }
}
