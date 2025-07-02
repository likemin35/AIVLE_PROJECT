package millie.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import millie.SubscriberApplication;
import millie.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    KafkaProcessor kafkaProcessor;

    @PostMapping
    public String publishEvent(@RequestBody Map<String, Object> event) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String payload = mapper.writeValueAsString(event);

            kafkaProcessor.outboundTopic().send(
                MessageBuilder.withPayload(payload)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .setHeader("type", event.get("eventType"))
                    .build()
            );

            return "이벤트가 성공적으로 발행되었습니다.";
        } catch (Exception e) {
            return "이벤트 발행 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}