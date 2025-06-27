package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class BookRegistered extends AbstractEvent {

    private Long id;
    private String bookName;
    private String category;
    private Boolean isBestSeller;
    private String bookContent;

    public BookRegistered(Book aggregate) {
        super(aggregate);
    }

    public BookRegistered() {
        super();
    }
}
//>>> DDD / Domain Event
