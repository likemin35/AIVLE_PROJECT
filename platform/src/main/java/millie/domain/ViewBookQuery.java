package millie.domain;

import java.util.Date;
import lombok.Data;

@Data
public class ViewBookQuery {

    private Long id;
    private String bookName;
    private String category;
    private String summaryContent;
    private Boolean isBestSeller;
    private String authorName;
    private Integer viewCount;
    private Integer point;
}
