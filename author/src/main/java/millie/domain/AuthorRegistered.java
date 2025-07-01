package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class AuthorRegistered extends AbstractEvent {

    private Long authorId;
    private String email;
    private String authorName;
    private String introduction;
    private String feturedWorks;
    private List<Portfolio> portfolios;
    private Boolean isApprove;

    public AuthorRegistered(Author aggregate) {
        super(aggregate);
    }

    public AuthorRegistered() {
        super();
    }
}
//>>> DDD / Domain Event
