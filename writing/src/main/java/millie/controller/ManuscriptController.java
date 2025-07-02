package millie.controller;

import millie.domain.AuthorId;
import millie.domain.Manuscript;
import millie.dto.ManuscriptResponse;
import millie.domain.ManuscriptRepository;
import millie.domain.RegisterManuscriptCommand;
import millie.domain.RequestPublishCommand;
import millie.domain.Status;
import millie.domain.AuthorStatus;
import millie.domain.AuthorStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manuscripts")  
public class ManuscriptController {

    @Autowired
    private ManuscriptRepository manuscriptRepository;

    @Autowired
    private AuthorStatusRepository authorStatusRepository;

    @GetMapping
    public List<ManuscriptResponse> getAllManuscripts() {
        List<Manuscript> manuscripts = manuscriptRepository.findAll();
        return manuscripts.stream()
            .map(manuscript -> {
                ManuscriptResponse dto = ManuscriptResponse.from(manuscript);
                Long authorId = manuscript.getAuthorId().getId();
                System.out.println("🔍 authorId: " + authorId);

                authorStatusRepository.findById(authorId).ifPresent(status -> {
                    dto.setApprove(status.getIsApprove());
                });
                return dto;
            })
            .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> registerManuscript(@RequestBody RegisterManuscriptCommand command) {
        try {
            Long authorId = Long.valueOf(command.getAuthorId());
            
            // ✅ 1단계: 작가 승인 여부 확인
            Optional<AuthorStatus> authorStatusOpt = authorStatusRepository.findById(authorId);
            
            if (authorStatusOpt.isEmpty()) {
                System.out.println("❌ 작가 정보 없음: authorId=" + authorId);
                return ResponseEntity.badRequest().body("등록되지 않은 작가입니다.");
            }
            
            AuthorStatus authorStatus = authorStatusOpt.get();
            System.out.println("🔍 작가 승인 상태: authorId=" + authorId + ", isApprove=" + authorStatus.getIsApprove());
            
            if (!Boolean.TRUE.equals(authorStatus.getIsApprove())) {
                System.out.println("❌ 승인되지 않은 작가: authorId=" + authorId);
                return ResponseEntity.badRequest().body("승인되지 않은 작가는 원고를 등록할 수 없습니다.");
            }
            
            // ✅ 2단계: 승인된 작가 → 원고 등록 허용
            System.out.println("✅ 승인된 작가 → 원고 등록 진행: authorId=" + authorId);
            
            Manuscript manuscript = new Manuscript();
            manuscript.setTitle(command.getTitle());
            manuscript.setContent(command.getContent());
            manuscript.setStatus(Status.WRITING);
            manuscript.setAuthorId(new AuthorId(authorId));

            manuscriptRepository.save(manuscript);

            ManuscriptResponse dto = ManuscriptResponse.from(manuscript);
            dto.setApprove(authorStatus.getIsApprove()); // 승인 상태 설정
            
            System.out.println("✅ 원고 등록 완료: bookId=" + manuscript.getBookId());
            return ResponseEntity.ok(dto);
            
        } catch (NumberFormatException e) {
            System.err.println("❌ 잘못된 authorId 형식: " + command.getAuthorId());
            return ResponseEntity.badRequest().body("잘못된 작가 ID 형식입니다.");
        } catch (Exception e) {
            System.err.println("❌ 원고 등록 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("원고 등록 중 오류가 발생했습니다.");
        }
    }

    // ✅ 출간 요청
    @PostMapping("/{bookId}/request-publish")
    public ResponseEntity<?> requestPublish(@PathVariable("bookId") Long bookId) {
        try {
            Manuscript manuscript = manuscriptRepository.findById(bookId)
                .orElse(null);

            if (manuscript == null) {
                System.out.println("❌ 출간 요청 실패 - 원고 없음: bookId=" + bookId);
                return ResponseEntity.badRequest().body("출간 요청할 원고를 찾을 수 없습니다.");
            }

            manuscript.setStatus(Status.DONE); // ✅ 고정값
           manuscriptRepository.save(manuscript);

            System.out.println("✅ 출간 요청 완료: bookId=" + bookId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("❌ 출간 요청 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("출간 요청 중 오류가 발생했습니다.");
        }
    }

    // ✅ 원고 수정
    @PutMapping("/{bookId}")
    public ResponseEntity<?> updateManuscript(
            @PathVariable("bookId") Long bookId,
            @RequestBody RegisterManuscriptCommand cmd) {
        try {
            Manuscript manuscript = manuscriptRepository.findById(bookId).orElse(null);

            if (manuscript == null) {
                System.out.println("❌ 수정 실패 - 원고 없음: bookId=" + bookId);
                return ResponseEntity.badRequest().body("수정할 원고를 찾을 수 없습니다.");
            }

            Long authorId = Long.valueOf(cmd.getAuthorId());
            Optional<AuthorStatus> authorStatusOpt = authorStatusRepository.findById(authorId);

            if (authorStatusOpt.isEmpty()) {
                System.out.println("❌ 작가 정보 없음 - authorId=" + authorId);
                return ResponseEntity.badRequest().body("등록되지 않은 작가입니다.");
            }

            AuthorStatus authorStatus = authorStatusOpt.get();

            if (!Boolean.TRUE.equals(authorStatus.getIsApprove())) {
                System.out.println("❌ 승인되지 않은 작가 - authorId=" + authorId);
                return ResponseEntity.badRequest().body("승인되지 않은 작가는 원고를 수정할 수 없습니다.");
            }

            manuscript.setTitle(cmd.getTitle());
            manuscript.setContent(cmd.getContent());
        
            manuscriptRepository.save(manuscript);

            System.out.println("✅ 원고 수정 완료: bookId=" + bookId);

            ManuscriptResponse dto = ManuscriptResponse.from(manuscript);
            dto.setApprove(authorStatus.getIsApprove());

            return ResponseEntity.ok(dto);

        } catch (NumberFormatException e) {
            System.err.println("❌ 잘못된 authorId 형식: " + cmd.getAuthorId());
            return ResponseEntity.badRequest().body("잘못된 작가 ID 형식입니다.");
        } catch (Exception e) {
            System.err.println("❌ 원고 수정 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("원고 수정 중 오류가 발생했습니다.");
        }
    }

    // ✅ 개별 원고 조회
    @GetMapping("/{bookId}")
    public ResponseEntity<?> getManuscript(@PathVariable("bookId") Long bookId) {
        try {
            Manuscript manuscript = manuscriptRepository.findById(bookId)
                .orElse(null);

            if (manuscript == null) {
                System.out.println("❌ 조회 실패 - 원고 없음: bookId=" + bookId);
                return ResponseEntity.badRequest().body("원고를 찾을 수 없습니다.");
            }

            ManuscriptResponse dto = ManuscriptResponse.from(manuscript);

            Long authorId = manuscript.getAuthorId().getId();
            System.out.println("🔍 authorId: " + authorId);

            authorStatusRepository.findById(authorId).ifPresent(status -> {
                dto.setApprove(status.getIsApprove());  // 여기서 주입
            });

            System.out.println("✅ 원고 조회 완료: bookId=" + bookId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.err.println("❌ 원고 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("원고 조회 중 오류가 발생했습니다.");
        }
    }

    // ✅ 원고 삭제
    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteManuscript(@PathVariable("bookId") Long bookId) {
        try {
            if (!manuscriptRepository.existsById(bookId)) {
                System.out.println("❌ 삭제 실패 - 원고 없음: bookId=" + bookId);
                return ResponseEntity.status(404).body("삭제할 원고를 찾을 수 없습니다.");
            }

            manuscriptRepository.deleteById(bookId);
            System.out.println("✅ 원고 삭제 완료: bookId=" + bookId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("❌ 원고 삭제 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("원고 삭제 중 오류가 발생했습니다.");
        }
    }
}