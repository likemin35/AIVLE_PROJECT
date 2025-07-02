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

    // 출간 요청
    @PostMapping("/{bookId}/request-publish")
    public ResponseEntity<?> requestPublish(@PathVariable("bookId") Long bookId, @RequestBody RequestPublishCommand cmd) {
        Manuscript manuscript = manuscriptRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
        manuscript.requestPublish(cmd);  // isApprove 포함된 cmd 전달
        manuscriptRepository.save(manuscript);
        return ResponseEntity.ok().build();
    }

    // 원고 등록
    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterManuscriptCommand cmd) {
        Manuscript manuscript = new Manuscript();
        manuscript.setTitle(cmd.getTitle());
        manuscript.setContent(cmd.getContent());

    
        Boolean approve = cmd.getIsApprove(); 
        manuscript.setAuthorId(new AuthorId(cmd.getAuthorId(), approve != null ? approve : false));
        
        manuscript.setApprove(approve != null ? approve : true);  
    

        manuscript.setStatus(Status.WRITING);
        Manuscript saved = manuscriptRepository.save(manuscript);

        return ResponseEntity.ok(saved.getBookId()); 
    }

    // 원고 수정
    @PutMapping("/{bookId}")
    public ResponseEntity<?> updateManuscript(@PathVariable("bookId") Long bookId, @RequestBody RegisterManuscriptCommand cmd) {
        Manuscript manuscript = manuscriptRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
        manuscript.setTitle(cmd.getTitle());
        manuscript.setContent(cmd.getContent());
        manuscriptRepository.save(manuscript);
        return ResponseEntity.ok().build();
    }

    // 단건 조회
    @GetMapping("/{bookId}")
    public ResponseEntity<Manuscript> getManuscript(@PathVariable("bookId") Long bookId) {
        Manuscript manuscript = manuscriptRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
        return ResponseEntity.ok(manuscript);
    }

    // 삭제
    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteManuscript(@PathVariable("bookId") Long bookId) {
        if (!manuscriptRepository.existsById(bookId)) {
            return ResponseEntity.notFound().build();
        }
        manuscriptRepository.deleteById(bookId);
        return ResponseEntity.ok().build();
    }

    // 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<Manuscript>> getAllManuscripts() {
        List<Manuscript> manuscripts = manuscriptRepository.findAll();
        return ResponseEntity.ok(manuscripts);
    }

    // 제목 기반 검색
    @GetMapping("/search")
    public ResponseEntity<List<Manuscript>> getByTitle(@RequestParam String title) {
        List<Manuscript> result = manuscriptRepository.findByTitle(title);
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }
}
