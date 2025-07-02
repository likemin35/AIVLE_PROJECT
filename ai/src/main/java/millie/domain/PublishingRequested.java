package millie.domain;

import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PublishingRequested extends AbstractEvent {

    private Long id;
    private String title;
    private Object authorId;
    private Object status;
    private String content;

    private String category;
    private String webUrl;
}
