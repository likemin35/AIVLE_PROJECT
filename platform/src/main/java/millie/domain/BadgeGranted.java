package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class BadgeGranted extends AbstractEvent {

    private Long id;
    private String bookName;
    private Boolean isBestSeller;
    private Integer subscriptionCount;
    private String views;

    public BadgeGranted(Book aggregate) {
        super(aggregate);
    }

    public BadgeGranted() {
        super();
    }
}
//>>> DDD / Domain Event
