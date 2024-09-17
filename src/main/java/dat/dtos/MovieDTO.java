package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class MovieDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String title;
    private List<ActorDTO> actors;
    private List<DirectorDTO> directors;
    private List<GenreDTO> genres;
    private LocalDate releaseDate;
}
