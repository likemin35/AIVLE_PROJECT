package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class PublishingRequested extends AbstractEvent {

    private Long bookId;
    private String title;
    private AuthorId authorId;
    private Status status;
    private String content;

    public PublishingRequested(Manuscript aggregate) {
        super(aggregate);
        this.bookId = aggregate.getBookId();
        this.title = aggregate.getTitle();
        this.authorId = aggregate.getAuthorId();
        this.status = aggregate.getStatus();
        this.content = aggregate.getContent();
        
    }

    public PublishingRequested() {
        super();
    }
}
//>>> DDD / Domain Event
