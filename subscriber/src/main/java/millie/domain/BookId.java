package millie.domain;

import javax.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
public class BookId {
    private Long id;
}
