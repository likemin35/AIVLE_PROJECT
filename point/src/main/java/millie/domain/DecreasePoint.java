package millie.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecreasePoint {
    private String eventType = "DecreasePoint";
    private Long userId;
    private Long bookId;
}