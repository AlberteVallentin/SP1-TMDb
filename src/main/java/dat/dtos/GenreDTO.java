package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
public class GenreDTO {
    private Long id;
    private String genreName;


}
