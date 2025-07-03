package millie.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import millie.AiApplication;
import millie.config.kafka.KafkaProcessor;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookEventHandler {

    private final ObjectMapper objectMapper;

    @StreamListener(KafkaProcessor.INPUT)
    public void handleBookEvent(String message) {
        log.info(">> [BookEventHandler] Received raw message: {}", message);

        try {
            // JSON → BookEvent 매핑
            BookEvent book = objectMapper.readValue(message, BookEvent.class);

            // 예제 모델 처리
            String image = generateImage(book.getTitle());
            String summary = summarizeContent(book.getContent());
            int cost = calculateCost(book.getTitle());

            // 처리 결과를 가공
            ProcessedBookEvent processed = new ProcessedBookEvent(
                    book.getTitle(),
                    book.getContent(),
                    book.getAuthorId(),
                    image,
                    summary,
                    cost
            );

            String processedJson = objectMapper.writeValueAsString(processed);

            // cloud stream 방식 (KafkaProcessor)
            AiApplication.applicationContext.getBean(KafkaProcessor.class)
                    .outboundTopic().send(
                            MessageBuilder
                                    .withPayload(processedJson)
                                    .setHeader("type", "ProcessedBookEvent")
                                    .build()
                    );

            log.info(">> [BookEventHandler] published processed data: {}", processedJson);

        } catch (JsonProcessingException e) {
            log.error(">> [BookEventHandler] JSON 파싱 오류", e);
        } catch (Exception e) {
            log.error(">> [BookEventHandler] 처리 중 오류", e);
        }
    }

    // 예제 가공 함수
    private String generateImage(String title) {
        return "https://example.com/cover/" + title + ".png";
    }

    private String summarizeContent(String content) {
        return "요약된 내용입니다.";
    }

    private int calculateCost(String title) {
        return 9900;
    }

    // ----------------------------
    // DTO를 내부 클래스로 정의
    // ----------------------------
    static class BookEvent {
        private String title;
        private String content;
        private String authorId;

        // getter/setter
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getAuthorId() { return authorId; }
        public void setAuthorId(String authorId) { this.authorId = authorId; }
    }

    static class ProcessedBookEvent {
        private String title;
        private String content;
        private String authorId;
        private String image;
        private String summaryContent;
        private int cost;

        public ProcessedBookEvent(String title, String content, String authorId,
                                  String image, String summaryContent, int cost) {
            this.title = title;
            this.content = content;
            this.authorId = authorId;
            this.image = image;
            this.summaryContent = summaryContent;
            this.cost = cost;
        }

        // getter/setter
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getAuthorId() { return authorId; }
        public void setAuthorId(String authorId) { this.authorId = authorId; }

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }

        public String getSummaryContent() { return summaryContent; }
        public void setSummaryContent(String summaryContent) { this.summaryContent = summaryContent; }

        public int getCost() { return cost; }
        public void setCost(int cost) { this.cost = cost; }
    }
}
