package millie.infra;

import millie.domain.UserRegistered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class KafkaTestController {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/register")
    public String sendTestUserRegistered() {
        UserRegistered event = new UserRegistered();
        event.setId(1L);  // test userId
        event.setName("홍길동");
        event.setTelecom("KT"); // KT면 5000포인트 로직 확인 가능

        kafkaTemplate.send("millie", event);
        return "UserRegistered 이벤트 전송됨";
    }
}
