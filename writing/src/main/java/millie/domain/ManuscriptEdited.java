package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class ManuscriptEdited extends AbstractEvent {

    private Long id;
    private String title;
    private String content;

    public ManuscriptEdited(Manuscript aggregate) {
        super(aggregate);
    }

    public ManuscriptEdited() {
        super();
    }
}
//>>> DDD / Domain Event
