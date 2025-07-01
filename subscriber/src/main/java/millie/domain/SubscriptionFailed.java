package millie.domain;

import java.util.Date;
import lombok.*;
import millie.infra.AbstractEvent;

@Data
@ToString
public class SubscriptionFailed extends AbstractEvent {

    private Long id;
    private Long bookId;
    private Long userId;
    private Boolean isSubscription;
    private Date startSubscription;
    private Date endSubscription;
    private String message;

    public SubscriptionFailed(Subscription aggregate) {
        super(aggregate);
        this.id = aggregate.getId();
        this.bookId = aggregate.getBookId() != null ? aggregate.getBookId().getId() : null;
        this.userId = aggregate.getUserId() != null ? aggregate.getUserId().getId() : null;
        this.isSubscription = aggregate.getIsSubscription();
        this.startSubscription = aggregate.getRentalstart();
        this.endSubscription = aggregate.getRentalend();
    }

    public SubscriptionFailed() {
        super();
    }
}
