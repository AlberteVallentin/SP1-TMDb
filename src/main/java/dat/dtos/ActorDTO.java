package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
public class ActorDTO {
    private Long id;
    private String name;
}

