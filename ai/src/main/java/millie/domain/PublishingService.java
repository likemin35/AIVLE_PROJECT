package millie.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PublishingService {

    private final PublishingRepository publishingRepository;
    private final AiClient aiClient;

    @Autowired
    public PublishingService(PublishingRepository publishingRepository, AiClient aiClient) {
        this.publishingRepository = publishingRepository;
        this.aiClient = aiClient;
    }

    public void publish(PublishingRequested event) {
        try {
            Publishing publishing = new Publishing();
            publishing.setBookName(event.getTitle());
            publishing.setAuthorId(event.getAuthorId().toString());
            publishing.setCategory(event.getCategory() != null ? event.getCategory() : "소설");
            publishing.setPdfPath("default.pdf");
            publishing.setWebUrl(event.getWebUrl() != null ? event.getWebUrl() : "https://millie.co.kr");

            // 수정된 aiClient 파라미터
            String summary = aiClient.summarizeContent(event.getContent());
            publishing.setSummaryContent(summary);

            String imageUrl = aiClient.generateCover(
                    event.getTitle(),
                    event.getAuthorId().toString()
            );
            publishing.setImage(imageUrl);

            int predictedCost = aiClient.predictBookPrice(
                    event.getTitle(),
                    publishing.getCategory(),
                    false,  // 베스트셀러 여부
                    0,      // 조회수
                    event.getContent()
            );
            publishing.setCost(predictedCost);


            publishingRepository.save(publishing);

            Published published = new Published(publishing);
            published.publishAfterCommit();

        } catch (Exception e) {
            Publishing failed = new Publishing();
            failed.setBookName(event.getTitle());
            failed.setAuthorId(event.getAuthorId().toString());
            failed.setCategory(event.getCategory() != null ? event.getCategory() : "소설");
            failed.setSummaryContent("요약 실패");
            failed.setImage("이미지 생성 실패");
            failed.setCost(1000);

            publishingRepository.save(failed);

            PublicationFailed failEvent = new PublicationFailed(failed);
            failEvent.publishAfterCommit();
        }
    }
}
