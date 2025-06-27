package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class Published extends AbstractEvent {

    private Long id;
    private ManuscriptId manuscriptId;
    private String image;
    private String summaryContent;
    private String bookName;
    private String pdfPath;
    private String authorId;
    private String webUrl;
    private String category;
    private Integer point;

    public Published(Publishing aggregate) {
        super(aggregate);
    }

    public Published() {
        super();
    }
}
//>>> DDD / Domain Event
