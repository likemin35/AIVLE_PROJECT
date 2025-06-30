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

            String summary = aiClient.summarizeContent(
                    event.getTitle(),
                    event.getAuthorId().toString(),
                    String.valueOf(event.getId()),
                    "",
                    "",
                    event.getCategory(),
                    event.getWebUrl(),
                    event.getContent()
            );
            publishing.setSummaryContent(summary);

            String imageUrl = aiClient.generateCover(
                    event.getTitle(),
                    event.getAuthorId().toString(),
                    "",
                    String.valueOf(event.getId()),
                    event.getCategory(),
                    "",
                    event.getWebUrl(),
                    summary
            );
            publishing.setImage(imageUrl);

            int predictedCost = aiClient.predictBookPrice(
                    event.getTitle(),
                    summary,
                    publishing.getCategory(),
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
