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
    private UserId userId;

    public SubscriptionBought(User aggregate) {
        super(aggregate);
        this.id = aggregate.getId();
        this.isPurchase = aggregate.getIsPurchase();
        this.userId = aggregate.getUserId();
    }

    public SubscriptionBought() {
        super();
    }
}
//>>> DDD / Domain Event
