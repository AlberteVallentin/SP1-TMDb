package dat.dtos;

import dat.entities.Director;

public class DirectorDTO {
    private Long id;
    private String name;

  public DirectorDTO(Director director) {
        this.id = director.getId();
        this.name = director.getName();
    }
}
