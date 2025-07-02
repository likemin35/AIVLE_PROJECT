package millie.controller;

import millie.domain.AuthorId;
import millie.domain.Manuscript;
import millie.dto.ManuscriptResponse;
import millie.domain.ManuscriptRepository;
import millie.domain.RegisterManuscriptCommand;
import millie.domain.Status;
import millie.domain.AuthorStatus;
import millie.domain.AuthorStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}