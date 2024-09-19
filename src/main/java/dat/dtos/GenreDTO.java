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


    public GenreDTO(String genreName) {
        this.genreName = genreName;
    }

    // Method to convert GenreDTO to Genre entity
    public Genre toEntity() {
        Genre genre = new Genre();
        genre.setId(this.id);
        genre.setGenreName(this.genreName);
        return genre;
    }


}
