package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import dat.entities.Genre;
import lombok.*;

@Data
@NoArgsConstructor
public class GenreDTO {
    private Long id;
    private String genreName;

    // Constructor to convert Genre entity to GenreDTO
    public GenreDTO(Genre genre) {
        this.id = genre.getId();
        this.genreName = genre.getGenreName();
    }


}
