package dat.dtos;

import dat.entities.Actor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActorDTO {
    private Long id;
    private String name;

    public ActorDTO(Actor actor) {
        this.id = actor.getId();
        this.name = actor.getName();
    }

}
