package millie.domain;

import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

@Data
@ToString
public class PublicationFailed extends AbstractEvent {

    private Long bookId;
    private String image;
    private String summaryContent;
    private String bookName;
    private String pdfPath;
    private String authorId;
    private String webUrl;
    private String category;
    private Object manuscriptId;
    private Integer point;
}
