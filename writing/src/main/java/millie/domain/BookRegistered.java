package millie.domain;

import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

@Data
@ToString
public class BookRegistered extends AbstractEvent {

    private Long bookId;
    private String bookName;
    private String category;
    private Boolean isBestSeller;
    private String bookContent;
}
