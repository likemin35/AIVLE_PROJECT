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
    private Boolean isKt;

    // ✅ 비밀번호 필드 추가
    private String password;

    public UserRegistered(User aggregate) {
        super(aggregate);
        this.id = aggregate.getId();
        this.email = aggregate.getEmail();
        this.userName = aggregate.getUserName();
        this.phoneNumber = aggregate.getPhoneNumber();
        this.isKt = aggregate.getIsKt();
        this.password = aggregate.getPassword(); // ✅ 비밀번호 포함
    }

    public UserRegistered() {
        super();
    }

    // ✅ 유효성 검증 메서드 추가
    public boolean validate() {
        return email != null && !email.isEmpty() &&
                userName != null && !userName.isEmpty() &&
                password != null && !password.isEmpty();
    }
}
// >>> DDD / Domain Event