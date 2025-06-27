package millie.controller;

import lombok.RequiredArgsConstructor;
import millie.domain.Manuscript;
import millie.domain.ManuscriptRepository;
import millie.domain.RequestPublishCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manuscripts")
@RequiredArgsConstructor
public class WritingController {

    private final ManuscriptRepository manuscriptRepository;

    @PostMapping("/{id}/request-publish")
    public ResponseEntity<?> requestPublish(@PathVariable Long id, @RequestBody RequestPublishCommand cmd) {
        Manuscript manuscript = manuscriptRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
        manuscript.requestPublish(cmd);
        manuscriptRepository.save(manuscript);
        return ResponseEntity.ok().build();
    }
}
