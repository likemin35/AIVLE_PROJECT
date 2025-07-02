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

    // ✅ password 필드 추가
    private String password;

    // ✅ Point 서비스에서는 기본 생성자만 사용
    public UserRegistered() {
        super();
    }

    // ❌ User 클래스 참조하는 생성자는 제거 (이미 주석처리됨)
    // public UserRegistered(User aggregate) {
    // super(aggregate);
    // }
}
// >>> DDD / Domain Event