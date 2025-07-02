package millie.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import millie.config.kafka.KafkaProcessor;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Map;
import millie.domain.Book;
//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    BookRepository bookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    // @StreamListener(
    //     value = KafkaProcessor.INPUT,
    //     // condition = "headers['type']=='SubscriptionApplied'"
    //     condition = "payload.eventType == 'SubscriptionApplied'"
    // )
    // public void wheneverSubscriptionApplied_GrantBadge(
    //     @Payload SubscriptionApplied subscriptionApplied
    // ) {
    //     SubscriptionApplied event = subscriptionApplied;
    //     System.out.println(
    //         "\n\n##### listener GrantBadge : " + subscriptionApplied + "\n\n"
    //     );
    //     // Sample Logic //
    //     // Published published = new Published();
    //     // published.setId((Long) event.getBookId());
    //     // published.setPdfPath(event.getPdfPath());
        
    //     Book.grantBestsellerBadge(event);
    // }

    // @StreamListener(
    //     value = KafkaProcessor.INPUT,
    //     condition = "headers['type']=='Published'"
    // )
    // public void wheneverPublished_RegisterBook(@Payload Published published) {
    //     Published event = published;
    //     System.out.println(
    //         "\n\n##### listener RegisterBook : " + published + "\n\n"
    //     );

    //     // Sample Logic //
    //     // Published published = new Published();
    //     // published.setId((Long) event.getBookId());
    //     Book.registerBook(event);
    // }

    @StreamListener(KafkaProcessor.INPUT)
    public void handleKafkaEvents(@Payload String eventString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, Object> event = mapper.readValue(eventString, Map.class);
            String eventType = (String) event.get("eventType");

            if (eventType == null) {
                System.out.println(">>> [경고] eventType 누락됨. 메시지 무시됨: " + eventString);
                return;
            }
            switch(eventType) {
                case "SubscriptionApplied": {
                    SubscriptionApplied subscriptionApplied = mapper.convertValue(event, SubscriptionApplied.class);
                    System.out.println("\n\n##### listener GrantBadge : " + subscriptionApplied + "\n\n");
                    Book.grantBestsellerBadge(subscriptionApplied);
                    break;
                }
                case "Published": {
                    Published published = mapper.convertValue(event, Published.class);
                    System.out.println("\n\n##### listener RegisterBook : " + published + "\n\n");
                    Book.registerBook(published);
                    break;
}
                default:
                    System.out.println(">>> [무시됨] 알 수 없는 이벤트 타입: " + eventType);
            }
        } catch (Exception e) {
            System.err.println(">>> [오류] 이벤트 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @StreamListener(value = KafkaProcessor.INPUT)
    public void debug(@Payload String message) {
        System.out.println(">>> 받은 메시지: " + message);
    }

}
//>>> Clean Arch / Inbound Adaptor
