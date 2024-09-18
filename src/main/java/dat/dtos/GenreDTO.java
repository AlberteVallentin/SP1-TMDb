package dat.dtos;

import dat.entities.Genre;

public class GenreDTO {
    private Long id;
    private String genreName;

    public GenreDTO(Genre genre) {
        this.id = genre.getId();
        this.genreName = genre.getGenreName();
    }

}
