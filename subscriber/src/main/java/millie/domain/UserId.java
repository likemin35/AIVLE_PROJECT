package millie.domain;

import javax.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
public class UserId {
    private Long id;
}