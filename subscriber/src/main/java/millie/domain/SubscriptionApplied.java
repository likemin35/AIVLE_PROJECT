package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class SubscriptionApplied extends AbstractEvent {

    private Long id;
    private BookId bookId;
    private UserId userId;
    private Boolean isSubscription;
    private Date startSubscription;
    private Date endSubscription;
    private String pdfPath;

    public SubscriptionApplied(Subscription aggregate) {
        super(aggregate);
    }

    public SubscriptionApplied() {
        super();
    }
}
//>>> DDD / Domain Event
