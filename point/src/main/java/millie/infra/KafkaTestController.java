package millie.infra;

import org.springframework.web.bind.annotation.*;
import millie.domain.UserRegistered;

@RestController
@RequestMapping("/test")
public class KafkaTestController {

    // ✅ 쿼리 파라미터로 유저 정보 전달 가능하게 수정
    @PostMapping("/register")
    public String sendTestEvent(
        @RequestParam Long userId,
        @RequestParam String userName,
        @RequestParam Boolean isKt
    ) {
        UserRegistered event = new UserRegistered();
        event.setUserId(userId);
        event.setUserName(userName);
        event.setIsKt(isKt);

        event.publish(); // Kafka 발행

        return "✅ Kafka 이벤트 전송 완료";
    }
}
