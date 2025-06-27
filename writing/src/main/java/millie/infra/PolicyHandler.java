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
    ManuscriptRepository manuscriptRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='BookRegistered'"
    )
    public void wheneverBookRegistered_등록여부알림(
        @Payload BookRegistered bookRegistered
    ) {
        BookRegistered event = bookRegistered;
        System.out.println(
            "\n\n##### listener 등록여부알림 : " + bookRegistered + "\n\n"
        );
        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PublicationFailed'"
    )
    public void wheneverPublicationFailed_등록여부알림(
        @Payload PublicationFailed publicationFailed
    ) {
        PublicationFailed event = publicationFailed;
        System.out.println(
            "\n\n##### listener 등록여부알림 : " + publicationFailed + "\n\n"
        );
        // Sample Logic //

    }
}
//>>> Clean Arch / Inbound Adaptor
