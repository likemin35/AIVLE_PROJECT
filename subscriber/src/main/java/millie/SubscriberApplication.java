package millie;

import millie.config.kafka.KafkaProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ApplicationContext;

import millie.domain.SubscriptionApplied;

@SpringBootApplication
@EnableBinding(KafkaProcessor.class)
@EnableFeignClients
public class SubscriberApplication {

    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext =
            SpringApplication.run(SubscriberApplication.class, args);
    


        SubscriptionApplied event = new SubscriptionApplied();
        event.setId(21L);
        event.setBookId(22L);
        event.setUserId(223L);
        event.setIsSubscription(false);
        event.setStartSubscription(null);
        event.setEndSubscription(null);
        event.setPdfPath("hi");
        event.publish();
    }
}
