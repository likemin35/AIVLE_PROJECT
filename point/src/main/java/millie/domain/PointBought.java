package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class PointBought extends AbstractEvent {

    private Long pointId;
    private Integer point;
    private Long userId;

    public PointBought(Point aggregate) {
        super(aggregate);
    }

    public PointBought() {
        super();
    }
}
//>>> DDD / Domain Event
