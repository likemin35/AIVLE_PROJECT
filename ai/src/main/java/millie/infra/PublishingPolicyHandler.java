package millie.infra;

import millie.config.kafka.KafkaProcessor;
import millie.domain.PublishingRequested;
import millie.domain.PublishingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PublishingPolicyHandler {

    @Autowired
    PublishingService publishingService;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPublishingRequested(@Payload PublishingRequested event) {
        if (!event.validate()) return;

        publishingService.publish(event);
    }
}
