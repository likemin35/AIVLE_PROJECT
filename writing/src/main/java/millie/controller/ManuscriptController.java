package millie.controller;

import millie.domain.Manuscript;
import millie.dto.ManuscriptResponse;
import millie.domain.ManuscriptRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ManuscriptController {

    @Autowired
    private ManuscriptRepository manuscriptRepository;

    @GetMapping("/manuscripts")
    public List<ManuscriptResponse> getAllManuscripts() {
        List<Manuscript> manuscripts = manuscriptRepository.findAll();
        return manuscripts.stream()
                .map(ManuscriptResponse::from)
                .collect(Collectors.toList());
    }
}
