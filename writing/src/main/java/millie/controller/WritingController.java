package millie.controller;

import lombok.RequiredArgsConstructor;
import millie.domain.Manuscript;
import millie.domain.ManuscriptRepository;
import millie.domain.RequestPublishCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import millie.domain.RegisterManuscriptCommand;
import millie.domain.AuthorId;
import millie.domain.Status;

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

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterManuscriptCommand cmd) {
        Manuscript manuscript = new Manuscript();
        manuscript.setTitle(cmd.getTitle());
        manuscript.setContent(cmd.getContent());
        manuscript.setAuthorId(new AuthorId(cmd.getAuthorId()));
        manuscript.setStatus(Status.WRITING); // 기본 상태 설정

        manuscriptRepository.save(manuscript);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}")
public ResponseEntity<?> updateManuscript(@PathVariable Long id, @RequestBody RegisterManuscriptCommand cmd) {
    Manuscript manuscript = manuscriptRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
    manuscript.setTitle(cmd.getTitle());
    manuscript.setContent(cmd.getContent());
    // 필요하면 상태도 변경 가능
    manuscriptRepository.save(manuscript);
    return ResponseEntity.ok().build();
}
@GetMapping("/{id}")
public ResponseEntity<Manuscript> getManuscript(@PathVariable Long id) {
    Manuscript manuscript = manuscriptRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
    return ResponseEntity.ok(manuscript);
}
@DeleteMapping("/{id}")
public ResponseEntity<?> deleteManuscript(@PathVariable Long id) {
    if (!manuscriptRepository.existsById(id)) {
        return ResponseEntity.notFound().build();
    }
    manuscriptRepository.deleteById(id);
    return ResponseEntity.ok().build();
}


}
