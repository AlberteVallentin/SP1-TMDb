package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor

public class MovieDTO {

    private Long id;


    private String title;


    private String englishTitle;

    private LocalDate releaseDate;

    private double voteAverage;


    private List<GenreDTO> genres;

    private List<ActorDTO> actors;

    private DirectorDTO director;





}
