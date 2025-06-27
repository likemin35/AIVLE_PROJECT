package millie.infra;

import lombok.Data;

@Data
public class PurchasePointRequest {
    private Long userId;
    private Long subscriptionId;
    private Integer amount; // 충전할 포인트
}
