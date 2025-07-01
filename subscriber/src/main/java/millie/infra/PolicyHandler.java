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
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
        System.out.println("Received Kafka message: " + eventString);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='OutOfPoint'"
    )
    public void wheneverOutOfPoint_FailSubscription(
        @Payload OutOfPoint outOfPoint
    ) {
        OutOfPoint event = outOfPoint;
        System.out.println(
            "\n\n##### listener FailSubscription : " + outOfPoint + "\n\n"
        );

        try {
            // Sample Logic //
            Subscription.failSubscription(event);
            System.out.println("FailSubscription processing completed successfully");
        } catch (Exception e) {
            System.err.println("Error processing OutOfPoint event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='SubscriptionFailed'"
    )
    public void wheneverSubscriptionFailed_GuideFeeConversionSuggestion(
        @Payload SubscriptionFailed subscriptionFailed
    ) {
        SubscriptionFailed event = subscriptionFailed;
        System.out.println(
            "\n\n##### listener GuideFeeConversionSuggestion : " +
            subscriptionFailed +
            "\n\n"
        );

        try {
            // Sample Logic //
            User.guideFeeConversionSuggestion(event);
            System.out.println("GuideFeeConversionSuggestion processing completed successfully");
        } catch (Exception e) {
            System.err.println("Error processing SubscriptionFailed event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
//>>> Clean Arch / Inbound Adaptor