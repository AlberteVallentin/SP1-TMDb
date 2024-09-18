package dat.dtos;

import dat.entities.Actor;

public class ActorDTO {
    private Long id;
    private String name;

    public ActorDTO(Actor actor) {
        this.id = actor.getId();
        this.name = actor.getName();
    }

}
