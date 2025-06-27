package millie.domain;

import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

@Data
@ToString
public class OutOfPoint extends AbstractEvent {

    private Long id;
    private Integer point;
    private Object userId;
    private Object subscriptionId;
}
