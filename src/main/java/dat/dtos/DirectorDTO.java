package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import dat.entities.Director;
import lombok.*;

@Data
@NoArgsConstructor
public class DirectorDTO {
    private Long id;
    private String name;

    // Constructor to convert Director entity to DirectorDTO
    public DirectorDTO(Director director) {
        this.id = director.getId();
        this.name = director.getName();
    }
}
