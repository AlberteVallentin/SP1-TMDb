package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenreDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String genreName;


}
