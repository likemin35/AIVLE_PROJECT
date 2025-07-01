package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class RegisterPointGained extends AbstractEvent {

    private Long pointId;
    private Long userId;
    private String point;
    private Boolean isPurchase;
    private Boolean isSubscription;

    public RegisterPointGained(Point aggregate) {
        super(aggregate);
    }

    public RegisterPointGained() {
        super();
    }
}
//>>> DDD / Domain Event
