package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class PointDecreased extends AbstractEvent {

    private Integer point;
    private Long userId;
    private Boolean isSubscription;

    // ✅ bookId 필드 추가
    private Long bookId;

    // ✅ eventType 필드 추가 (카프카 라우팅용)
    private String eventType = "PointDecreased";

    public PointDecreased(Point aggregate) {
        super(aggregate);
    }

    public PointDecreased() {
        super();
    }
}
// >>> DDD / Domain Event