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

    public PointDecreased(Point aggregate) {
        super(aggregate);
    }

    public PointDecreased() {
        super();
    }
}
//>>> DDD / Domain Event
