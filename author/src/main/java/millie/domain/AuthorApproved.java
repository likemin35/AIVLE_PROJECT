package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class AuthorApproved extends AbstractEvent {

    private Long authorId;
    private Boolean isApprove;

    public AuthorApproved(Author aggregate) {
        super(aggregate);
    }

    public AuthorApproved() {
        super();
    }
}
//>>> DDD / Domain Event
