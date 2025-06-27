package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class AuthorDisApproved extends AbstractEvent {

    private Boolean isApprove;

    public AuthorDisApproved(Author aggregate) {
        super(aggregate);
    }

    public AuthorDisApproved() {
        super();
    }
}
//>>> DDD / Domain Event
