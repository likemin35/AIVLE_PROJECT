package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class UserRegistered extends AbstractEvent {

    private Long id;
    private String email;
    private String userName;
    private String phoneNumber;

    public UserRegistered(User aggregate) {
        super(aggregate);
        this.id = aggregate.getId();
        this.email = aggregate.getEmail();
        this.userName = aggregate.getUserName();
        this.phoneNumber = aggregate.getPhoneNumber();
    }

    public UserRegistered() {
        super();
    }
}
//>>> DDD / Domain Event
