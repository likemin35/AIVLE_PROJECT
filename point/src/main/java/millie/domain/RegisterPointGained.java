package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class RegisterPointGained extends AbstractEvent {

    private Long id;
    private String 구독자정보;
    private String 포인트;
    private String 구독권여부;

    public RegisterPointGained(Point aggregate) {
        super(aggregate);
    }

    public RegisterPointGained() {
        super();
    }
}
//>>> DDD / Domain Event
