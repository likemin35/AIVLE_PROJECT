package millie.domain;

import java.util.Date;
import lombok.Data;

@Data
public class GetPointQuery {

    private Long pointId;
    private Integer point;
    private Boolean isPurchase; // 구독권 구매 여부
    private Long userId;
    private Boolean isSubscription; // 책 구독 여부
}
