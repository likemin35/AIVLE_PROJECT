package millie.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import millie.config.kafka.KafkaProcessor;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import millie.domain.AuthorApproved;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    ManuscriptRepository manuscriptRepository;

    @Autowired
    private AuthorStatusRepository authorStatusRepository;

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

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='AuthorApproved'")
    public void wheneverAuthorApproved(@Payload AuthorApproved event) {
    System.out.println("✅ Received AuthorApproved event: " + event);

    AuthorStatus status = new AuthorStatus();
    status.setAuthorId(event.getAuthorId());
    status.setIsApprove(event.getIsApprove());

    authorStatusRepository.save(status);
    }
}
//>>> Clean Arch / Inbound Adaptor
