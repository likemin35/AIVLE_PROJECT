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
    private Long userId;
    private String email;
    private String userName;
    private String phoneNumber;
    private Boolean isKt;

    // ✅ Point 서비스에서는 기본 생성자만 사용
    public UserRegistered() {
        super();
    }
    
    // ❌ User 클래스 참조하는 생성자는 제거
    // public UserRegistered(User aggregate) {
    //     super(aggregate);
    // }
}
//>>> DDD / Domain Event
