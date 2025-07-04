package millie.domain;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiClient aiClient;

    public AiController(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    
    @PostMapping("/summarize")
    public ResponseEntity<String> summarize(@RequestBody BookDto book) throws Exception {
        String summary = aiClient.summarizeContent(book.getContent(), book.getCategory());
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/cover")
    public ResponseEntity<String> cover(@RequestBody BookDto book) throws Exception {
        String imageUrl = aiClient.generateCover(book.getTitle(), book.getContent());

        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/price")
    public ResponseEntity<Integer> price(@RequestBody BookDto book) throws Exception {
        int price = aiClient.predictBookPrice(
                book.getTitle(),
                book.getCategory(),
                book.isBestSeller(),
                book.getViewCount(),
                book.getContent()
        );
        return ResponseEntity.ok(price);
    }
}
