package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class SubscriptionCanceled extends AbstractEvent {

    private Long id;
    private Boolean isPurchase;

    public SubscriptionCanceled(User aggregate) {
        super(aggregate);
    }

    public SubscriptionCanceled() {
        super();
    }
}
//>>> DDD / Domain Event
