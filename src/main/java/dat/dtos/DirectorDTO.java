package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DirectorDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
}
