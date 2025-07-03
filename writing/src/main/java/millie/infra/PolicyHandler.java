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
import java.util.Map;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    ManuscriptRepository manuscriptRepository;

    @Autowired
    private AuthorStatusRepository authorStatusRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Map<String, Object> event = mapper.readValue(eventString, Map.class);
            String eventType = (String) event.get("eventType");

            // ✅ AuthorApproved 이벤트 처리
            if ("AuthorApproved".equals(eventType)) {
                System.out.println(">>> [수신] AuthorApproved 이벤트: " + event);
                
                Long authorId = Long.valueOf(event.get("authorId").toString());
                Boolean isApprove = (Boolean) event.get("isApprove");

                // AuthorStatus 테이블에 저장
                AuthorStatus status = new AuthorStatus();
                status.setAuthorId(authorId);
                status.setIsApprove(isApprove);
                authorStatusRepository.save(status);

                System.out.println(">>> AuthorStatus 저장 완료: authorId=" + authorId + ", isApprove=" + isApprove);
            }
            
            // ✅ AuthorDisApproved 이벤트 처리 추가
            else if ("AuthorDisApproved".equals(eventType)) {
                System.out.println(">>> [수신] AuthorDisApproved 이벤트: " + event);
                
                Long authorId = Long.valueOf(event.get("authorId").toString());
                Boolean isApprove = (Boolean) event.get("isApprove");

                // AuthorStatus 테이블 업데이트
                AuthorStatus status = new AuthorStatus();
                status.setAuthorId(authorId);
                status.setIsApprove(isApprove);
                authorStatusRepository.save(status);

                System.out.println(">>> AuthorStatus 업데이트 완료: authorId=" + authorId + ", isApprove=" + isApprove);
            }

        } catch (Exception e) {
            System.err.println(">>> [오류] 이벤트 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

    // ✅ 기존 조건부 핸들러는 제거하거나 주석처리
    /*
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='AuthorApproved'")
    public void wheneverAuthorApproved(@Payload AuthorApproved event) {
        System.out.println("✅ Received AuthorApproved event: " + event);

        AuthorStatus status = new AuthorStatus();
        status.setAuthorId(event.getAuthorId());
        status.setIsApprove(event.getIsApprove());

        authorStatusRepository.save(status);
    }
    */
}
//>>> Clean Arch / Inbound Adaptor