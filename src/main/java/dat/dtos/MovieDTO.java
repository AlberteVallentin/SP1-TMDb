package dat.dtos;

import java.time.LocalDate;
import java.util.List;

public class MovieDTO {

    private Long id;
    private String title;
    private List<ActorDTO> actors;
    private List<DirectorDTO> directors;
    private List<GenreDTO> genres;
    private LocalDate releaseDate;
}
