package millie.infra;

import javax.transaction.Transactional;

import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publish")
@Transactional
public class PublishingController {

    @Autowired
    PublishingRepository publishingRepository;

    @Autowired
    AiClient aiClient;

    @PostMapping
    public Publishing createPublishing(@RequestBody Publishing publishing) throws Exception {
        System.out.println(">>> POST /publish called");
        // 1. 먼저 기본 정보 저장
        Publishing saved = publishingRepository.save(publishing);

        // 2. AI 를 통해 후처리
        String summary = aiClient.summarizeContent(saved.getContent(), saved.getCategory());

        String keywords = aiClient.extractKeywords(summary);
        
        String image = aiClient.generateCover(saved.getTitle(), saved.getCategory(), keywords);
        int cost = aiClient.predictBookPrice(
            saved.getTitle(),
            saved.getCategory(),
            false,
            0,
            saved.getContent()
        );

        
        saved.setSummaryContent(summary);
        saved.setKeywords(keywords);
        saved.setImage(image);
        saved.setCost(cost);

        
        return publishingRepository.save(saved);
    }
}
