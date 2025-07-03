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
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/manuscripts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // CORS 설정 추가
public class WritingController {

    private final ManuscriptRepository manuscriptRepository;

    // 출간 요청
    @PostMapping("/{bookId}/request-publish")
    public ResponseEntity<Map<String, Object>> requestPublish(@PathVariable("bookId") Long bookId, @RequestBody RequestPublishCommand cmd) {
        try {
            Manuscript manuscript = manuscriptRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
            manuscript.requestPublish(cmd);
            manuscriptRepository.save(manuscript);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "출판 신청이 완료되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "출판 신청 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 원고 등록
    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterManuscriptCommand cmd) {
        try {
            Manuscript manuscript = new Manuscript();
            manuscript.setTitle(cmd.getTitle());
            manuscript.setContent(cmd.getContent());

            Boolean approve = cmd.getIsApprove(); 
            manuscript.setAuthorId(new AuthorId(cmd.getAuthorId(), approve != null ? approve : false));
            manuscript.setApprove(approve != null ? approve : true);
            manuscript.setStatus(Status.WRITING);
            
            Manuscript saved = manuscriptRepository.save(manuscript);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "원고가 등록되었습니다.");
            response.put("data", saved);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "원고 등록 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 원고 수정
    @PutMapping("/{bookId}")
    public ResponseEntity<Map<String, Object>> updateManuscript(@PathVariable("bookId") Long bookId, @RequestBody RegisterManuscriptCommand cmd) {
        try {
            Manuscript manuscript = manuscriptRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("원고를 찾을 수 없습니다."));
            manuscript.setTitle(cmd.getTitle());
            manuscript.setContent(cmd.getContent());
            manuscriptRepository.save(manuscript);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "원고가 수정되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "원고 수정 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
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
    public ResponseEntity<Map<String, Object>> deleteManuscript(@PathVariable("bookId") Long bookId) {
        try {
            if (!manuscriptRepository.existsById(bookId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "원고를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            manuscriptRepository.deleteById(bookId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "원고가 삭제되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "원고 삭제 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<Manuscript>> getAllManuscripts() {
        List<Manuscript> manuscripts = manuscriptRepository.findAll();
        return ResponseEntity.ok(manuscripts);
    }
}