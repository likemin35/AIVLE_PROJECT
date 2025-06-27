package millie.domain;

import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

@Data
@ToString
public class Published extends AbstractEvent {

    private Long id;
    private Object manuscriptId;
    private String image;
    private String summaryContent;
    private String bookName;
    private String pdfPath;
    private String authorId;
    private String webUrl;
    private String category;
    private Integer point;
}
