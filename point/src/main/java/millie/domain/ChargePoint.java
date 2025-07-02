package millie.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargePoint {
    private String eventType = "ChargePoint";
    private Long userId;
    private Integer amount;
}