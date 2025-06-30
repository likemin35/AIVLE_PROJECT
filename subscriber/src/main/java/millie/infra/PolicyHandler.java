package millie.infra;

import java.util.Optional;
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
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='OutOfPoint'")
    public void wheneverOutOfPoint_FailSubscription(
            @Payload OutOfPoint outOfPoint) {
        OutOfPoint event = outOfPoint;
        System.out.println(
                "\n\n##### listener FailSubscription : " + outOfPoint + "\n\n");

        Subscription.failSubscription(event);
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='SubscriptionFailed'")
    public void wheneverSubscriptionFailed_GuideFeeConversionSuggestion(
            @Payload SubscriptionFailed subscriptionFailed) {
        SubscriptionFailed event = subscriptionFailed;
        System.out.println(
                "\n\n##### listener GuideFeeConversionSuggestion : " +
                        subscriptionFailed +
                        "\n\n");

        User.guideFeeConversionSuggestion(event);

        // BuySubscriptionCommand command = new BuySubscriptionCommand();
        // command.setUserId(subscriptionFailed.getUserId());
        // User.buySubscription(command);
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='SubscriptionCanceled'")
    public void wheneverSubscriptionCanceled_CancelSubscription(@Payload SubscriptionCanceled subscriptionCanceled) {
        if (!subscriptionCanceled.validate())
            return;

        System.out.println("\n##### listener CancelSubscription : " + subscriptionCanceled.toJson());

        Optional<Subscription> subscriptionOptional = subscriptionRepository
                .findByUserId(subscriptionCanceled.getUserId());

        if (subscriptionOptional.isPresent()) {
            Subscription subscription = subscriptionOptional.get();
            subscription.setIsSubscription(false); // 구독 상태 해제
            subscriptionRepository.save(subscription);
            System.out.println(">>> 구독 상태 취소 완료: userId = " + subscriptionCanceled.getUserId());
        } else {
            System.out.println(">>> 해당 userId에 대한 구독 정보가 없습니다: " + subscriptionCanceled.getUserId());
        }
    }
}
// >>> Clean Arch / Inbound Adaptor
