package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class OutOfPoint extends AbstractEvent {

    private Long id;
    private Integer point;
    private UserId userId;
    private SubscriptionId subscriptionId;

    public OutOfPoint(Point aggregate) {
        super(aggregate);
    }

    public OutOfPoint() {
        super();
    }
}
//>>> DDD / Domain Event
