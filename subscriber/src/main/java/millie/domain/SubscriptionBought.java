package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class SubscriptionBought extends AbstractEvent {

    private Long id;
    private Boolean isPurchase;

    public SubscriptionBought(User aggregate) {
        super(aggregate);
    }

    public SubscriptionBought() {
        super();
    }
}
//>>> DDD / Domain Event
