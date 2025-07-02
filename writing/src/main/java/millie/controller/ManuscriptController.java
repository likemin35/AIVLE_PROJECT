package millie.controller;

import millie.domain.AuthorId;
import millie.domain.Manuscript;
import millie.dto.ManuscriptResponse;
import millie.domain.ManuscriptRepository;
import millie.domain.RegisterManuscriptCommand;
import millie.domain.Status;
import millie.domain.AuthorStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public ManuscriptResponse registerManuscript(@RequestBody RegisterManuscriptCommand command) {
    Manuscript manuscript = new Manuscript();
    manuscript.setTitle(command.getTitle());
    manuscript.setContent(command.getContent());
    manuscript.setStatus(Status.WRITING);
    manuscript.setAuthorId(new AuthorId(Long.valueOf(command.getAuthorId())));

    manuscriptRepository.save(manuscript);

    ManuscriptResponse dto = ManuscriptResponse.from(manuscript);
    authorStatusRepository.findById(command.getAuthorId()).ifPresent(status -> {
        dto.setApprove(status.getIsApprove());
    });

    return dto;
}
}
