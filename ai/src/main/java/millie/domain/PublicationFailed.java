package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class PublicationFailed extends AbstractEvent {

    private Long id;
    private String image;
    private String summaryContent;
    private String bookName;
    private String pdfPath;
    private String authorId;
    private String webUrl;
    private String category;
    private ManuscriptId manuscriptId;
    private Integer point;

    public PublicationFailed(Publishing aggregate) {
        super(aggregate);
    }

    public PublicationFailed() {
        super();
    }
}
//>>> DDD / Domain Event
