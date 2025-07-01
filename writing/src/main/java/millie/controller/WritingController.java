package millie.controller;

import lombok.RequiredArgsConstructor;
import millie.domain.Manuscript;
import millie.domain.ManuscriptRepository;
import millie.domain.RequestPublishCommand;
import millie.domain.RegisterManuscriptCommand;
import millie.domain.AuthorId;
import millie.domain.Status;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/writing")  
@RequiredArgsConstructor
public class WritingController {

    private final ManuscriptRepository manuscriptRepository;

    @PostMapping("/{bookId}/request-publish")
    public ResponseEntity<?> requestPublish(@PathVariable("bookId") Long bookId, @RequestBody RequestPublishCommand cmd) {
        Manuscript manuscript = manuscriptRepository.findById(bookId)
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
        manuscript.setStatus(Status.WRITING);
        manuscriptRepository.save(manuscript);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{bookId}")
    public ResponseEntity<?> updateManuscript(@PathVariable("bookId") Long bookId, @RequestBody RegisterManuscriptCommand cmd) {
        Manuscript manuscript = manuscriptRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
        manuscript.setTitle(cmd.getTitle());
        manuscript.setContent(cmd.getContent());
        manuscriptRepository.save(manuscript);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Manuscript> getManuscript(@PathVariable("bookId") Long bookId) {
        Manuscript manuscript = manuscriptRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
        return ResponseEntity.ok(manuscript);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteManuscript(@PathVariable("bookId") Long bookId) {
        if (!manuscriptRepository.existsById(bookId)) {
            return ResponseEntity.notFound().build();
        }
        manuscriptRepository.deleteById(bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Manuscript>> getAllManuscripts() {
        List<Manuscript> manuscripts = manuscriptRepository.findAll();
        return ResponseEntity.ok(manuscripts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Manuscript>> getByTitle(@RequestParam String title) {
        List<Manuscript> result = manuscriptRepository.findByTitle(title);
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }
}
