    package millie.infra;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import millie.SubscriberApplication;
    import millie.config.kafka.KafkaProcessor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.messaging.support.MessageBuilder;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;

    @RestController
    public class EventTestController {

        @Autowired
        KafkaProcessor kafkaProcessor;

        @PostMapping("/events")
        public void receiveEventFromPostman(@RequestBody Map<String, Object> event) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(event);

                // Kafka 토픽으로 직접 메시지 발행
                kafkaProcessor.inboundTopic().send(
                        MessageBuilder.withPayload(json).build());

                System.out.println(">>> [테스트] Postman 이벤트 수신 완료 → Kafka 발행 성공");
            } catch (Exception e) {
                System.out.println(">>> [오류] Postman 이벤트 처리 실패:");
                e.printStackTrace();
            }
        }
    }
