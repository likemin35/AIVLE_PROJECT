package millie.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;

import millie.PointApplication;
import millie.config.kafka.KafkaProcessor;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Map;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    PointRepository pointRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='UserRegistered'")
    public void wheneverUserRegistered_GainRegisterPoint(
            @Payload UserRegistered userRegistered) {
        UserRegistered event = userRegistered;
        System.out.println(
                "\n\n##### listener GainRegisterPoint : " + userRegistered + "\n\n");

        // Sample Logic //
        Point.gainRegisterPoint(event);
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='SubscriptionApplied'")
    public void wheneverSubscriptionApplied_DecreasePoint(
            @Payload SubscriptionApplied subscriptionApplied) {
        SubscriptionApplied event = subscriptionApplied;
        System.out.println(
                "\n\n##### listener DecreasePoint : " + subscriptionApplied + "\n\n");

        // Sample Logic //
        Point.decreasePoint(event);
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='DecreasePoint'")
    public void wheneverDecreasePoint_HandleDecreasePoint(
            @Payload String eventString) {
        System.out.println(">>> [DEBUG] DecreasePoint 핸들러 진입");

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Map<String, Object> event = mapper.readValue(eventString, Map.class);

            Long userId = Long.valueOf(event.get("userId").toString());
            Long bookId = Long.valueOf(event.get("bookId").toString());

            System.out.println(">>> [수신] DecreasePoint 이벤트: userId=" + userId + ", bookId=" + bookId);

            // 포인트 차감 처리
            SubscriptionApplied subscriptionEvent = new SubscriptionApplied();
            subscriptionEvent.setUserId(userId);
            subscriptionEvent.setBookId(bookId);
            subscriptionEvent.setIsSubscription(false);

            Point.decreasePoint(subscriptionEvent);

            // ✅ 포인트 차감 성공 후 PointDecreased 이벤트 발행
            Map<String, Object> decreasedEvent = Map.of(
                    "eventType", "PointDecreased",
                    "userId", userId,
                    "bookId", bookId,
                    "decreasedAmount", 100);

            ObjectMapper outMapper = new ObjectMapper();
            String payload = outMapper.writeValueAsString(decreasedEvent);

            KafkaProcessor processor = PointApplication.applicationContext.getBean(KafkaProcessor.class);
            processor.outboundTopic().send(
                    org.springframework.messaging.support.MessageBuilder
                            .withPayload(payload)
                            .setHeader("type", "PointDecreased")
                            .build());

            System.out.println(">>> PointDecreased 이벤트 발행 완료: userId=" + userId);

        } catch (Exception e) {
            System.err.println(">>> DecreasePoint 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
// >>> Clean Arch / Inbound Adaptor