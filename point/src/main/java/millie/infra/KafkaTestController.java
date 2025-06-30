package millie.infra;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import millie.domain.UserRegistered;


@RestController
@RequestMapping("/test")
public class KafkaTestController {

    @PostMapping("/register")
    public String sendTestEvent() {
        UserRegistered event = new UserRegistered();
        //event.setPointId(999L);
        event.setUserName("테스터");
        event.setTelecom("KT");

        event.publish(); //  AbstractEvent에서 제공하는 메서드

        return "✅ Kafka 이벤트 전송 완료 (AbstractEvent 사용)";
    }
}