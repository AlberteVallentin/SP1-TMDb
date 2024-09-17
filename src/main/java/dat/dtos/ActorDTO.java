package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActorDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
}

