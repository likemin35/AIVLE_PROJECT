package millie.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import millie.AiApplication;
import millie.domain.PublicationFailed;
import millie.domain.Published;

import javax.persistence.*;

@Entity
@Table(name = "Publishing_table")
@Data
public class Publishing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String image;             // AI 생성 표지 이미지 URL
    private String summaryContent;    // 요약 내용
    private String bookName;          // 제목
    private String pdfPath;           // PDF 경로
    private String authorId;          // 저자 ID
    private String webUrl;            // 웹 주소
    private String category;          // 장르 (소설, 에세이 등)
    private Integer cost;             // 가격 (연속형)

    @Embedded
    private ManuscriptId manuscriptId;

    // Repository 접근
    public static PublishingRepository repository() {
        return AiApplication.applicationContext.getBean(PublishingRepository.class);
    }

    // AI Client 접근
    private static final AiClient aiClient = AiApplication.applicationContext.getBean(AiClient.class);

    // 핵심 기능: publish()
    public static void publish(PublishingRequested event) {
        try {
            // 1. 새로운 Publishing 인스턴스 생성
            Publishing publishing = new Publishing();
            publishing.setBookName(event.getTitle());
            publishing.setAuthorId(event.getAuthorId().toString());
            publishing.setCategory(event.getCategory() != null ? event.getCategory() : "소설");
            publishing.setPdfPath("default.pdf"); // 추후 설정
            publishing.setWebUrl(event.getWebUrl() != null ? event.getWebUrl() : "https://millie.co.kr");

            // 2. 책 내용 요약
            String summary = aiClient.summarizeContent(
                event.getTitle(),
                event.getAuthorId().toString(),
                String.valueOf(event.getId()),
                publishing.getManuscriptId() != null ? publishing.getManuscriptId().getId() : "",
                publishing.getCost() != null ? publishing.getCost().toString() : "",
                event.getCategory(),
                event.getWebUrl(),
                event.getContent()
            );

            publishing.setSummaryContent(summary);

            // 3. 표지 이미지 생성
            String imageUrl = aiClient.generateCover(
                event.getTitle(),
                event.getAuthorId().toString(),
                publishing.getManuscriptId() != null ? publishing.getManuscriptId().getId() : "",
                String.valueOf(event.getId()),
                event.getCategory(),
                publishing.getCost() != null ? publishing.getCost().toString() : "",
                event.getWebUrl(),
                summary
            );
            publishing.setImage(imageUrl);

            // 4. 연속형 가격 예측 (AI 기반, 1000 ~ 10000)
            int predictedCost = aiClient.predictBookPrice(
                event.getTitle(),
                summary,
                publishing.getCategory(),
                event.getContent()
            );
            publishing.setCost(predictedCost);

            // 5. 저장 및 성공 이벤트 발행
            repository().save(publishing);
            Published published = new Published(publishing);
            published.publishAfterCommit();

        } catch (Exception e) {
            // 6. 실패 시 최소 정보만 포함한 객체 생성
            Publishing failed = new Publishing();
            failed.setBookName(event.getTitle());
            failed.setAuthorId(event.getAuthorId().toString());
            failed.setCategory(event.getCategory() != null ? event.getCategory() : "소설");
            failed.setSummaryContent("요약 실패");
            failed.setImage("이미지 생성 실패");
            failed.setCost(1000); // 기본값

            repository().save(failed);
            PublicationFailed failEvent = new PublicationFailed(failed);
            failEvent.publishAfterCommit();
        }
    }
}
