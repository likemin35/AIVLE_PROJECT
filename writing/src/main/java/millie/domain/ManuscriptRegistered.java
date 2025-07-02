package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class ManuscriptRegistered extends AbstractEvent {

    private Long bookId;
    private String title;
    private String content;
    private Long authorId;   // AuthorId -> Long 변경
    private boolean isApprove;
    private Status status;

    public ManuscriptRegistered(Manuscript aggregate) {
        super(aggregate);
        this.bookId = aggregate.getBookId();
        this.title = aggregate.getTitle();
        this.content = aggregate.getContent();
        this.authorId = aggregate.getAuthorId().getId(); // AuthorId 객체에서 id만 꺼내서 저장
        this.isApprove = aggregate.getAuthorId().isApprove();  
        this.status = aggregate.getStatus();
    }

    public ManuscriptRegistered() {
        super();
    }
}

