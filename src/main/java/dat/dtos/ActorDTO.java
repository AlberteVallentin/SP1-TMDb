package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import dat.entities.Actor;
import lombok.*;

@Data
@NoArgsConstructor
public class ActorDTO {
    private Long id;
    private String name;

    // Constructor to convert Actor entity to ActorDTO
    public ActorDTO(Actor actor) {
        this.id = actor.getId();
        this.name = actor.getName();
    }

    public ActorDTO(String name) {
        this.name = name;
    }

    // Method to convert ActorDTO to Actor entity
    public Actor toEntity() {
        Actor actor = new Actor();
        actor.setId(this.id);
        actor.setName(this.name);
        return actor;
    }
}

