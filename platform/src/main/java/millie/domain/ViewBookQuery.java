package millie.domain;

import java.util.Date;
import lombok.Data;

@Data
public class ViewBookQuery {

    private Long id;
    private String bookName;
    private String category;
    private Boolean isBestSeller;
    private String authorName;
    private Integer 조회수;
    private Integer point;
}
