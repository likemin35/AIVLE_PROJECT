package millie.domain;

import javax.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookId {
    private Long id;
}
