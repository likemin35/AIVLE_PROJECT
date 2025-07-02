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

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    BookRepository bookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='SubscriptionApplied'"
    )
    public void wheneverSubscriptionApplied_GrantBadge(
        @Payload SubscriptionApplied subscriptionApplied
    ) {
        SubscriptionApplied event = subscriptionApplied;
        System.out.println(
            "\n\n##### listener GrantBadge : " + subscriptionApplied + "\n\n"
        );
        // Sample Logic //
        Published published = new Published();
        published.setId((Long) event.getBookId());
        published.setPdfPath(event.getPdfPath());
        
        Book.registerBook(published);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='Published'"
    )
    public void wheneverPublished_RegisterBook(@Payload Published published) {
        Published event = published;
        System.out.println(
            "\n\n##### listener RegisterBook : " + published + "\n\n"
        );

        // Sample Logic //
        Book.registerBook(event);
    }

    @StreamListener(value = KafkaProcessor.INPUT)
    public void debug(@Payload String message) {
        System.out.println(">>> 받은 메시지: " + message);
    }

}
//>>> Clean Arch / Inbound Adaptor
