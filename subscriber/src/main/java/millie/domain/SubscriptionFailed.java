package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class SubscriptionFailed extends AbstractEvent {

    private Long id;
    private Boolean isSubscription;
    private BookId bookId;
    private UserId userId;
    private Date startSubscription;
    private Date endSubscription;

    public SubscriptionFailed(Subscription aggregate) {
        super(aggregate);
    }

    public SubscriptionFailed() {
        super();
    }
}
//>>> DDD / Domain Event
