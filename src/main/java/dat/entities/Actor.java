package dat.entities;

import dat.dtos.ActorDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data

@NoArgsConstructor
@ToString
@Entity

public class Actor {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies;

    public Actor(String name) {
        this.name = name;
    }

    public Actor (ActorDTO actorDTO) {
        this.id = actorDTO.getId();
        this.name = actorDTO.getName();
    }
}
